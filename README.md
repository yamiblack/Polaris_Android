# Polaris(Android Version)
- 강의명 : 2021학년도 1학기 소프트웨어공학 캡스톤 프로젝트
- 팀명 : 북두칠성
  - [이주형](https://github.com/yamiblack)
  - [강지웅](https://github.com/JIW00NG)
  - [김진산](https://github.com/lazybones1)
  - [신영환](https://github.com/sin111011)
- 관련 수상 : [2021년 한국정보기술학회 대학생논문경진대회 은상](https://ki-it.or.kr/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD/9152015)
<img src="https://user-images.githubusercontent.com/50551349/126074575-34adb516-cad6-49be-bd19-a9fbb47f7f25.png" width="500">

</br>

## 개발 배경
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

## 서비스 소개
- 스마트워치의 활용도 증가에 따라 이를 활용하여 보행 중 스마트폰을 주시하는 문제를 개선한다.
- 스마트워치의 진동을 활용하여 도보 내비게이션 서비스 제공한다. 
- 사용자는 직접 스마트워치의 진동 패턴을 본인에게 맞춰 커스터마이징할 수 있다. 
- 사용자는 직접 진동 패턴을 본인에게 맞춰 커스터마이징할 수 있다. 
- 보행 중 노이즈 캔슬링과 같이 주변의 소리를 못 듣는 경우에 폭발음, 경적음, 그리고 공사장 소리와 같이 큰 소리나 위협이 되는 소리가 감지면 이를 진동을 통해 사용자에게 안내한다. 
- 스마트워치에서 STT(Speech-to-Text) 기능을 활용하여 청각장애인의 소통 문제를 개선한다.

</br>

## 시연 영상
### 1. 진동 내비게이션
[![Video Label](https://user-images.githubusercontent.com/50551349/126074311-e9312869-0cac-420b-b8ec-8483c46a782a.png)](https://youtu.be/Hw5rmQwl3-E)
- 행 중 스마트폰을 사용할 경우의 위험(개발 배경1) 개선했다.
- GS25 편의점으로 목적지를 설정하면 스마트워치에서 이를 안내하여 목적지에 도착하는 것을 위 영상을 통해 확인할 수 있다. 
- 영상에서는 표현이 부족했지만, 초반에 좌회전할 때 설정한 진동의 횟수가 작동했다. 
- 시간 관계상 영상을 배속으로 편집했다.

### 2. 위험 감지
[![Video Label](https://user-images.githubusercontent.com/50551349/126074507-b6488890-9031-4a90-909c-7175a8496cfd.png)](https://youtu.be/anOrdAAKtKU)
- 주변의 위험을 인지하지 못할 경우의 위험(개발 배경2) 개선했다. 
- 위 영상과 같이 큰 소리가 감지되면 스마트워치는 진동으로 안내한다.

### 3. STT
[![Video Label](https://user-images.githubusercontent.com/50551349/126074509-bebef2a4-9f0c-40a0-bd1b-10440e24a46e.png)](https://youtu.be/afRJe0G7AcE)
- 마스크 착용으로 인한 청각장애인들의 불편함(개발 배경3) 개선했다.
- 위 영상과 같이 스마트워치에서 STT 기능이 동작하는 것을 확인할 수 있다. 
- STT 기능은 단위 테스팅까지 완료된 상태이므로 추후에 통합 테스팅을 진행할 예정이다. 

</br>

## 상세 기능 소개


</br>

## 추후 보완 내용 (예정)
- STT 기능 통합
- iOS 개발 
- Node.js 서버 구축
- 디자인 수정
- 안정성 개선

</br>

## 사용 기술 스택
- Android(Java)
- Tizen
- Samsung Accessory Protocol(SAP)
- T map API
