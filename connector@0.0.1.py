import json
from dataclasses import dataclass
from enum import *
from typing import List

import requests


class ConnectorError(Exception):
    pass


class ResponseType(Enum):
    ERROR = auto()
    RESULT = auto()
    FORBIDDEN = auto()
    SUCCESS = auto()
    SYNTAX_ERROR = auto()


@dataclass
class User:
    name: str
    password: str
    database: str or None = None


class Token(User):
    name = "%TOKEN%"

    def __init__(self, token):
        self.password = token
        self.database = None


class Column:

    def __init__(self, content, singleton=False) -> None:
        self.singleton = singleton
        self.content = content

        if singleton and not content is str:
            raise ConnectorError("Column isn't a singleton column!")

    def get(self, key=None):
        if self.singleton and not key is None:
            raise ConnectorError("Singleton column has no keys!")

        return self.content if key is None else self.content["content"][key]

    def is_null(self, key=None):
        if self.singleton and not key is None:
            raise ConnectorError("Singleton column has no keys!")

        return self.content is None or self.content == "null" if key is None else self.get(key) is None or self.get(
            key) == "null"

    def creation(self):
        if self.singleton:
            raise ConnectorError("Singleton column has no creation variable!")

        return self.content["creation"]

    def __str__(self) -> str:
        if self.singleton:
            return str(self.content)

        return json.dumps(self.content)


class Result:
    def __init__(self, response, exceptions=True) -> None:
        self.response = response

        if not exceptions:
            return

        if response is None:
            raise ConnectorError("Emtpy response!")

        responseType: ResponseType = ResponseType[response["type"]]

        if responseType is ResponseType.ERROR:
            raise ConnectorError(response["exception"])

        if responseType is ResponseType.FORBIDDEN:
            raise ConnectorError("You don't have the permissions to do that!")

        if responseType is ResponseType.SYNTAX_ERROR:
            raise ConnectorError("Syntax error!")

    def columns(self) -> List[Column]:
        if not self.type() is ResponseType.RESULT:
            raise ConnectorError("Response isn't of type 'RESULT'!")

        columns: list = []
        results = self.response["result"] if "result" in self.response else self.response["answer"]

        for result in results:
            if result is str:
                columns.append(Column(result, True))
            else:
                columns.append(Column(result))

        return columns

    def type(self) -> ResponseType:
        return ResponseType[self.response["type"]]

    def structure(self) -> List[str]:
        if not self.type() is ResponseType.RESULT:
            raise ConnectorError("Response isn't of type 'RESULT'!")

        return self.response["structure"]

    def response(self):
        return self.response

    def __str__(self) -> str:
        return json.dumps(self.response)


class Connection:
    def connect(self, host: str = None, user: User = None) -> None:
        pass

    def disconnect(self) -> None:
        pass

    def connected(self) -> bool:
        pass

    def query(self, query: str, exceptions: bool = True) -> Result:
        pass


class JFQLConnection(Connection):

    def __init__(self, host: str = None, user: User = None) -> None:
        self.host = host
        self.user = user
        self.token = None

    def exec(self, query: str, exceptions: bool = True):
        try:
            return requests.post(self.host,
                                 json={"name": self.user.name, "password": self.user.password, "query": query}).json()
        except Exception as ex:
            if exceptions:
                raise ConnectorError(ex)

    def connect(self, host: str = None, user: User = None) -> None:
        host = host if not host is None else self.host
        user = user if not user is None else self.user

        if not host.startswith("http://") and not host.startswith("https://"):
            host = "myjfql:" + host

        if host.startswith("myjfql:"):
            host = "http://" + host.replace("myjfql:", "") + ":2291/query"

        self.host = host

        if self.exec("#connect")["type"] == "FORBIDDEN":
            raise ConnectorError("Connection failed!")

        if not user.database is None:
            self.query(f"use database '{user.database}'")

    def disconnect(self) -> None:
        if not self.connected():
            raise ConnectorError("Client isn't connected!")

        self.connected = False

    def connected(self) -> bool:
        return self.connected

    def query(self, query: str, exceptions: bool = True) -> Result:
        if not self.connected:
            raise ConnectorError("Client isn't connected!")

        return Result(self.exec(query, exceptions), exceptions)


class TokenConnection(Connection):

    def __init__(self, host: str = None, user: User = None) -> None:
        if not host is None and not host.startswith("http://") and not host.startswith("https://"):
            host = "http://" + host

        self.host = host
        self.user = user
        self.token = None

    def send(self, url: str, request):
        try:
            return requests.post(url, json=request).json()
        except Exception as ex:
            raise ConnectorError(ex)

    def connect(self, host: str = None, user: User = None) -> None:
        host = host if not host is None else self.host
        user = user if not user is None else self.user

        if not host.startswith("http://") and not host.startswith("https://"):
            host = "http://" + host

        try:
            self.token = \
                self.send(host + "/api/v1/session/open", {"user": user.name, "password": user.password})["result"][0]
        except Exception:
            raise ConnectorError("Connection failed!")

        if not user.database is None:
            self.query(f"use database '{user.database}'")

    def disconnect(self) -> None:
        if not self.connected():
            raise ConnectorError("Client isn't connected!")

        self.send(self.host + "/api/v1/session/close", {"token": self.token})
        self.token = None

    def connected(self) -> bool:
        return self.token is not None

    def query(self, query: str, exceptions: bool = True) -> Result:
        if not self.connected():
            raise ConnectorError("Client isn't connected!")

        return Result(self.send(self.host + "/api/v1/query", {"token": str(self.token), "query": query}), exceptions)
