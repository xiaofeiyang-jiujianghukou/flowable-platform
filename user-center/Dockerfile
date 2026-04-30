FROM crpi-27zlqugq2208c0pz.cn-hangzhou.personal.cr.aliyuncs.com/xiaofeiyang930112/maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src/ src/
COPY settings.xml /tmp/settings.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -s /tmp/settings.xml clean package -DskipTests

FROM crpi-27zlqugq2208c0pz.cn-hangzhou.personal.cr.aliyuncs.com/xiaofeiyang930112/eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/user-center-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
