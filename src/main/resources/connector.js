const Connection = function (url, user, password) {

    this.query = function (query, result) {
        const jsonObject = {
            name: user,
            password: password,
            query: query
        }

        const request = new XMLHttpRequest();

        if (url.startsWith("myjfql:")) {
            url = "http://" + url.replace("myjfql:", "") + ":2291/query"
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url
        }

        request.onreadystatechange = () => {
            var response = request.responseText

            if (response.startsWith('{') && response.endsWith('}')) {
                if (result !== null && result !== undefined)
                    result(JSON.parse(response))
            }
        };

        request.open('POST', url, false)
        request.send(JSON.stringify(jsonObject))
    }
}