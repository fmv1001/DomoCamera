#Conexion con camara IP

import cv2

#########cap = cv2.VideoCapture('http://192.168.18.17:8080/onvif/device_service')

print("1")
#cap = cv2.VideoCapture('rtsp://192.168.18.17:8080/h264_ulaw.sdp')
#cap = cv2.VideoCapture('rtsp://192.168.18.17:8080/h264_pcm.sdp')

cap = cv2.VideoCapture("rtsp://192.168.18.51:554/user=admin&password=1&channel=1&stream=0.sdp?") 

print("1.2")
print(cap.isOpened())

if (cap.isOpened()==False):
    cap.open("rtsp://192.168.18.51:80/user=admin&password=1&channel=1&stream=0.sdp?")
    print("2")
    print(cap.isOpened())

while (True):

    ret, frame = cap.read()

    print("3")
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    print("4")
    cv2.imwrite('img.png',gray)
    #cv2.imwrite('C:/Users/franm/Desktop/TFG/ProyectoGit/RaceStream/app/app/src/main/res/img.png',gray)

print("5")
cap.release()

print("Fin")