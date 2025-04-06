# LoveNotes - 안드로이드 포트폴리오 프로젝트

![LoveNotes Icon](app/src/main/res/mipmap/love_notes_icon.jpg)

**LoveNotes**는 현대적인 안드로이드 개발 기술을 활용하여 제작된 포트폴리오 프로젝트입니다. 이 앱은 사용자가 사랑하는 사람들과의 추억을 기록하고, 일정을 관리하며, AI를 활용한 데이트 플랜을 생성할 수 있도록 설계되었습니다. 최신 안드로이드 개발 프레임워크와 라이브러리를 사용하여 모던하고 반응성 있는 사용자 경험을 제공합니다.

---

## 주요 기능
- **구글 로그인**;
- **캘린더 관리**: 사용자는 중요한 이벤트를 추가, 수정, 삭제할 수 있으며, 날짜별로 일정을 시각화합니다.
- **위치 기반 추억 기록**: GPS를 활용해 이동 경로를 기록하고, 사진과 메모를 추가하여 특별한 순간을 저장합니다.
- **AI 데이트 플래너**: Gemini API를 통해 날짜, 위치, 목적 등을 기반으로 최적의 데이트 계획을 제안합니다.
- **소셜 기능**: 초대 코드를 통해 다른 사용자를 구독하고, 서로의 일정과 추억을 공유할 수 있습니다.
- **오프라인 지원**: Room 데이터베이스를 사용해 오프라인에서도 경로 데이터를 저장하고 관리합니다.

---

## 기술 스택

이 프로젝트는 최신 안드로이드 개발 기술을 활용하여 설계되었습니다. 주요 기술 스택은 다음과 같습니다:

- **언어**: Kotlin (100% 코틀린으로 작성)
- **아키텍처**: MVVM (Model-View-ViewModel) + Clean Architecture
- **의존성 주입**: Hilt (Dagger 기반)
- **UI**: Jetpack Compose (모던 선언형 UI)
- **네비게이션**: Jetpack Navigation Compose
- **데이터베이스**: Room (로컬 데이터 저장)
- **백엔드**: Firebase (Firestore, Authentication)
- **위치 서비스**: Google Play Services Location API (FusedLocationProviderClient)
- **지도**: Google Maps Compose
- **AI**: Gemini API (Generative AI 통합)
- **비동기 처리**: Coroutines + Flow
- **기타 라이브러리**:
    - Accompanist (Pager, Permissions)
    - Material 3 (최신 디자인 시스템)

---
## 프로젝트 구조
```markdown
app/
├── src/
│   ├── main/
│   │   ├── java/com/self/lovenotes/           # 소스 코드
│   │   │   ├── data/                         # 데이터 계층 (Room, Firebase, Repository)
│   │   │   ├── di/                           # 의존성 주입 모듈 (Hilt)
│   │   │   ├── domain/                       # 비즈니스 로직 (UseCase)
│   │   │   ├── presentation/                 # UI 및 ViewModel
│   │   │   └── service/                      # 백그라운드 서비스 (TrackingService)
│   │   ├── res/                              # 리소스 (아이콘, 테마, 문자열 등)
│   │   └── AndroidManifest.xml               # 매니페스트 파일
```
---

## 설치 방법

### 사전 요구 사항

- **Android Studio**: 최신 버전 (2023.1.1 이상 권장)
- **JDK**: 17 이상
- **Google Maps API Key**: `GOOGLE_MAP_API_KEY` 환경 변수 설정 필요
- **Gemini API Key**: `GEMINI_API_KEY` 환경 변수 설정 필요
- **Firebase 설정**: `google-services.json` 파일 필요

### 설치 단계

1. **레포지토리 클론**
- git clone https://github.com/your-username/LoveNotes.git
- cd LoveNotes

2. **환경 변수 설정**
- `local.properties` 파일에 아래 내용을 추가:
- GOOGLE_MAP_API_KEY=your-google-maps-api-key
  GEMINI_API_KEY=your-gemini-api-key

3. 3. **Firebase 설정**
- Firebase 콘솔에서 프로젝트를 생성하고, `google-services.json` 파일을 `app/` 디렉토리에 추가합니다.

4. **프로젝트 빌드**
- Android Studio에서 프로젝트를 열고, **Sync Project with Gradle Files**를 실행합니다.
- 디바이스 또는 에뮬레이터에서 앱을 실행합니다.

---

## 사용법

1. **로그인**
- 앱 실행 시 익명 로그인(Firebase Anonymous Auth)을 통해 자동으로 계정이 생성됩니다.

2. **캘린더**
- 날짜를 선택하고 이벤트를 추가하거나 편집합니다.
- 구독한 사용자의 일정도 함께 확인 가능합니다.

3. **추억 기록**
- 위치 추적 서비스를 활성화하여 이동 경로를 기록합니다.
- 사진과 메모를 추가해 특별한 날을 저장합니다.

4. **데이트 플래너**
- 날짜, 장소, 목적을 입력하면 AI가 최적의 일정을 제안합니다.

5. **설정**
- 닉네임을 설정하고, 초대 코드를 통해 다른 사용자를 구독합니다.
