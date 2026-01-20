# 1단계: 빌드 (Gradle 8 / JDK 21)
FROM gradle:8-jdk21-jammy AS build
WORKDIR /app

# 현재 폴더(backend)의 모든 내용을 복사
COPY --chown=gradle:gradle . .

# gradlew 실행 권한 부여 및 빌드 (테스트 제외)
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test

# 2단계: 실행 (JRE 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 빌드된 jar 파일을 app.jar로 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]