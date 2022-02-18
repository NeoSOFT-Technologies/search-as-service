FROM gradle:jdk11 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

RUN gradle build || return 0
COPY . .
RUN gradle clean build

# Actual Container
FROM openjdk:11
ENV ARTIFACT_NAME=searchservice-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .

ENV SOFT_DELETE_DIR=DeletedRecordFiles

RUN mkdir $APP_HOME/$SOFT_DELETE_DIR
RUN chmod 777 $APP_HOME/$SOFT_DELETE_DIR
RUN touch $APP_HOME/$SOFT_DELETE_DIR/TableDeleteRecord.txt
RUN touch $APP_HOME/$SOFT_DELETE_DIR/SchemaDeleteRecord.txt


#API Port
EXPOSE 8080

ENTRYPOINT exec java -jar ${ARTIFACT_NAME}