from schema import Schema
from cameras import Camera
from sqlalchemy.orm.session import Session
from sqlalchemy.sql.sqltypes import Boolean 
from sqlalchemy import sql 


schema: Schema = Schema('sqlite:////tmp/database.db')
cam1: Camera = Camera(0, "camera 1","rtsp://192.168.0.29:8080/h264_pcm.sdp",9999,"False")
cam2: Camera = Camera(1, "camera 2","rtsp://192.168.18.5:8080/h264_pcm.sdp",9998,"False")
#cam3: Camera = Camera(2, "camera 3","rtsp://192.168.18.235:8080/h264_pcm.sdp",9997,"False")
#cam4: Camera = Camera(3, "camera 4","rtsp://192.168.18.51:554/user=admin&password=1&channel=1&stream=0.sdp?",9996,"False")
#cam5: Camera = Camera(4, "camera 5","rtsp://192.168.0.29:8080/h264_pcm.sdp",9995,"False")


db_session: Session = schema.new_session()

#db_session.execute('DROP TABLE IF EXISTS cameras;')

num_rows_deleted = 0

try:
    num_rows_deleted = db_session.query(Camera).delete()
    db_session.commit()
except:
    db_session.rollback()

print("Borradas: " + str(num_rows_deleted))

db_session.add_all([])
db_session.commit()

for instance in db_session.query(Camera).order_by(Camera.name):
    print(instance.id, instance.name, instance.ipaddres, instance.port, instance.state)

db_session.close()