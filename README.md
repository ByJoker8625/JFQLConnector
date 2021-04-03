# JFQLConnector

With the [JFQLConnector](https://joker-games.org/documentation/connector/download) you can connect to the MyJFQL DMBS.
There is support for JavaScript, Python and Java. You can, however, write a connector yourself in another language such
as C#. The language only needs JSON support and an HTTP client.

The example program listed here would log into a database which is located on port *2291* local on the server or computer. There it logs on with the user data *root* and *pw*. Then a table with the name *Users* is created there. All values are then read out from this and printed on the console.

### Java

```java

public class ConnectorExample {
    public static void main(String[] args) {
        //Create connection
        Connection connection = new Connection("http://localhost:2291/query", new User("root", "pw"));
        connection.connect();

        connection.query("CREATE TABLE Users STRUCTURE Name Password Email", false);

        //Select values
        Result result = connection.query("SELECT VALUE * FROM %", "Users");
        System.out.println(result);
    }
}

```

```xml
<repositories>
    <repository>
        <id>joker-games</id>
        <url>https://joker-games.org/repository</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.joker-games</groupId>
        <artifactId>JFQLConnector</artifactId>
        <version>1.1</version>
    </dependency>
</dependencies>
```

### Python

```python
import connector

# Create connection
connection = connector.Connection("http://localhost:2291/query", User("root", "pw"))

connection.query("CREATE TABLE Users STRUCTURE Name Password Email")

# Select values
result = connection.query("SELECT VALUE * FROM Users")
print(result)
```

### JavaScript

```javascript
//Create connection
var connection = new Connection('http://localhost:2291/query', 'root', 'pw')

connection.query('CREATE TABLE Users STRUCTURE Name Password Email')

//Select values
let response
connection.query('SELECT VALUE * FROM Users', (json) => response = json)
console.log(response)
```

### License

All files on this repository are subject to the MIT license. Please read
the [LICENSE](https://github.com/joker-games/JFQLConnector/blob/master/LICENSE) file at the root of the project.

