const axios = require("axios")

const runtime = {
    send: async function (url, request) {
        try {
            return await axios({
                method: "POST",
                url: url,
                data: request
            }).then(response => {
                return response.data
            })
        } catch (ex) {
            throw new Error(ex)
        }
    }, temp: {}
}

module.exports = {
    connect: async function (host, user) {
        if (!host.startsWith("http://") && !host.startsWith("https://"))
            host = "http://" + host

        try {
            runtime.temp.token = await runtime.send(host + "/api/v1/session/open", {
                user: user.name,
                password: user.password
            }).then(data => data.result[0])
        } catch (ex) {
            console.error(ex)
            throw new Error("Connection failed!")
        }

        runtime.temp.user = user
        runtime.temp.host = host

        if (user.database !== undefined)
            await this.query("use database " + user.database)
    },
    disconnect: async function () {
        if (!this.connected)
            throw new Error("Client isn't connected!")

        await runtime.send(runtime.temp.host + "/api/v1/session/close", {token: runtime.temp.token})
        delete runtime.temp.token

    }, query: async function (query, exceptions = true) {
        if (!this.connected)
            throw new Error("Client isn't connected!")

        const response = await runtime.send(runtime.temp.host + "/api/v1/query", {
            token: runtime.temp.token,
            query: query
        }).then(data => data)

        if (exceptions) {
            if (response == null)
                throw new Error("Empty response!")

            if (response.type === "ERROR") {
                throw new Error(response.exception)
            }

            if (response.type === "FORBIDDEN") {
                throw new Error("You don't have the permissions to do that!")
            }

            if (response.type === "SYNTAX_ERROR") {
                throw new Error("Syntax error!")
            }
        }

        return {
            columns: function () {
                if (this.type !== "RESULT") {
                    throw new Error("Response isn't of type 'RESULT'!")
                }

                const columns = []
                const results = response.result

                for (const index in results) {
                    const result = results[index]

                    if (result["content"] === undefined) {
                        columns.push({
                            get: function () {
                                if (result === "null")
                                    return null

                                return result
                            },
                            getNumber: function () {
                                return Number(result)
                            },
                            getBoolean: function () {
                                return result === "true"
                            },
                            getJson: function () {
                                return JSON.parse(result)
                            },
                            isNull: function () {
                                return result === "null"
                            }
                        })
                    } else {
                        const content = result.content

                        columns.push({
                            get: function (key) {
                                if (content[key] == null || content[key] === "null")
                                    return null

                                return content[key]
                            },
                            getNumber: function (key) {
                                return parseInt(this.get(key))
                            },
                            getBoolean: function (key) {
                                return this.get(key) === "true"
                            },
                            getJson: function (key) {
                                return JSON.parse(this.get(key))
                            },
                            isNull: function (key) {
                                return this.get(key) == null || key === "null"
                            },
                            creation: result.creation
                        })
                    }
                }

                return columns
            },
            structure: function () {
                if (this.type !== "RESULT") {
                    throw new Error("Response isn't of type 'RESULT'!")
                }

                return response.structure
            },
            response: response,
            type: response.type
        }
    },
    connected: function () {
        return runtime.temp.token != null
    },
    tokenOf: function (token) {
        return {
            name: "%TOKEN%",
            password: token
        }
    }
}