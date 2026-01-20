# 1단계: 빌드 스테이지
FROM gradle:8-jdk21-jammy AS build
WORKDIR /app

# 빌드에 필요한 설정 파일 먼저 복사
COPY build.gradle settings.gradle ./
# 의존성 미리 다운로드 (캐시 활용)
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 실제 소스 코드 전체 복사
COPY . .

# 소스 코드가 있는지 로그로 확인 (디버깅용) 및 빌드
RUN ls -R src/main/java && gradle clean bootJar -x test

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# 빌드된 jar 파일 위치를 정확히 지정해서 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]