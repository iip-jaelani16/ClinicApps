# ClinicApps
Clinic App build with Java JFrame


# TP 2 Business Application Development

> Requirement
- Java
- Docker
- Postgres DB

> Instalation postgres DB
```bash
> docker run -e POSTGRES_HOST_AUTH_METHOD=md5 -e POSTGRES_PASSWORD=password -p 5433:5432 --name java_postgres -d postgres
```

Or if you already have docker on your machine, you can change the DB connection to inside the ConnectionManager class
```java
public class ConnectionManager {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://{host}:{post}/{db_name}"; // ganti "mydatabase" dengan nama database Anda
        String username = "{username}"; // ganti "myusername" dengan nama pengguna Anda
        String password = "{password}"; // ganti "mypassword" dengan kata sandi Anda

        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}

```

Java Version

```bash
> java --version
openjdk 19.0.1 2022-10-18
OpenJDK Runtime Environment Homebrew (build 19.0.1)
OpenJDK 64-Bit Server VM Homebrew (build 19.0.1, mixed mode, sharing)


```
Maven dependencies
```bash
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>ClinicApp2</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>19</maven.compiler.source>
        <maven.compiler.target>19</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>
        </dependency>
        <dependency>
            <groupId>commons-dbutils</groupId>
            <artifactId>commons-dbutils</artifactId>
            <version>1.7</version>
        </dependency>
        <dependency>
            <groupId>om-datepicker</groupId>
            <artifactId>om-datepicker</artifactId>
            <version>0.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.jdatepicker</groupId>
            <artifactId>jdatepicker</artifactId>
            <version>1.3.4</version>
        </dependency>
        <dependency>
            <groupId>net.sourceforge.jdatepicker</groupId>
            <artifactId>jdatepicker</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>io.github.lzh0379</groupId>
            <artifactId>jdatepicker</artifactId>
            <version>2.0.3</version>
        </dependency>

    </dependencies>
    <repositories>
        <repository>
            <id>clojars</id>
            <name>Clojars</name>
            <url>https://repo.clojars.org/</url>
        </repository>
    </repositories>

</project>
```

The tool I use:
- [intelliJ IDEA](https://www.jetbrains.com/idea/)
Source:
- https://docs.oracle.com/javase/tutorial
- Google
