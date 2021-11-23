# Polaris(Android Version)
- 강의명 : 2021학년도 1학기 소프트웨어공학 캡스톤 프로젝트
- 팀명 : 북두칠성
  - [이주형](https://github.com/yamiblack)
  - [강지웅](https://github.com/JIW00NG)
  - [김진산](https://github.com/lazybones1)
  - [신영환](https://github.com/sin111011)
- 관련 수상 : [2021년 한국정보기술학회 대학생논문경진대회 은상](https://ki-it.or.kr/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD/9152015)

<img src="https://user-images.githubusercontent.com/50551349/126074575-34adb516-cad6-49be-bd19-a9fbb47f7f25.png" width="500">

<br>

## 목차
[1. 개발 배경](#1.개발-배경)    
[2. 서비스 소개](#2.-서비스-소개)      
[3. 시연 영상](#3.-시연-영상)      
[4. 상세 기능 소개](#4.-상세-기능-소개)   
[5. 사용 기술 스택](#5.-사용-기술-스택)    
[6. Advanced Feature](#6.-advanced-feature)     
[7. WBS & Gantt Chart](#7.-wbs-&-gantt-chart)     
[8. 추후 보완 내용(예정)](#8.-추후-보완-내용(예정))      
  

</br>

## 1. 개발 배경
- 보행 중 스마트폰을 사용할 경우의 위험(개발 배경1)
  - [보행 중 주의분산 실태와 사고특성 분석_삼성교통안전문화연구소](http://sts.samsungfire.com/information/regulations/asn/asn_201815_09/asn_issue2.html)
  - ['스몸비, 블좀족' 보행자 위험경보](http://www.ggilbo.com/news/articleView.html?idxno=832249)
    - 스몸비 : 길거리에서 스마트폰을 보면서 걷는 사람을 넋 빠진 시체 걸음걸이에 빗대어 일컫는 말로, ‘스마트폰’과 ‘좀비’의 합성어
    - 블좀족 :블루투스 이어폰으로 발생하는 잦은 사고로 인하여  ‘블루투스 이어폰’과 ‘좀비처럼 걷는 보행자’를 일컫는 합성어
- 주변의 위험을 인지하지 못할 경우의 위험(개발 배경2)
  - ['에어팟 프로' 노이즈 캔슬링 너무 잘돼 쓰러지는 나무 소리 못듣고 깔려 사망한 여성](https://www.insight.co.kr/news/326012)
  - [부산 주택 화재로 50대 청각장애인 숨져..."전기 합선 추정"(종합)](https://www.yna.co.kr/view/AKR20200607008351051)
- 마스크 착용으로 인한 청각장애인들의 불편함(개발 배경3)
  - ["마스크 쓴 탓에..." 생존 위협받는 청각장애인들](https://www.hankyung.com/life/article/2020070706707)
- 스마트워치의 활용도 증가
  - [이제 마스크 껴도 아이폰 잠금해제 OK](https://www.sedaily.com/NewsView/22IDJCY1IC)
  - [이게 삼성 'AR 글라스'? 추정 영상 유출[영상]](https://www.hankyung.com/it/article/202102220077g)

</br>

## 2. 서비스 소개
- 스마트워치의 활용도 증가에 따라 이를 활용하여 보행 중 스마트폰을 주시하는 문제를 개선한다.
- 스마트워치의 진동을 활용하여 도보 내비게이션 서비스 제공한다. 
- 사용자는 직접 스마트워치의 진동 패턴을 본인에게 맞춰 커스터마이징할 수 있다. 
- 사용자는 직접 진동 패턴을 본인에게 맞춰 커스터마이징할 수 있다. 
- 보행 중 노이즈 캔슬링과 같이 주변의 소리를 못 듣는 경우에 폭발음, 경적음, 그리고 공사장 소리와 같이 큰 소리나 위협이 되는 소리가 감지면 이를 진동을 통해 사용자에게 안내한다. 
- 스마트워치에서 STT(Speech-to-Text) 기능을 활용하여 청각장애인의 소통 문제를 개선한다.
- 해당 서비스의 SWOT 분석은 다음과 같다. 

![image](https://user-images.githubusercontent.com/50551349/143029015-14ac1320-658e-4f96-bb48-bb108f1ca9d3.png)

</br>

## System Architecture

![image](https://user-images.githubusercontent.com/50551349/143028631-e0ed4f21-1e9f-496c-858a-159a3b2f8cb2.png)

<br>

## 3. 시연 영상
### 3.1. 진동 내비게이션
[![Video Label](https://user-images.githubusercontent.com/50551349/126074311-e9312869-0cac-420b-b8ec-8483c46a782a.png)](https://youtu.be/Hw5rmQwl3-E)
- 행 중 스마트폰을 사용할 경우의 위험(개발 배경1) 개선했다.
- GS25 편의점으로 목적지를 설정하면 스마트워치에서 이를 안내하여 목적지에 도착하는 것을 위 영상을 통해 확인할 수 있다. 
- 영상에서는 확인이 어렵지만, 초반에 좌회전할 때 설정한 횟수만큼 진동을 확인할 수 있었다. 
- 시간 관계상 영상을 배속으로 편집했다.

</br>

### 3.2. 위험 감지
[![Video Label](https://user-images.githubusercontent.com/50551349/126074507-b6488890-9031-4a90-909c-7175a8496cfd.png)](https://youtu.be/anOrdAAKtKU)
- 주변의 위험을 인지하지 못할 경우의 위험(개발 배경2) 개선했다. 
- 위 영상과 같이 큰 소리가 감지되면 스마트워치는 진동으로 안내한다.

</br>

### 3.3. STT
[![Video Label](https://user-images.githubusercontent.com/50551349/126074509-bebef2a4-9f0c-40a0-bd1b-10440e24a46e.png)](https://youtu.be/afRJe0G7AcE)
- 마스크 착용으로 인한 청각장애인들의 불편함(개발 배경3) 개선했다.
- 위 영상과 같이 스마트워치에서 STT 기능이 동작하는 것을 확인할 수 있다. 
- STT 기능은 단위 테스팅까지 완료된 상태이므로 추후에 통합 테스팅을 진행할 예정이다. 

</br>

## 4. 상세 기능 소개
### 4.1. Firebase 활용 회원가입 및 로그인

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077361-931dfe28-95eb-4568-855e-363ae9001c48.jpg" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077362-974f9c61-5ec1-4b62-adf3-5f5ee89d1bb8.jpg" width="300"/> 
</p>

- Firebase 활용 회원가입 및 로그인 화면은 위 그림과 같다.

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077415-b03cb8d8-1d2c-415b-b2ef-ecf32ce646bf.jpg" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077418-2cab4b1f-53fb-49d6-a807-0755a84451cd.jpg" width="300"/> 
</p>
 
- 좌측은 회원가입 성공화면을, 우측은 로그인 성공 화면을 나타낸다.

</br>

### 4.2. 주변 지도 표시 및 검색

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077442-c2a65f63-9e67-4a8a-9ec8-d243af0c6e8c.png" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077446-21ea7808-aa53-4a84-90b1-700f361f2341.png" width="300"/> 
</p>

- 좌측은 현재 위치를 기반한 주변 지도를 나타낸다.
- 우측은 검색창에서 목적지를 검색할 때의 검색 화면을 나타낸다.

</br>

### 4.3. 경로 탐색 및 내비게이션

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077519-f9638bc5-3d23-47d6-8a06-ad150d0026c5.png" width="300"/> 
 <img src="https://user-images.githubusercontent.com/50551349/126077584-18459647-70ea-4c1c-b251-c8080f7a719c.png" width="300"/>
</p>

- 좌측과 같이 스마트폰에서 '길 안내 시작' 버튼을 터치하면 우측과 같이 스마트워치에서 길 안내가 시작된다. 

</br>

### 4.4. 최근 검색

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077625-f60190c1-fbec-4121-9094-2379dd6498e6.png" width="300"/>
</p>

- 위 그림과 같이 최근 검색 기록을 확인하고 '길 안내 시작' 버튼을 터치하여 바로 길 안내 서비스를 이용할 수 있다. 

</br>

### 4.5. 즐겨찾기

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077717-eed4d3f0-4803-4b66-829d-344ee2103448.png" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077721-e43721a1-8898-452b-b094-76e712c0f79a.png" width="300"/> 
</p>

- 좌측과 같이 검색 결과에서 우측의 별 모양을 터치하면 우측과 같이 별의 색이 변하면서 즐겨찾기 목록에 등록이 된다. 

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077795-0aaaa10e-a8ed-4378-bab8-872fcf22c8d4.png" width="300"/>
</p>

- 위 그림과 같이 즐겨찾기 목록을 확인하고 '길 안내 시작' 버튼을 터치하여 바로 길 안내 서비스를 이용할 수 있다.
</br>


### 4.6. 진동 패턴 설정

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077831-d5be6b5d-8154-40e6-a67c-c00b0037b62c.png" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077835-52766c5c-25b6-4bb1-8cf9-d0b421c49473.png" width="300"/> 
</p>

- 위 그림과 같이 진동 패턴을 사용자별로 설정할 수 있다.

</br>


### 4.7. 음성인식 단어 관리

<p align="center"> 
 <img src="https://user-images.githubusercontent.com/50551349/126077884-aedb2390-054e-45da-8968-22f65bf67625.png" width="300"/>
 <img src="https://user-images.githubusercontent.com/50551349/126077888-2fe1b2ba-4226-4c9c-b3cb-cc4a17109532.png" width="300"/> 
</p>

- 위 그림과 같이 음성인식 단어를 사용자별로 관리할 수 있다.

</br>

## 5. 사용 기술 스택
- Android(Java)
- Tizen Native API
- Samsung Accessory Protocol(SAP)
- T map API

<br>

## 6. Advanced Feature
### 6.1. SAP 연결 설정
```xml
<uses-permission android:name="com.samsung.accessory.permission.ACCESSORY_FRAMEWORK" />
<uses-permission android:name="com.samsung.wmanager.APP" />
<uses-permission android:name="com.samsung.WATCH_APP_TYPE.Companion" />
```

```xml
<receiver android:name="com.samsung.android.sdk.accessory.MessageReceiver">
    <intent-filter>
        <action android:name="com.samsung.accessory.action.MESSAGE_RECEIVED" />
    </intent-filter>
</receiver>

<receiver android:name="com.samsung.android.sdk.accessory.RegisterUponInstallReceiver">
    <intent-filter>
        <action android:name="com.samsung.accessory.action.REGISTER_AGENT" />
    </intent-filter>
</receiver>
<receiver android:name="com.samsung.android.sdk.accessory.ServiceConnectionIndicationBroadcastReceiver">
    <intent-filter>
        <action android:name="com.samsung.accessory.action.SERVICE_CONNECTION_REQUESTED" />
    </intent-filter>
</receiver>

<service android:name="com.samsung.android.sdk.accessory.SAService" />

<meta-data
    android:name="AccessoryServicesLocation"
    android:value="/res/xml/accessoryservices.xml" />
```

- SAP 사용을 위해서는 AndroidManifest.xml에서 위와 같이 설정해야 한다.

<br> 

### 6.2. Galaxy Watch 연결
```java
private void connectGalaxyWatch() {
    SAAgentV2.requestAgent(getApplicationContext(), MessageConsumer.class.getName(), agentCallback);
    new Thread() {
        @Override
        public void run() {
            while (true) {
                if (messageConsumer != null) {
                    messageConsumer.findPeers();
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }
    }.start();
}
```

<br>

### 6.3. Galaxy Watch 데이터 전송
```java
if (messageConsumer != null) {
    messageConsumer.sendData("guide/" + etStraight.getText().toString() + "/straight");
    messageConsumer.sendData("guide/" + etLeft.getText().toString() + "/left");
    messageConsumer.sendData("guide/" + etRight.getText().toString() + "/right");
    messageConsumer.sendData("guide/" + etTwo.getText().toString() + "/two");
    messageConsumer.sendData("guide/" + etFour.getText().toString() + "/four");
    messageConsumer.sendData("guide/" + etEight.getText().toString() + "/eight");
    messageConsumer.sendData("guide/" + etTen.getText().toString() + "/ten");

    Toast.makeText(context, "성공적으로 설정됐습니다.", Toast.LENGTH_SHORT).show();
} else {
    Toast.makeText(context, "갤럭시워치 연결을 확안해주세요.", Toast.LENGTH_SHORT).show();
}
```

<br>

## 7. WBS & Gantt Chart
![image](https://user-images.githubusercontent.com/50551349/143029101-dcebe1f8-3bfc-4fe4-a1d0-11e277a4120f.png)

![image](https://user-images.githubusercontent.com/50551349/143029130-f092ff8a-f515-4974-9692-f1af8fc8d69c.png)

<br>

## 8. 추후 보완 내용 (예정)
- STT 기능 통합
- iOS Version 개발 
- Node.js 서버 구축
- 디자인 수정
- 안정성 개선
