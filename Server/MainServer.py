""" Modulo de la clase servidor
"""

import socket
from CamConnex import ServerThreadForIpCam 
from schema import Schema
from cameras import Camera
from sqlalchemy.orm.session import Session
import time

class SocketServer():
    """ Clase responsable del funcionamiento del servidor
    """
    def __init__(self):
        # Create a TCP/IP socket
        self.__sock_recv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # Bind the socket to the port
        server_address = ('', 8888)
        print('starting up on {} port {}'.format(*server_address))
        self.__sock_recv.bind(server_address)

        # Database connect
        schema: Schema = Schema('sqlite:////tmp/database.db')
        self.__db_session: Session = schema.new_session()

        # Obtain camera Ip address from database
        self.__dir_cam1 = self.__db_session.query(Camera).get(0).ipaddres
        self.__dir_cam2 = self.__db_session.query(Camera).get(2).ipaddres
        
        self.__lista_camaras = []
        self.__main__()
        return
    
    def __main__(self):
        # Wait for an android client
        self.__sock_recv.listen(1)
        connection, client_address = self.__sock_recv.accept()
        print(client_address)
        app_client = connection.recv(1024)
        print("recibo: ",app_client)
        print("Conexion con cliente, conectado con cameras...")
        cam1 = ServerThreadForIpCam(9988, client_address, self.__dir_cam1)
        #cam2 = ServerThreadForIpCam(9999, client_address, self.__dir_cam2)
        self.__lista_camaras.append(cam1)
        #self.__lista_camaras.append(cam2)
        cam1.start()
        #cam2.start()

        while True:
            pass
        
        self.__sock_recv.close()
        
SocketServer()