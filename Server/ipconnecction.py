#Conexion con camara IP

import cv2

print("1")
#cap = cv2.VideoCapture('rtsp://192.168.18.17:8080/h264_ulaw.sdp')
cap = cv2.VideoCapture('rtsp://192.168.18.17:8080/h264_pcm.sdp')
#VideoCapture:cap.open('rtsp://admin:1@192.168.18.51/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp')

#########cap = cv2.VideoCapture('http://192.168.18.17:8080/onvif/device_service')
#cap = cv2.VideoCapture("http://192.168.18.17:8080/browserfs.html") 
#cap = cv2.VideoCapture("http://ff972057153fbd6c:1234@192.168.18.51:8088/mjpeg.cgi?user=ff972057153fbd6c&password=1234&channel=0&.mjpg") 

#while (True):
print("2")
ret, frame = cap.read()
#print("3")
gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
#print("4")
cv2.imwrite('C:/Users/franm/Desktop/TFG/ProyectoGit/RaceStream/app/app/src/main/res/img.png',gray)
#print("5")
cap.release()
print("Fin")