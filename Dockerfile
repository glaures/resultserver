FROM openjdk:11
COPY ./target/resultserver-1.0.jar .
CMD ["java", "--add-modules java.xml.bind -jar", "resultserver-1.0.jar"]