import socket
from time import sleep

host = 'localhost'
port = 9998

#créer le socket qui va donner l’accès au spark streaming a traver le port 9998 dans le localhost :
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(1)

while True:
    print('\nEcoute de Spark streaming en : ',host , port)
    conn, addr = s.accept() #Ecoute de Spark streaming

    print('\nConnected by', addr)
    try:
        print('\nCommence lire le fichier log-generateur.log ...\n')
        with open('log-generator.log') as f:
            i=0
            for line in f:
                if i==0:
                    i=1
                    continue
                out = line.encode('utf-8')
                print('transmettre au spark Streaming la line : ',line)
                conn.send(out)
                sleep(1) #attendre un 1sec avant de continue le traitement
            print('End Of Stream.')
    except socket.error:
        print ('Error Occured.\n\nClient disconnected.\n')
conn.close()