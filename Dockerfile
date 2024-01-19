# ==============================================================
#  Docker File Creazione Immagine ZUUL-API-GATEWAY WEB SERVICE 
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

COPY /target/ZUUL-MICRO-SERVICE-0.1.0-SNAPSHOT.jar zuul-ms.jar

ENTRYPOINT exec java $JAVA_OPTS $Xmx -XX:+UseSerialGC $Xss -jar zuul-ms.jar

#Generazione Immagine:
# docker build -t zuul-ms .

# Upload in dockerhub:

# docker login 

# docker tag f99ccf9e3f12 paoloacqua/zuul-ms

# docker push paoloacqua/zuul-ms

