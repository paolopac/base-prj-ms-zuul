# ==============================================================
#  Docker File Creazione Immagine MS-ZUUL-API-GATEWAY 
# ==============================================================

FROM openjdk:11-jre-slim
LABEL maintainer="Paolo Acquaviva <paoloacqua@hotmail.it>"

ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# ARG Xmx # Abilitare nel caso si voglia resettare, rivalorizzare tale valore in fase di build
# ARG Xss # Abilitare nel caso si voglia resettare, rivalorizzare tale valore in fase di build
ENV Xmx=-XX:MaxRAM=1024m Xss=-Xss1M

WORKDIR /webapi

VOLUME ["/logs"]

COPY /target/ZUUL-MICRO-SERVICE-0.2.0-SNAPSHOT.jar ms-zuul.jar

ENTRYPOINT exec java $JAVA_OPTS $Xmx -XX:+UseSerialGC $Xss -jar ms-zuul.jar

#Generazione Immagine:
# docker build -t ms-zuul .

# Upload in dockerhub:

# docker login 

# docker tag 8fd3dbe5580effb5d34c9b79691e32a3e532b848b5ed4c4a80cda42e5f09f14c paoloacqua/ms-zuul

# docker push paoloacqua/ms-zuul

