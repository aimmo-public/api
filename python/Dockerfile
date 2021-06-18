FROM python:3.8.10-slim-buster as mini-python

ARG UID=1000
ARG GID=1000
ARG PIPENV_VENV_IN_PROJECT=1
ARG PWD=${PWD:-/code}
ARG APP_DIR=${PWD}


ENV PYTHONPATH=${APP_DIR}
ENV APP_DIR=${APP_DIR}
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR $APP_DIR

RUN chown -R "$UID:$GID" "${APP_DIR}" && \
     id "$UID" || \
     useradd -m -u "$UID" docker

RUN # apt update \
    shell / editing basics \
    apt install -y less net-tools screen vim nano curl \
    pip install --upgrade pip 

RUN pip install pyjwt

CMD python post_example.py 
