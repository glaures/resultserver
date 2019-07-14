FROM openjdk:11
ADD ./target/resultserver-1.0.jar .
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "resultserver-1.0.jar"]