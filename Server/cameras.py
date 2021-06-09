from sqlalchemy import Table, MetaData, Column, String  
from sqlalchemy.orm import mapper, relationship
from sqlalchemy.sql.sqltypes import Boolean, Integer 

class Camera():
    def __init__(self, id: int, ipaddres: str, name: str, state: str):
        self.id: Integer = id
        self.ipaddres: str = ipaddres
        self.name: str = name
        self.state: str = state

    @classmethod
    def map(cls: type, metadata: MetaData) -> None:
        mapper(
            cls,
            Table(
                'cameras',
                metadata,
                Column('id', Integer, primary_key=True),
                Column('ipaddres', String(64), nullable=False),
                Column('name', String(16), nullable=False),
                Column('state', String(8), nullable=False)
            )
        )