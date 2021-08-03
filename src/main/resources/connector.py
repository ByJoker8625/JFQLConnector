import json
import requests

class User:

    def __init__(self, name, password):
        self.name = name
        self.password = password

    def set_name(self, name):
        self.name = name

    def set_password(self, password):
        self.password = password

    def get_name(self):
        return self.name

    def get_password(self):
        return self.password


class Connection:

    def __init__(self, host, user):
        self.host = host
        self.user = user

    def connect(self):
        result = self.query("#connect")

        if result["type"] == "FORBIDDEN":
            raise Exception("Connection failed!")

    def query(self, query):
        json_object = {
            "name": self.user.get_name(),
            "password": self.user.get_password(),
            "query": query
        }
        json_object = str(json_object)

        request = requests.post(self.format_host(), json_object)
        return json.loads(request.text)

    def format_host(self):
        new_host = self.host

        if self.host.startswith("myjfql:"):
            new_host = "http://" + self.host.replace("myjfql:", "") + ":2291/query"

        if not new_host.startswith("http://") and not new_host.startswith("https://"):
            new_host = "http://" + self.host

        return new_host

    def get_user(self):
        return self.user

    def get_host(self):
        return self.host
