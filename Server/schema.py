from sqlalchemy import create_engine 
from sqlalchemy.ext.declarative import declarative_base   
from sqlalchemy.orm import sessionmaker 
from sqlalchemy.orm.session import Session 
from cameras import Camera

class Schema():
    def __init__(self, db_connection_string: str):
        self.__declarative_base = declarative_base()
        self.__create_engine = create_engine(db_connection_string)
        self.__session_maker = sessionmaker(bind=self.__create_engine)

        Camera.map(self.__declarative_base.metadata)
        self.__declarative_base.metadata.create_all(self.__create_engine)

    def new_session(self) -> Session:
        return self.__session_maker()