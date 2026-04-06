# 1. 베이스 이미지
FROM amazoncorretto:17
# 2. 작업 디렉토리
WORKDIR /app
# 3. 파일 복사
COPY . .
# 4. 빌드
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# build
RUN ./gradlew clean build -x test

# 5. 노출 포트
EXPOSE 80

# 6. 환경변수 설정
# 1) 프로젝트 이름
ENV  PROJECT_NAME=discodeit
# 2) 프로젝트 버전
ENV PROJECT_VERSION=1.2-M8
# 3) JVM 옵션(기본값은 빈 문자열)
# 메모리나 gc 옵션 설정가능
ENV JVM_OPTS=""

# 7. 컨테이너 실행
# 환경변수 실행을 위해 쉘 형태로 작성
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
