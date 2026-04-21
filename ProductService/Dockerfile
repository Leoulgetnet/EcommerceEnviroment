FROM mcr.microsoft.com/openjdk/jdk:21-mariner
COPY target/*.jar productservice.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "productservice.jar"]