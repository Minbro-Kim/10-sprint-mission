# 🚀 Project Discodeit (Sprint 8)

## 📊 Test Coverage

[![codecov](https://codecov.io/github/Minbro-Kim/10-sprint-mission/branch/sprint8/graph/badge.svg?token=B0WE4JVIIN)](https://codecov.io/github/Minbro-Kim/10-sprint-mission)


## 🤖 사용방법

### 📥 Repository Clone
- 해당 레포지토리(sprint 8) 로컬 저장

---

### ☁️ (선택) AWS S3 설정
- s3 저장방식을 사용하는 경우 필수
#### 🪣 S3 Bucket 생성
- 모든 퍼블릭 액세스를 차단한 범용 버킷 생성

#### 👩‍🎤 IAM User 생성(권장)
- Access Key 생성 및 .csv 파일 다운
- IAM User 인라인 권한 정책 연결
  - json 인라인 정책(app 실행에 필요한 최소한의 권한 정의)
    
    ```json
    {
    	"Version": "2012-10-17",
    	"Statement": [
    		{
    			"Sid": "S3AppAccess",
    			"Effect": "Allow",
    			"Action": [
    				"s3:PutObject",
    				"s3:GetObject",
    				"s3:GetBucketLocation"
    			],
    			"Resource": [
    				"s3 arn 주소/*",
    				"s3 arn 주소"
  			  ]
  		  }
  	  ]
    }
    ```


#### 🛜 S3 연결 검증
- `AWSS3TestTest`을 실행하여 S3 업로드/다운로드 테스트
- 환경 변수 미지정 시, 실행 X
  
```bash
RUN_S3_CONNECT_TEST=true ./gradlew test --tests "com.sprint.mission.discodeit.storage.s3.AWSS3TestTest"
```

---

### 🐳 로컬 실행(Docker compose)

- Docker Compose를 사용하여 Spring Boot 애플리케이션과 PostgreSQL DB를 단일 브리지 네트워크 내에서 실행
- 볼륨을 사용하여 데이터 유지

#### 1️⃣ .env 환경 변수 설정
- 프로젝트 루트(root) 위치에 .env 파일 생성 및 하위 내용 입력

```properties
# [필수] Storage 설정 (local 또는 s3)
STORAGE_TYPE=s3
STORAGE_LOCAL_ROOT_PATH=.discodeit/storage

# [S3 선택 시 필수] AWS 자격 증명
AWS_S3_ACCESS_KEY=YOUR_ACCESS_KEY
AWS_S3_SECRET_KEY=YOUR_SECRET_KEY
AWS_S3_REGION=ap-northeast-2
AWS_S3_BUCKET=YOUR_BUCKET_NAME
AWS_S3_PRESIGNED_URL_EXPIRATION=600

# [필수] Database 설정
POSTGRES_DB=db_name
POSTGRES_USER=your_name
POSTGRES_PASSWORD=your_password

# [필수] Spring Boot Profile
SPRING_PROFILE=prod
```

#### 2️⃣ 컨테이너 제어 명령어

- 컨에이너 실행
  
  ```bash
  docker-compose up
  ```
- 컨테이너 중지
  
  ```bash
  docker-compose down
  ```
- 컨테이너 볼륨 삭제
  
  ```bash
  docker-compose down -v
  ```






