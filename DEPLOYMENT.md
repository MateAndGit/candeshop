# 배포 가이드

## 배포 구조
- **프론트엔드**: Vercel
- **백엔드**: Railway
- **데이터베이스**: Railway (MySQL)

---

## 1. 백엔드 배포 (Railway)

### Railway에 프로젝트 연결
1. [Railway](https://railway.app/)에 로그인
2. "New Project" → "Deploy from GitHub repo" 선택
3. 이 저장소 선택

### MySQL 데이터베이스 추가
1. Railway 대시보드에서 "New" → "Database" → "MySQL" 선택
2. 데이터베이스가 생성되면 Connection URL을 복사

### 백엔드 서비스 설정
1. Railway에서 백엔드 서비스를 위한 새 서비스 생성
2. GitHub 저장소 연결
3. Root Directory를 `backend`로 설정

### 환경 변수 설정
Railway 대시보드 → Variables 탭에서 다음 환경 변수 설정:

```env
# 데이터베이스
SPRING_DATASOURCE_URL=jdbc:mysql://[Railway MySQL URL]/candeshop?serverTimezone=UTC&characterEncoding=UTF-8
DB_PASSWORD=[Railway MySQL 비밀번호]

# JWT
JWT_SECRET=[랜덤한 긴 문자열 - 최소 256자 권장]

# 이메일 (Gmail 사용 시)
MAIL_USERNAME=[Gmail 주소]
MAIL_PASSWORD=[Gmail 앱 비밀번호]

# CORS (프론트엔드 Vercel URL)
ALLOWED_ORIGINS=http://localhost:5173,https://your-vercel-app.vercel.app
```

**Gmail 앱 비밀번호 생성 방법:**
1. Google 계정 → 보안
2. 2단계 인증 활성화
3. 앱 비밀번호 생성

### 배포
- Railway가 GitHub에 푸시될 때마다 자동 배포
- 배포 완료 후 생성된 URL 확인 (예: `https://your-app.railway.app`)

---

## 2. 프론트엔드 배포 (Vercel)

### Vercel에 프로젝트 연결
1. [Vercel](https://vercel.com/)에 로그인
2. "Add New..." → "Project" 선택
3. GitHub 저장소 연결
4. Root Directory를 `frontend`로 설정

### 빌드 설정
- **Framework Preset**: Vite
- **Build Command**: `npm run build` (자동 감지)
- **Output Directory**: `dist` (자동 감지)

### 환경 변수 설정
Vercel 대시보드 → Settings → Environment Variables에서 설정:

```env
VITE_BACKEND_API_BASE_URL=https://your-railway-backend.railway.app
```

### 배포
- Vercel이 GitHub에 푸시될 때마다 자동 배포
- 배포 완료 후 생성된 URL 확인 (예: `https://your-app.vercel.app`)

### 백엔드 CORS 업데이트
프론트엔드 배포 후, Railway의 `ALLOWED_ORIGINS` 환경 변수에 Vercel URL 추가:

```env
ALLOWED_ORIGINS=http://localhost:5173,https://your-vercel-app.vercel.app
```

---

## 3. 데이터베이스 초기화

Railway MySQL 데이터베이스에 관리자 계정 생성:

```sql
USE candeshop;
INSERT INTO users (username, password, role) 
VALUES ('admin@gmail.com', '$2a$10$[bcrypt 해시된 비밀번호]', 'ADMIN');
```

또는 Railway MySQL CLI를 통해:

```bash
mysql -h [host] -u root -p
USE candeshop;
# init.sql 실행
```

---

## 4. 배포 확인 체크리스트

- [ ] 백엔드 API가 정상 응답하는지 확인: `https://your-railway-backend.railway.app/api/products`
- [ ] 프론트엔드가 백엔드에 연결되는지 확인
- [ ] CORS 오류가 없는지 확인
- [ ] 로그인/회원가입 기능 테스트
- [ ] 상품 CRUD 기능 테스트
- [ ] 장바구니 기능 테스트
- [ ] 주문 기능 테스트

---

## 5. 문제 해결

### CORS 오류
- Railway의 `ALLOWED_ORIGINS`에 Vercel URL이 정확히 입력되었는지 확인
- URL 끝에 `/`가 없는지 확인

### 데이터베이스 연결 오류
- `SPRING_DATASOURCE_URL`이 정확한지 확인
- Railway MySQL의 Connection URL 형식 확인

### 환경 변수 오류
- Railway와 Vercel 양쪽에 환경 변수가 설정되었는지 확인
- 변수명에 오타가 없는지 확인

---

## 6. 비용 정보

- **Railway**: 무료 티어 제공 (월 $5 크레딧)
- **Vercel**: 무료 티어 제공 (개인 프로젝트 무제한)

---

## 참고 자료
- [Railway 문서](https://docs.railway.app/)
- [Vercel 문서](https://vercel.com/docs)
