# JFQLConnector

With the JFQLConnector you can connect to the MyJFQL DMBS. There is support for JavaScript, Python and Java. You can,
however, write a connector yourself in another language such as C#. The language only needs JSON support and an HTTP
client.

### Java

If you want to use the Java version of the JFQLConnector click
on [download](https://joker-games.org/documentation/connector/java/) to download it.

```java

import org.jokergames.jfql.connection.Connection;
import org.jokergames.jfql.connection.MyJFQLConnectionection;
import org.jokergames.jfql.util.Column;
import org.jokergames.jfql.util.Result;
import org.jokergames.jfql.util.User;

import java.util.List;

public class JFQLProjectExample {


    public static void main(String[] args) {
        /**
         * If you want to connect your application to a MyJFQL database you have to create a connection with the Connection class.
         * You can enter your connection information like your host, username and password in the constructor of the Connection or in the connect method.
         * Below is an example for both cases:
         */

        final Connection connection = new Connection("myjfql:your-host.com", new User("your-user-name", "your-user-password"));
        connection.connect();

        /**
         * Or you enter the information in the connect method (this could be helpful so that to connection object can't produce a NullPointerException)
         */

        final Connection connection = new Connection();
        connection.connect("myjfql:your-host.com", new User("your-user-name", "your-user-password"));

        /**
         * What you also can do is to only enter the host or the user in the constructor and the connect method, like this:
         */

        final Connection connection = new Connection("myjfql:your-host.com");
        connection.connect(new User("your-user-name", "your-user-password"));


        /**
         * If a connection has been successfully established, commands can be sent to the database.
         * You can to this in two ways: you send a query and don't use the response, or you use the response.
         *
         * In this first case a query will be sent to the database. The JFQLConnector has some extras to simplify this for you.
         */

        connection.query("create table Example structure Column1 Column2 Column3");

        /**
         * To integrate variables in your query you can write '%' and enter the variable after.
         * If you do this the '%' will be replaced and the toString method of your entered object will be called also.
         * And the first entered argument after the query replaces the first entered '%' in the query.
         */

        connection.query("create table Example structure % % %", "Column1", "Column2", "Column3");

        /**
         * When something went wrong with your query an exception will be called. To impede this you can add an ', false' to your query.
         * This could be helpful if you want to create a database if it doesn't exist already, but everytime you query this and the database
         * already exist an exception will be called.
         */

        connection.query("create table Example structure Column1 Column2 Column3", false);

        /**
         * You can also use this with the replacement feature combined. But there it is important to add the false before the replacement variables.
         */

        connection.query("create table Example structure % % %", false, "Column1", "Column2", "Column3");

        /**
         * Maybe you want the result of your query. You can get it like this:
         */

        final Result result = connection.query("select value * from Example");

        result.getCode(); //returns the http (error) code
        result.getType(); //returns the type of response

        result.getResponse(); //returns the howl response in an JSONObject

        //those methods return the structure of the query (important if case of a table)
        result.getStructureArray();
        result.getStructureList();

        /**
         * The actual result for example the content of a table is accessible by the getColumns function. This function returns a list with all columns:
         */

        final List<Column> columns = result.getColumns();

        /**
         * To check if something is inside this columns list you can use the size function of the list:
         */

        if (columns.size() == 0) {
            //Nothing is in the columns list!
        } else {
            //Do something...
        }

        /**
         * For example, you want to get all rows of a table you can use:
         */

        final List<Column> columns = connection.query("select value * from Example").getColumns();

        /**
         * You can go through this with a for loop, for example.
         * Now you have the column and like the result class it has some features.
         */

        for (Column column : columns) {
            column.toJSONObject(); //returns the source of the column as JSONObject

            column.getCreation(); //returns the date when the column was created (is ms).

            /**
             * There are two types of results, with one there are theoretically several columns that could be present.
             * The column whose value you would like to have must be explicitly specified again (select query only). Like here:
             */

            column.getString("Column1");

            /**
             * In the second case, each column has and can only have one value.
             * There you do not have to specify the desired column (list, structure query only). Like this:
             */

            column.getString();

            /**
             * You can convert you result also in other data types:
             * - getLong
             * - getInteger
             * - getDouble
             * - getFloat
             * - getBoolean
             * - getShort
             */

        }
    }
}
```

or you can also import me into your pom.xml. Like this:

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
    <version>1.2</version>
</dependency>
</dependencies>
```

### Python

If you want to use the Python version of the JFQLConnector click
on [download](https://joker-games.org/documentation/connector/python/) to download it

```python
from connector import *

if __name__ == '__main__':
    """If you want to connect your application to a MyJFQL database you have to create a connection with the
    connection class. You can enter your connection information like your host, username and password in the
    constructor of the connection. Like this:"""

    connection = Connection("myjfql:your-host.com", User("your-user-name", "your-user-password"))
    connection.connect()

    """If a connection has been successfully established, commands can be sent to the database.
    You can to this in two ways: you send a query and don't use the response, or you use the response.
    In this case a query will be sent to the database."""

    connection.query("create table Example structure Column1 Column2 Column3")

    """If you want to integrate variables into your query python has some cool features for this. For example:"""

    connection.query("create table Example structure {} {} {}".format("Column1", "Column2", "Column3"))

    """Maybe you want the result of your query. You can get it like this:"""

    result = connection.query("select value * from Example")

    result["type"]  # returns the type of response
    result["structure"]  # returns the structure of the query (important if case of a table)

    """The actual result for example the content of a table is accessible by using code like this:"""

    columns = result["answer"]

    """"To check if something is inside this columns list you can use the len function:"""

    if len(columns) == 0:
        # Nothing is in the columns!
        pass
    else:
        # Do something...
        pass

    """For example, you want to get all rows of a table you can use:"""

    columns = connection.query("select value * from Example")["answer"]

    """You can go through this with a for loop, for example.
    There are two types of results, with one there are theoretically several columns that could be present.
    The column whose value you would like to have must be explicitly specified again (select query only). Like here:"""

    for column in columns:
        column["creation"]  # returns the date when the column was created (is ms).
        content = column["content"]  # returns the content of this column

        content["Column1"]  # returns the value of Column1 of this column

    """In the second case, each column has and can only have one value.
    There you do not have to specify the desired column (list, structure query only). Like this:"""

    for column in columns:
        """The column variable is the content of this column."""
        pass

    """The contents of the columns are defaulted to a string. If you want other type you have to convert them."""
```

### JavaScript

If you want to use the JavaScript version of the JFQLConnector click
on [download](https://joker-games.org/documentation/connector/javascript/) to download it.

```javascript
/**
 * If you want to connect your application to a MyJFQL database you have to create a connection with the connection class.
 * You can enter your connection information like your host, username and password in the constructor of the connection.
 * Like this:
 */


const connection = new Connection("myjfql:your-host.com", "your-user-name", "your-user-password");
connection.connect()

/**
 * If a connection has been successfully established, commands can be sent to the database.
 * You can to this in two ways: you send a query and don't use the response, or you use the response.
 */

connection.query("create table Example structure Column1 Column2 Column3")

/**
 * Maybe you want the result of your query. You can get it like this:
 */

const result = connection.query("select value * from Example")

result["type"]  // returns the type of response
result["structure"]  //returns the structure of the query (important if case of a table)

/**
 * The actual result for example the content of a table is accessible by using code like this
 */

const columns = result["answer"]

/**
 * To check if something is inside this columns list you can use the length function
 */


if (columns.length === 0) {
    //Nothing is in the columns!
} else {
    //Do something...
}

/**
 * For example, you want to get all rows of a table you can use:
 */


const columns = connection.query("select value * from Example")["answer"]

/**
 * You can go through this with a for loop, for example.
 * There are two types of results, with one there are theoretically several columns that could be present.
 * The column whose value you would like to have must be explicitly specified again (select query only). Like here:
 */


for (let column in columns) {
    column["creation"]  // returns the date when the column was created (is ms).
    const content = column["content"]  // returns the content of this column

    content["Column1"]  // returns the value of Column1 of this column
}

/**
 * In the second case, each column has and can only have one value.
 * There you do not have to specify the desired column (list, structure query only). Like this:
 */
for (let column in columns) {
    //The column variable is the content of this column.
}

/**
 * The contents of the columns are defaulted to a string. If you want other type you have to convert them.
 */
```

### License

All files on this repository are subject to the MIT license. Please read
the [LICENSE](https://github.com/joker-games/JFQLConnector/blob/master/LICENSE) file at the root of the project.

