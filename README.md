# NAC_using_hyperledgerFabric  
하이퍼레저 페브릭을 이용하여 NAC 접근 권한 상태를 저장하며 라즈베리파이를 이용하여 Network 접근을 제한.  
  
아래의 논문 내용을 참고하여 구현  
***Smart Contract-Based AccessControl for the Internet of Things - Yuanyu Zhang, Member, IEEE, Shoji Kasahara, Member, IEEE, Yulong Shen, Member, IEEE,***

## 개인 결과물 노션 페이지
[프로젝트 개인 결과물 노션 페이지](https://www.notion.so/lcy960729/NAC-aeb5d0cb319544f49980601da4631da0)

## 목적
블록체인 및 스마트 컨트랙트를 이용하여 비 중앙방식의 네트워크 접근제어 시스템을 구현하였다.

기존 접근 제어 시스템의 문제점인 중앙 서버의 부하와 별도의 장비 추가를 위한 비용증가, 보안성 약화 등의 문제점을 해소할 수 있으며 블록체인 기반 시스템의 장점인 부하 분산, 데이터에 대한 무결성, 가용성의 증대를 가져올 수 있다.

## 시스템 구조
![시스템 구조](https://user-images.githubusercontent.com/58020519/108621919-02b1be80-7479-11eb-807f-e8bb5806f98d.png)
* 하이퍼레저 페브릭을 이용하여 프라이빗 블록체인 네트워크 구성
* 체인 코드를 이용하여 블록체인 네트워크에 NAC 관련 자원 관리 및 접근 권한 요청 구현
* 라즈베리 파이를 이용하여 네트워크를 구축하여 접근 제어
* Spring Boot를 이용하여 관리자 페이지 백엔드 서버 구축

## 동작 방식
가장 초기 설정에서 채널이 없을 시 블록체인 네트워크에 채널 생성 및 체인 코드 업로드  
이후 공유할 네트워크 환경을 추가하고 허용할 단말 노드들의 정보를 입력하여 네트워크 공유  

### 1.채널이 없을때
![채널이 없을때](https://user-images.githubusercontent.com/58020519/106419618-31510080-649c-11eb-8f3c-443acc046487.png)

### 2.체인 코드 업로드
![체인코드 업로드](https://user-images.githubusercontent.com/58020519/106419630-357d1e00-649c-11eb-986b-996def41c046.png)

### 3.메인 화면
![메인 화면](https://user-images.githubusercontent.com/58020519/106419601-272f0200-649c-11eb-9d9d-c22ca585958c.png)

## 동작 환경
프론트 서버
Node.js 	14.15.0.
npm       	6.14.8

백엔드 서버
java 	openJDK Runtime Environment 1.8.0_272

블록체인 네트워크
go		1.15.4
docker 	20.10.0
***
## 실행 방식
위에 제시한 모든 환경을 설치 후 아래의 명령을 따르면 됨

1.프론트 서버, 백엔드 서버, 블록체인 네트워크 실행 
   폴더 최 상단에 존재하는 start.sh 실행

2.프론트 서버, 백엔드 서버, 블록체인 네트워크 종료
   폴더 최 상단에 존재하는 stop.sh 실행


3.라즈베리파이로 네트워크 생성
바탕화면 경로로 터미널을 연후 아래의 명령어 실행
sudo -i 실행 아래 명령어 입력
python IoTGateway.py [Network Interface] [Web Server Address] [Web Port]

	[Network Interface]는 ifconfig 명렁어 입력 후 eth0이나 wlan0을 작성
	[Web Server Address]는 백엔드 서버의 ip주소
	[Web Port]는 백엔드 서버의 포트 주소
***
## 폴더 구조
backend - 백엔드 서버 코드입니다.  
frontend - 프론트 서버 코드입니다.  
docs - 구현 문서 파일과 시연 영상이 있습니다.  
hyperledgerFabric - 하이퍼레저 페브릭 네트워크 파일입니다.  
IoTGateway - 게이트웨이 프로그램 파일입니다.  
start.sh 네트워크를 올리고 프론트와 백엔드 서버를 실행합니다.  
stop.sh 네트워크를 내리고 프론트와 백엔드 서버를 종료합니다.  
