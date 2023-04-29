FROM tomcat:9.0-jre11-openjdk-slim-bullseye

ARG WAIT_TIMEOUT
ARG PORT

ENV WAIT_TIMEOUT ${WAIT_TIMEOUT:-'60s'}
ENV PORT ${PORT:-8080}

RUN addgroup --gid 1000 www-group && adduser --system --disabled-password --gecos "" --gid 1000 --uid 1000 www-user
RUN chown -hR 1000:1000 ${CATALINA_HOME}

EXPOSE ${PORT}

COPY --chown=1000:root /maven/*.war ${CATALINA_HOME}/webapps/ROOT.war

USER 1000

CMD ["catalina.sh", "run"]