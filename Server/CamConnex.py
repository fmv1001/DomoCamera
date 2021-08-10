from threading import Thread
import cv2
import socket
import time

class ServerThreadForIpCam(Thread):
    """ Clase responsable del envio de imagenes de la camara
    """
    def __init__(self,nameC, port, app_client, cam_dir, group=None, target=None, name=None, kwargs=None, *, daemon=None):
        """ Initialization/constructor method.
        """

        super().__init__(group=group, target=target, name=name,
                         daemon=daemon)
        print("iniciando: ", cam_dir)
        self.__sock_send = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.__runnig = True
        self.__stop = False
        self.__cam_dir = cam_dir
        self.__capture = cv2.VideoCapture(cam_dir)
        self.__name = nameC
        if (self.__capture.isOpened()):
            print("Conexion con cámara establecida")
        else:
            print("Conexion con cámara fallida")
            self.__runnig = False
        self.__app_client = app_client
        self.__img_counter = 0
        self.__port = port

        return
    def run(self):
        print('sending... to ', self.__app_client[0])
        self.__runnig = False
        while True:
            time.sleep(5)
            while self.__runnig:
                print("frame ", self.__img_counter)
                ret, frame = self.__capture.read()
                if not ret:
                    print("error en lectura de frame en camra: ", self.__cam_dir)
                    break

                # Frame resize
                frame = cv2.resize(frame, (1000,700))

                # Frame serialize
                data = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY),5])[1]

                # Sending the frame
                try:
                    self.__sock_send.sendto(data, (self.__app_client[0],self.__port))
                except:
                    print("ocurrio un error al enviar imagen, paramos ejecucion")
                    self.__runnig = False
                self.__img_counter += 1
            if (self.__stop):
                break
        print("saliendo en: ", self.__cam_dir)
            #self.__capture.release()
        
        return

    def stop(self):
        print("parando : ", self.__cam_dir)
        self.__runnig = False
        #self.__sock_send.close()

    def startCamera(self):
        #self.__sock_send = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        print("reiniciando : ", self.__cam_dir)
        self.__runnig = True
    
    def delete(self):
        print("eliminando : ", self.__cam_dir)
        self.__stop = True
        self.__runnig = False
        self.__sock_send.close()
        self.__capture.release()

    def getName(self) -> str:
        return self.__name
