# 1단계: 빌드 스테이지 (JDK 21 버전 사용)
FROM gradle:8-jdk21-jammy AS build
WORKDIR /app

# 설정 파일 복사 및 캐싱
COPY build.gradle settings.gradle ./
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 소스 복사 및 빌드
COPY . .
RUN gradle clean build -x test

# 2단계: 실행 스테이지 (실행 환경도 Java 21로)
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]