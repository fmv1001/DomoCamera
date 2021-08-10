""" Modulo de la clase servidor
"""

import socket
from CamConnex import ServerThreadForIpCam 
from schema import Schema
from cameras import Camera
from sqlalchemy.orm.session import Session
from sqlalchemy import delete
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
        
        self.__lista_camaras = []
        self.__main__()
        return
    
    def __main__(self):
        # Wait for an android client
        self.__sock_recv.listen(1)
        connection, client_address = self.__sock_recv.accept()
        self.__client_address = client_address
        print(client_address)
        rcv = connection.recv(1024)
        print("recibo: ",rcv.decode())

        camerasString = "-"
        for instance in self.__db_session.query(Camera).order_by(Camera.name):
            camerasString = (camerasString + instance.name + "|" + instance.ipaddres + "|" + str(instance.port) + "-")
            print(camerasString)
            camX = ServerThreadForIpCam(instance.name,instance.port, self.__client_address, instance.ipaddres)
            self.__lista_camaras.append(camX)
            camX.start()
        connection.send(camerasString.encode())

        while True:
            try:
                print("recibiendo nueva instruccion")
                #connection.send(b"True")
                option = connection.recv(1)
                print("reciboOption: ", option.decode())
                #self.__switch_options.get(option.decode(), self.error)()
                if option.decode() == "0":
                    print("op0")
                    self.stopServer()
                    break
                elif option.decode() == "1":
                    print("op1")
                    newC = connection.recv(128)
                    print("reciboNew: ", newC.decode())
                    cameraOptions = newC.decode().split('-')
                    print(cameraOptions)
                    self.addCamera(cameraOptions[0], cameraOptions[1], cameraOptions[2])
                elif option.decode() == "2":
                    print("op2")
                    delC = connection.recv(16)
                    print("reciboDel: ", delC.decode())
                    self.deleteCamera(delC.decode())
                elif option.decode() == "3":
                    print("op3")
                    stopC = connection.recv(16)
                    print("reciboStop: ", stopC.decode())
                    self.stopCamera(stopC.decode())
                elif option.decode() == "4":
                    print("op4")
                    startC = connection.recv(16)
                    print("reciboStart: ", startC.decode())
                    self.startCamera(startC.decode())
            except:                
                print("Error en la conexion, cerrando camaras...")
                for i in self.__lista_camaras:
                    print("Cerrando camara: ", i)
                    i.delete()
                self.__sock_recv.close()
                break
    
    def addCamera(self, name, ip_camera, port):
        try:
            ip = "rtsp://" + ip_camera + "/h264_ulaw.sdp"
            camX_data_base: Camera = Camera(22,name, ip, port,"false")
            try:
                self.__db_session.add(camX_data_base)
                self.__db_session.commit()
            except:
                self.__db_session.rollback()
            camX = ServerThreadForIpCam(name,int(port), self.__client_address, ip)
            self.__lista_camaras.append(camX)
            camX.start()
        except:
            print("Error añadiendo camaras")
        return
    
    def deleteCamera(self, name_camera):
        try:
            print("Numero de camaras: ", self.__lista_camaras)
            for i in self.__lista_camaras:
                    if i.getName() == name_camera:
                        i.delete()
                        try:
                            sql1 = delete(Camera).where(Camera.name == name_camera)
                            self.__db_session.execute(sql1)
                            self.__db_session.commit()
                            print("Camara eliminada de la base de datos")
                        except:
                            print("Error eliminando camaras de la bbdd")
                            self.__db_session.rollback()
                        self.__lista_camaras.remove(i)
                        print("Camara eliminada de la lista del servidor")
                        print("Numero de camaras final: ", self.__lista_camaras)
                        return
        except:
            print("Error eliminando camaras")
        return
    
    def stopServer(self):
        print("Cerrando servidor...")
        for i in self.__lista_camaras:
            print("Cerrando camara: ", i)
            i.delete()
        self.__lista_camaras.clear()
        self.__sock_recv.close()
        print("Hasta otra")
        return

    def stopCamera(self, name_camera): #----------------------------------------------
        try:
            for i in self.__lista_camaras:
                    if i.getName() == name_camera:
                        i.stop()
                        return
        except:
            print("Error parando camara")
        return

    def startCamera(self, name_camera): #----------------------------------------------
        try:
            for i in self.__lista_camaras:
                    if i.getName() == name_camera:
                        i.startCamera()
                        return
        except:
            print("Error iniciando camara")
        return

    def error(self):
	    print('error al recibir accion')

    __switch_options = {
	"0": stopServer,
	"1": addCamera,
    "2": deleteCamera,
    "3": stopCamera,
    "4": startCamera
    }
        
SocketServer()