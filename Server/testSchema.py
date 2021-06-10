from schema import Schema
from cameras import Camera
from sqlalchemy.orm.session import Session
from sqlalchemy.sql.sqltypes import Boolean 
from sqlalchemy import sql 


schema: Schema = Schema('sqlite:////tmp/database.db')
cam1: Camera = Camera(0,"rtsp://192.168.18.185:8080/h264_pcm.sdp", "camera 1","False")
cam2: Camera = Camera(1,"rtsp://192.168.18.61:8080/h264_pcm.sdp", "camera 2","False")
cam3: Camera = Camera(2,"rtsp://192.168.18.235:8080/h264_pcm.sdp", "camera 3","False")

db_session: Session = schema.new_session()

#db_session.execute('DROP TABLE IF EXISTS cameras;')

num_rows_deleted = 0

print(db_session.query(Camera).get(0).ipaddres)

try:
    num_rows_deleted = db_session.query(Camera).delete()
    db_session.commit()
except:
    db_session.rollback()

print("Borradas: " + str(num_rows_deleted))

db_session.add_all([cam1, cam2, cam3])
db_session.commit()

for instance in db_session.query(Camera).order_by(Camera.name):
    print(instance.id, instance.name, instance.ipaddres, instance.state)

db_session.close()