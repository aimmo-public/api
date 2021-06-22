FROM debian:buster-slim as mini-java

ARG UID=1000
ARG GID=1000
ARG PWD=${PWD:-/code}
ARG APP_DIR=${PWD}


ENV APP_DIR=${APP_DIR}

WORKDIR $APP_DIR

RUN chown -R "$UID:$GID" "${APP_DIR}" && \
     id "$UID" || \
     useradd -m -u "$UID" docker

RUN  apt update 
    # shell / editing basics \
    # apt install -y less net-tools screen vim nano curl 

RUN mkdir -p /usr/share/man/man1 && apt install -y openjdk-11-jre-headless  # https://github.com/geerlingguy/ansible-role-java/issues/64
RUN apt install -y openjdk-11-jdk-headless maven

CMD mvn compile exec:java
