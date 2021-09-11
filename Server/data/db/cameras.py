from sqlalchemy import Table, MetaData, Column, String  
from sqlalchemy.orm import mapper, relationship
from sqlalchemy.sql.sqltypes import Boolean, Integer 

class Camera():
    def __init__(self, id: int, name: str, ipaddres: str,  port:int):
        self.id: Integer = id
        self.name: str = name
        self.ipaddres: str = ipaddres
        self.port:int = port

    @classmethod
    def map(cls: type, metadata: MetaData) -> None:
        mapper(
            cls,
            Table(
                'cameras',
                metadata,
                Column('id', Integer, primary_key=True),
                Column('ipaddres', String(64), nullable=False),
                Column('name', String(16), unique=True, nullable=False),
                Column('port', Integer, unique=True, nullable=False)
            )
        )