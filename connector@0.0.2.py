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


class ResultType(Enum):
    RELATIONAL = auto()
    DOCUMENT = auto()
    DEPRECATED = auto()


@dataclass
class User:
    name: str
    password: str


class Token(User):
    name = "%TOKEN%"

    def __init__(self, token: str):
        self.password = token


class TableEntry:
    def get(self, field: str = None) -> ...:
        pass

    def is_present(self, field: str) -> bool:
        pass

    def is_null(self, field: str) -> bool:
        pass

    def creation(self) -> int:
        pass


class SimpleTableEntry(TableEntry):
    def __init__(self, content, creation):
        self.content = content
        self.creation = creation

    def get(self, field: str = None) -> ...:
        return self.content[field]

    def is_present(self, field) -> bool:
        return self.content[field] is not None

    def is_null(self, field: str) -> bool:
        return self.content[field] is None or self.content[field] == "null"

    def creation(self) -> int:
        return self.creation


class SingletonTableEntry(TableEntry):

    def __init__(self, content):
        self.content = content

    def get(self, field: str = None) -> ...:
        return self.content

    def is_present(self, field) -> bool:
        raise ConnectorError("Unsupported in singleton entry!")

    def is_null(self, field: str) -> bool:
        raise ConnectorError("Unsupported in singleton entry!")

    def creation(self) -> int:
        raise ConnectorError("Unsupported in singleton entry!")


@dataclass
class Response:
    type: ResponseType
    response: ...


class SimpleResponse(Response):
    def __init__(self, response: ..., exception: bool):
        self.type = ResponseType[response["type"]]
        self.response = response

        if exception:
            if type == ResponseType.ERROR:
                raise ConnectorError(response["exception"])
            elif type == ResponseType.FORBIDDEN:
                raise ConnectorError("You don't have the permissions to do that!")
            elif type == ResponseType.SYNTAX_ERROR:
                raise ConnectorError("Unknown syntax!")


class ErrorResult(SimpleResponse):
    def exception(self) -> str:
        return self.response["exception"]


class Result(SimpleResponse):
    def entries(self) -> List[TableEntry]:
        results = self.response["result"] if "result" in self.response else self.response["answer"]
        entries: List[TableEntry] = []

        for result in results:
            if result is str:
                entries.append(SingletonTableEntry(result))
            else:
                entries.append(SimpleTableEntry(result["content"], result["creation"]))

        return entries

    def structure(self) -> List[str]:
        return self.response["structure"]

    def resultType(self) -> ResultType:
        return ResultType[self.response["resultType"]] if self.response["resultType"] is None else ResultType.DEPRECATED


class Connection:
    def connect(self) -> None:
        pass

    def disconnect(self) -> None:
        pass

    def closeSession(self) -> None:
        pass

    def connected(self) -> bool:
        pass

    def query(self, query: str, exception: bool = True) -> Response:
        pass


class TokenConnection(Connection):

    def __init__(self, host: str, user: User, ignore_static_sessions: bool = True):
        self.host = host
        self.user = user
        self.ignore_static_sessions = ignore_static_sessions
        self.token = None

    @staticmethod
    def __send(host: str, payload) -> ...:
        try:
            return requests.post(host, json.dumps(payload)).json()
        except Exception as ex:
            raise ConnectorError(ex)

    def connect(self) -> None:
        if not self.host.startswith("http://") and not self.host.startswith("https://"):
            self.host = "http://" + self.host

        try:
            self.token = \
                self.__send(f"{self.host}/api/v1/session/open",
                            {"user": self.user.name, "password": self.user.password})[
                    "result"][0]
        except Exception as ex:
            raise ConnectorError("Connection failed!")

    def disconnect(self) -> None:
        self.token = None

    def closeSession(self) -> None:
        if self.user.name == "%TOKEN%" and self.ignore_static_sessions:
            self.disconnect()
            return

        self.__send(f"{self.host}/api/v1/session/close", {"token": self.token})
        self.disconnect()

    def connected(self) -> bool:
        return self.token is not None

    def query(self, query: str, exception: bool = True) -> Response:
        response = SimpleResponse(
            self.__send(f"{self.host}/api/v1/query", {"token": self.token, 'query': query}),
            exception)

        if response.type == ResponseType.RESULT:
            return Result(response.response, exception)
        elif response.type == ResponseType.ERROR:
            return ErrorResult(response.response, exception)
        else:
            return response
