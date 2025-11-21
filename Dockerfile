FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || true

COPY . .

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app
ENV TZ=Asia/Seoul

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
