# JFQLConnector

With the [JFQLConnector](https://joker-games.org/documentation/connector/download) you can connect to the MyJFQL DMBS.
There is support for JavaScript, Python and Java. You can, however, write a connector yourself in another language such
as C #. The language only needs JSON support and an HTTP client.

### Java

```java

public class ConnectorExample {
    public static void main(String[] args) {
        //Create connection
        Connection connection = new Connection("http://localhost:2291/query", new User("root", "pw"));
        connection.connect();

        connection.query("CREATE DATABASE Example", false);
        connection.query("USE DATABASE Example");
        connection.query("CREATE TABLE Users STRUCTURE Name Password Email", false);

        //Select values
        Result result = connection.query("SELECT VALUE * FROM %", "Users");
        System.out.println(result);
    }
}

```

### Python

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

### License

All files on this repository are subject to the MIT license. Please read
the [LICENSE](https://github.com/joker-games/JFQLConnector/blob/master/LICENSE) file at the root of the project.

