FROM 10.5.133.220:5000/openjdk:8u121-jre-alpine
VOLUME /tmp
ADD serv05-traincontrol-0.0.5.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]