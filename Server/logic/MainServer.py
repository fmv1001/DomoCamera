""" Modulo de la clase servidor
"""

import socket
import time
import sys, os

from sqlalchemy.sql.expression import false
from sqlalchemy.orm.session import Session
from sqlalchemy import delete, exc


SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))

from presentation.CamConnex import ServerThreadForIpCam
from data.db.schema import Schema
from data.db.cameras import Camera

class SocketServer():
    """ Clase responsable del funcionamiento del servidor
    """

    def __init__(self):
        self.__next_client_run = True
        # Create a TCP/IP socket
        self.__sock_recv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # Bind the socket to the port
        server_address = ('', 8888)
        print('starting up on {} port {}'.format(*server_address))
        self.__sock_recv.bind(server_address)

        # Database connect
        schema: Schema = Schema('sqlite:////tmp/database.db')
        self.__db_session: Session = schema.new_session()
        
        self.__lista_camaras = []
        self.__main__()
    
    def __main__(self):
        # Wait for an android client
        self.__sock_recv.listen(1)
        connection, client_address = self.__sock_recv.accept()
        self.__client_address = client_address
        print("Cliente conectado: ", client_address)

        cameras_string = "-"
        for instance in self.__db_session.query(Camera).order_by(Camera.name):
            cameras_string = (cameras_string + instance.name + "|" + instance.ipaddres + "|" + str(instance.port) + "-")
            cam_x = ServerThreadForIpCam(instance.name,instance.port, self.__client_address, instance.ipaddres)
            self.__lista_camaras.append(cam_x)
            cam_x.start()
        connection.send(cameras_string.encode())

        while self.__next_client_run:
            while True:
                try:
                    print("recibiendo nueva instruccion")
                    option = connection.recv(1)
                    if option.decode() == "0":
                        self.stop_server()
                        break
                    elif option.decode() == "1":
                        print("Añadiendo cámara")
                        new_cam = connection.recv(128)
                        camera_options = new_cam.decode().split('-')
                        print(camera_options)
                        self.add_camera(camera_options[0], camera_options[1], camera_options[2])
                    elif option.decode() == "2":
                        
                        del_cam = connection.recv(16)
                        print("Eliminando camara: ", del_cam.decode())
                        self.delete_camera(del_cam.decode())
                    elif option.decode() == "3":
                        stop_cam = connection.recv(16)
                        print("Parando cámara: ", stop_cam.decode())
                        self.stop_camera(stop_cam.decode())
                    elif option.decode() == "4":
                        start_c = connection.recv(16)
                        print("Enviando imágenes de cámara: ", start_c.decode())
                        self.start_camera(start_c.decode())
                    if option.decode() == "5":
                        print("Cliente ", client_address, " desconectado")
                        break
                    else:
                        self.error()
                except Exception as e:  
                    print(e)              
                    print("Error en la conexion, cerrando camaras...") 
                    for i in self.__lista_camaras:
                        print("Cerrando camara: ", i)
                        i.delete()
                    self.__sock_recv.close()
                    self.__next_client_run = False
                    break
            if (self.__next_client_run):
                self.__sock_recv.listen(1)
                connection, client_address = self.__sock_recv.accept()
                cameras_string = "-"
                for instance in self.__db_session.query(Camera).order_by(Camera.name):
                    cameras_string = (cameras_string + instance.name + "|" + instance.ipaddres + "|" + str(instance.port) + "-")
                connection.send(cameras_string.encode())
    
    def add_camera(self, name, ip_camera, port):
        ip = "rtsp://" + ip_camera + "/h264_ulaw.sdp"
        cam_x_data_base: Camera = Camera(22,name, ip, port)
        try:
            self.__db_session.add(cam_x_data_base)
            self.__db_session.commit()
        except exc.SQLAlchemyError as e:
            print(type(e))
            print("error añadiendo cámara")
            self.__db_session.rollback()
        cam_x = ServerThreadForIpCam(name,int(port), self.__client_address, ip)
        self.__lista_camaras.append(cam_x)
        cam_x.start()
    
    def delete_camera(self, name_camera):
        for i in self.__lista_camaras:
                if i.getName() == name_camera:
                    i.delete()
                    try:
                        sql1 = delete(Camera).where(Camera.name == name_camera)
                        self.__db_session.execute(sql1)
                        self.__db_session.commit()
                        print("Camara eliminada de la base de datos")
                    except exc.SQLAlchemyError as e:
                        print(e)
                        print("Error eliminando camaras de la bbdd")
                        self.__db_session.rollback()
                    self.__lista_camaras.remove(i)
                    return
    
    def stop_server(self):
        print("Cerrando servidor...")
        for i in self.__lista_camaras:
            print("Cerrando camara: ", i)
            i.delete()
        self.__next_client_run = False
        self.__lista_camaras.clear()
        self.__sock_recv.close()
        print("Hasta otra")

    def stop_camera(self, name_camera): 
        for i in self.__lista_camaras:
                if i.getName() == name_camera:
                    i.stop()
                    return

    def start_camera(self, name_camera): 
        for i in self.__lista_camaras:
                if i.getName() == name_camera:
                    i.start_cam()
                    return

    def error(self):
	    print("")
    
SocketServer()