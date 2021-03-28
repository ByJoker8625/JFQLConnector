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

    def query(self, query):
        jsonObject = {
            "auth": {
                "user": self.user.get_name(),
                "password": self.user.get_password()
            },
            "query": query
        }
        jsonObject = str(jsonObject)

        request = requests.post(self.format_host(self.host), jsonObject)
        return json.loads(request.text)

    def format_host(self, host):
        newHost = host

        if (host.startswith("myjfql:")):
            newHost = "http://" + host.replace("myjfql:", "") + ":2291/query"

        if (not host.startswith("http://") and not host.startswith("https://")):
            newHost = "http://" + host

        return newHost

    def get_user(self):
        return self.user

    def get_host(self):
        return self.host
