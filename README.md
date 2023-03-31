# 이어줌 안드로이드 프로젝트
 - firefox 웹브라우저를 기반으로 하여 웹브라우저를 통해 재생되는 영상을 추출하여 음성을 텍스트로(STT) 변환하여 청각장애인을 위한 서비스를 제공하고 있습니다.
 - 추가로 수어 아바타를 통해 자막에 대한 수어를 아바타를 통해 내용 설명을 제공하고 있습니다.


- 개발 이슈: Firefox 웹브라우저를 기반으로 개발함에 있어 Firefox 웹 브라우저 소스의 개발 환경에 따라 개발을 진행합니다. 최신 버전의 환경설정을 하지 못하게 되는 아쉬움이 있습니다.
 
----------

# 🚧개발환경


## 개발도구

- Android Studio 최신 버전

      * Update 22/07/14
      Android Studio Chipmunk | 2021.2.1 Patch 1
      Build #AI-212.5712.43.2112.8609683, built on May 19, 2022
      Runtime version: 11.0.12+7-b1504.28-7817840 amd64
      VM: OpenJDK 64-Bit Server VM by Oracle Corporation
      Windows 10 10.0
      GC: G1 Young Generation, G1 Old Generation
      Memory: 3072M
      Cores: 8
      Registry: external.system.auto.import.disabled=true, ide.balloon.shadow.size=0
      Non-Bundled Plugins: 
               com.github.patou.gitmoji (1.10.0), 
               mobi.hsz.idea.gitignore (4.3.0), 
               idea.plugin.protoeditor (212.5080.8), 
               org.intellij.plugins.markdown (212.5457.16), 
               org.jetbrains.kotlin (212-1.6.21-release-334-AS5457.46), 
               com.chrisrm.idea.MaterialThemeUI (6.9.1)
      
----------

## 보안

- API

      EncryptedSharedPreferences
          spec: MasterKeys.AES256_GCM_SPEC
     

----------

## Gradle JDK

- JDK 11

      version 11.0.11
      
      Android Gradle Plugin Version: 7.1.2
      
      Gradle Versioni: 7.2

----------
## 개발 언어

- Kotlin, Java

----------
## 대상 단말

      안드로이드 6.0 마쉬맬로우(Marshmallow) 버전 이상의 phone / tablet 장치
      API LV 21이 default 이나, 보안 및 개발 편의 성을 이유로 API LV 23으로 설정하여 진행 예정
      
----------
## 개발 설정

      Min SDK Version: 23
       - Android 6.0(Marshmallow) 부터 지원
      
      Target SDK Version: 30
       - Android 11(R) 을 지원하는 것으로, Google play에 배포를 위해서는 반드시 30 이상으로 설정을 해야만 하기에 강제적입니다.
       - 이로 인해 로컬 스토리지의 파일 접근 권한이 강해져 개발 이슈가 발생합니다.
       
      Build features: dataBinding 사용불가, viewBinding 만 사용
      
-------------
## 디자인 패턴

- MVVM 패턴 기반의 AAC(Android Architecture Component) 사용

      목적: Clean Architecture
      - 테스트와 개발의 편의성 향상, 간결한 소스로 인한 가독성 향상을 위해

      MVVM pattern: Model – View – ViewModel
      
      Libraries
            Android Support Library
            Android Architecture Components
            - LiveData, Flow
            - ViewModel
            - Room
            Hilt for dependency injection -> 낮은 버전으로 사용 불가
            Retrofit for REST api communication
            Glide for image loading
            Timber for logging -> 낮은 버전으로 사용 불가
            Espresso for UI tests
            Mockito for mocking in tests
            
----------
