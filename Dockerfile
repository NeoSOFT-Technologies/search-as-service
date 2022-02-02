#base docker image
FROM openjdk:11

LABEL maintainer = "neosofttechnologies"

ADD build/libs/searchservice-0.0.1-SNAPSHOT.jar search-service-ingress.jar

#API Port
EXPOSE 8080

ENTRYPOINT ["java","-jar","search-service-ingress.jar"]