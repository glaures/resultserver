git pull
mvn -DskipTests package spring-boot:repackage
docker build --tag sandkastenliga/resultserver:1.0 .
docker stop sandkastenliga-resultserver
docker rm sandkastenliga-resultserver
docker run --name sandkastenliga-resultserver -d --net sklnet sandkastenliga/resultserver:1.0