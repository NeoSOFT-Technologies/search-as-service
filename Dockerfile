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

RUN mkdir $APP_HOME/DeletedRecordFiles
RUN chmod 755 $APP_HOME/DeletedRecordFiles
#RUN cd DeletedRecordFiles
RUN touch $APP_HOME/DeletedRecordFiles/TableDeleteRecord.txt
RUN touch $APP_HOME/DeletedRecordFiles/SchemaDeleteRecord.txt


#API Port
EXPOSE 8080

ENTRYPOINT exec java -jar ${ARTIFACT_NAME}