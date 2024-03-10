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

COPY /target/MICRO-SERVICE-ZUUL-0.2.0-SNAPSHOT.jar th-prj-ms-zuul.jar

ENTRYPOINT exec java $JAVA_OPTS $Xmx -XX:+UseSerialGC $Xss -jar th-prj-ms-zuul.jar

#Generazione Immagine:
# docker build -t th-prj-ms-zuul .

# Upload in dockerhub:

# docker login 

# docker tag b65689588a440bf70faa90ad69001e6d86eb88abd07a214be5146f07b922f6bb paoloacqua/th-prj-ms-zuul:0.2.0

# docker push paoloacqua/th-prj-ms-zuul:0.2.0

