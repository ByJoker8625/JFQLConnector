var Connection = function (url, user, password) {

    this.query = function (query, result) {
        const jsonObject = {
            auth: {
                user: user,
                password: password
            },
            query: query
        }

        const request = new XMLHttpRequest();

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