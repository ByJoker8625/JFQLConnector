const Connection = function (host, user, password) {

    this.connect = function () {
        this.query("#connect", (res) => {
            if (res["type"] === "FORBIDDEN") {
                throw new Error("Connection failed!")
            }
        })
    }

    this.query = function (query, result) {
        const jsonObject = {
            name: user,
            password: password,
            query: query
        }

        const request = new XMLHttpRequest()

        if (host.startsWith("myjfql:")) {
            host = "http://" + host.replace("myjfql:", "") + ":2291/query"
        }

        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = "http://" + host
        }

        request.onreadystatechange = () => {
            const response = request.responseText

            if (response.startsWith('{') && response.endsWith('}')) {
                if (result !== null && result !== undefined)
                    result(JSON.parse(response))
            }
        }

        request.open('POST', host, false)
        request.send(JSON.stringify(jsonObject))
    }
}