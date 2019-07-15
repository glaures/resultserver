# Sandkastenliga Resultserver

## Local development

You will need a local MySQL database "resultserver" to test the application locally.

## Deployment

* Build the fat JAR
  * `mvn -DskipTests package spring-boot:repackage`
  
* Start the network between the DB and App containers
  * `docker network create sklnet`

* Start mysql Docker container
  * `docker run --name resultserver-db -v /Users/guido/mysql/5.6/:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=pw --net sklnet -d mysql:5.6`
  * Make sure the DB has a database `resultserver` defined. If not: `docker exec -it resultserver-db bash`
  
* Start resultserver
  * `docker build --tag sandkastenliga/resultserver:1.0 .`
  * `docker run --name sandkastenliga-resultserver -d -p 80:80 --net sklnet sandkastenliga/resultserver:1.0`
  
* Test the installation
  * http://127.0.0.1/
