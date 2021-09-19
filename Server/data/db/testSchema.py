from sqlalchemy.orm.session import Session
from sqlalchemy.sql.sqltypes import Boolean 
from sqlalchemy import sql, exc
import sys, os

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))
from schema import Schema
from cameras import Camera



schema: Schema = Schema('sqlite:////tmp/database.db')

db_session: Session = schema.new_session()

#db_session.execute('DROP TABLE IF EXISTS cameras;')


num_rows_deleted = 0

try:
    num_rows_deleted = db_session.query(Camera).delete()
    db_session.commit()
except exc.SQLAlchemyError as e:
    print(type(e))
    db_session.rollback()

print("Borradas: " + str(num_rows_deleted))

db_session.add_all([])
db_session.commit()

for instance in db_session.query(Camera).order_by(Camera.name):
    print(instance.id, instance.name, instance.ipaddres, instance.port)

db_session.close()