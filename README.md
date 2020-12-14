# JFQLConnector

Mit dem JFQLConnector kannst du dich mein dem MyJFQL DMBS verbinden. Support gibt es für JavaScript, Python und Java. Du
kannst dir aber selbst einen Connector mit einer anderen sprache wie C# schreiben. Die sprache braucht lediglich JSON
Support und einen HTTP-Client.

### Java

```java
//Create connection
Connection connection = new Connection("http://localhost:2291/query",new User("root","pw"));
        connection.connect();

        connection.query("CREATE DATABASE Example",false);
        connection.query("USE DATABASE Example");
        connection.query("CREATE TABLE Users STRUCTURE Name Password Email",false);

//Select values
        Result result=connection.query("SELECT VALUE * FROM %","Users");
        System.out.println(result);
```

» [Connector.jar](http://jokergames.ddnss.de/lib/download/JFQLConnector.jar)

### Python:

```python
# Create connection
connection = Connection("http://localhost:2291/query", User("root", "pw"))

connection.query("CREATE DATABASE Example")
connection.query("USE DATABASE Example")
connection.query("CREATE TABLE Users STRUCTURE Name Password Email")

# Select values
result = connection.query("SELECT VALUE * FROM Users")
print(result)
```

» [connector.py](http://jokergames.ddnss.de/lib/download/connector.py)

### JavaScript

```javascript
//Create connection
var connection = new Connection('http://localhost:2291/query', 'root', 'pw')

connection.query('CREATE DATABASE Example')
connection.query('USE DATABASE Example')
connection.query('CREATE TABLE Users STRUCTURE Name Password Email')

//Select values
let response
connection.query('SELECT VALUE * FROM Users', (json) => response = json)
console.log(response)
```

» [connector.js](http://jokergames.ddnss.de/lib/download/connector.js)


