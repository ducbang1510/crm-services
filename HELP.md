# Read Me First

The following was discovered as part of building this project:

* The original package name 'com.tdbang.crm'.

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.5/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.5/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.5/reference/web/servlet.html)
* [Validation](https://docs.spring.io/spring-boot/3.5.5/reference/io/validation.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.5.5/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.5/reference/web/spring-security.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Required Installations

The following instructions are for Windows OS.

### ![Java 17](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

1. Download: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html -> Choose Windows x64
   Installer (for this project, we're using java 17)
2. Install jdk file
3. Setup JAVA_HOME environment variable
    1. Open Setting -> System -> About -> Advanced system settings -> Environment Variables
    2. Add new variable `JAVA_HOME` = Path to jdk-17 (e.g. `C:\Program Files\Java\jdk-17`)
    3. Edit `Path`, add `%JAVA_HOME%\bin;`
    4. Open cmd and run `java --version` to verify.

### ![Maven 3.9+](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

1. Download Maven 3.9: https://maven.apache.org/download.cgi -> Choose Binary zip archive
2. Extract the zip file and copy path to bin folder (e.g. `C:\Program Files\Maven\apache-maven-3.9.9\bin`)
3. Add the path into variable `Path` in Environment Variables.
4. Open cmd and run `mvn -v to verify.

### ![MySQL 8+](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

1. Download MySQL: https://dev.mysql.com/downloads/installer/ -> Choose mysql-installer-community-8+.msi not the web
   version
2. Follow these step in https://www.w3schools.com/mysql/mysql_install_windows.asp
3. Recommend to choose the 'Full' installation in order to have both MySQL server and MySQL Workbench/Shell.
4. Check the option autostart MySQL80 service with Windows.

### ![MongoDB 8.2.0](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)

1. Download MongoDB - Visit this page https://www.mongodb.com/try/download/community-kubernetes-operator, just keep
   everything as default and download it.
2. After the download process is completed, let’s move on to install it. When you reach this step so clicking on the <b>
   Complete</b> button
3. Keep everything as default and <b>Next</b>, <b>Next</b> and <b>Next</b> and then <b>Install</b>
4. After the installing process is completed, it needs to be added to the MongoDB’s bin folder (e.g.
   `C:\Program Files\MongoDB\Server\<version>\bin`) to the `Path`
5. Check the configuration by opening your cmd and run the command `mongod --version`. If you can see the result as the
   screenshot below, the configuration is completed.