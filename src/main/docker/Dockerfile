FROM openjdk:17-jdk-slim

ENV PORT ${PORT:-8080}

EXPOSE ${PORT}

VOLUME /tmp

ADD /maven/*.jar /opt/app.jar

CMD java -jar /opt/app.jar