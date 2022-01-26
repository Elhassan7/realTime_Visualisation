import org.apache.spark.sql.SparkSession
import org.elasticsearch.hadoop.cfg.ConfigurationOptions


object readSocket {
  def main(args: Array[String]): Unit = {

    //cree spark seesion en le configurant avec elasticsearch :
    val sparkSession = SparkSession.builder()
      //configure elasticsearch avec spark
      .config(ConfigurationOptions.ES_NET_HTTP_AUTH_USER, "elasticsearch")
      .config(ConfigurationOptions.ES_NET_HTTP_AUTH_PASS, "")
      .config(ConfigurationOptions.ES_NODES, "localhost")
      .config(ConfigurationOptions.ES_PORT, "9200")
      .master("local[*]")
      .appName("Mediateur")
      .getOrCreate()


    //capter les donnes de socket qui vient de script Stream.py
    import sparkSession.implicits._
    val logValue = sparkSession
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", "9998")
      .load()
      .as[String]


    //créer un base de donnees dans spark SQL en le nommé par "SDF"
    logValue.createOrReplaceTempView("SDF")

    //recuper les donnees de SDF, qui est une base de donnees avec un seul colonne "value",
    // chaque cellule de colonne value contient une line de fichier Log-generateur.log
    val outDF = sparkSession.sql("select * from SDF")

    //construire un nouveau dataset ouDF1,
    // à partir de dataset outDF, on separerant la colonne "value"  en 5 colonne: HTTP, Port, Url, Path, Ip
    import org.apache.spark.sql.functions.split
    val outDF1= outDF.withColumn("_tmp", split($"value", "\\ ")).select(
      $"_tmp".getItem(0).as("HTTP"),
      $"_tmp".getItem(1).as("Port"),
      $"_tmp".getItem(2).as("Url"),
      $"_tmp".getItem(3).as("Path"),
      $"_tmp".getItem(4).as("Ip")
    )

    //C'est juste pour afficher le resultat de dataset outDF1 dans le console
    /*utDF1.writeStream.format("console")
      .outputMode("append")
      .start()
      .awaitTermination()*/

    // transmettre le dataset OutDF1 avec spark streaming au elasticsearch
    outDF1.writeStream
    .format("org.elasticsearch.spark.sql")
    .outputMode("append")
    .option("checkpointLocation", "path-to-checkpointing")
    .start("logfile/stream").awaitTermination()

  }

}
