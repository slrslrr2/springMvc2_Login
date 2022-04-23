# springMvc2_Login
스프링 MVC 2편 - 백엔드 웹 개발 활용 기술 - (로그인 처리)

쿠키는 브라우저에 담겨, 모든 요청이 있을 때마다 서버도 전달된다.

<img width="537" alt="image-20220423182740653" src="https://user-images.githubusercontent.com/58017318/164895166-682b0cf9-d5e8-48d8-8f85-396e43b7d54f.png">



**쿠카는 영속쿠키와 세션쿠키가 있다**

- **영속 쿠키** : 만료 날짜를 입력하면 해당 날짜까지 유지된다.

- **세션 쿠키** : 만료 날짜를 생략하면 브라우저 종료시 까지만 유지된다.<br>브라우저 종료시 로그아웃이 되길 기대하므로, 우리에게 필요한 것은 **세션 쿠키**이다.??

    이상하네 우리는 요청 할 때마다 연장되는거였으니까 세션쿠키가 아니라 영속쿠키를 한거아냐???????



그렇다면 로그인과 로그아웃 처리를 Cookie를 통해 해보자

##### LoginController.java

```java
// 로그인
Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
httpResponse.addCookie(idCookie);

// 로그아웃
@PostMapping("/logout")
public String logout(HttpServletResponse response){
  expireCookie(response, "memberId");
  return "redirect:/";
}

private void expireCookie(HttpServletResponse response, String cookieName) {
  Cookie cookie = new Cookie("memberId", null);
  cookie.setMaxAge(0); // 해당 쿠키의 종료날짜를 0으로 지정한다.
  response.addCookie(cookie);
}
```



하지만 저렇게 쿠키값으로만 로그인 로그아웃을 한다면, <br>쿠키는 값을 임의로 변경할 수 있기때문에 다른 사용자로도 변신할 수 있다.



#### 대안 [세션] 이용하기

1. 쿠키에 중요한 값을 노출시키지 않고 **예측 불가능한 임의의 토큰**값을 노출한다.
2. 서버에서 토큰값을 사용자id와 매핑해서 인식한다.<br>단, 토큰은 만료시간을 짧게 유지한다(30분)
3. 토큰은 해당 서버에서 관리한다.



세션을 어떻게 할 것인지 전체 개념을 이해해보자

1. 사용자가 loginId, password를 전달하면, 서버는 해당 사용자가 맞는지 확인을 한다.

<img width="544" alt="image-20220423185228083" src="https://user-images.githubusercontent.com/58017318/164895168-4192e435-a9d7-424c-b174-7252eaa2f1d6.png">

<br><br>2. 해당 사용자가 맞을 경우 **UUID**라는 랜덤값을 생성하여 세션저장소에 담아둔다.<br>생성된 **세션ID**와 세션에 보관할 값(**Member객체**)을 세션 저장소에 보관한다.

- Cookie: **mySessionId**=zz0101xx-bab9-4b92-9b32-dadb280f4b61
<img width="542" alt="image-20220423185418752" src="https://user-images.githubusercontent.com/58017318/164895174-f3d7da14-b60f-42f6-8f97-3071af884005.png">

<br><br>3. sessionId를 담아 쿠키로 전달한다.

- **클라이언트와 서버는 결국 쿠키로 연결이 되어야 한다**

<img width="538" alt="image-20220423185607953" src="https://user-images.githubusercontent.com/58017318/164895175-83395f8d-e894-4ba5-8006-f741af25d582.png">
<br><br>

 여기서 중요한 포인트는 **회원과 관련된 정보는 클라이언트에 전달하지 않는것**<br>또한, **추정 불가능한 세션 ID**만 쿠키를 통해 **클라이언트**에 전달한다.

4.  로그인 이후
   - 클라이언트의 요청 시 항상 sessionId를 전달한다.
   - 서버에서는 클라이언트가 전달한 sessionId 쿠키정보로<br>세션 저장소를 조회해서 로그인 시 보관한 세션 정보를 사용한다.



**정리**

세션저장소에 sessionId(uuid)를 저장하고 sessionId(uuid)만을 클라이언트 브라우저에 저장할 경우<br> 아래와 같은 보안문제들을 해결할 수 있다.

| 문제                                                         | 해결                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 쿠키 값 변조 가능                                            | 예상 불가능한 복잡한 세션Id를 사용                           |
| 쿠키를 보관하는 정보는 클라이언트 해킹 시 털릴 가능성이 있다. | 세션Id에는 중요한 정보가 없다.                               |
| 쿠키 탈취 후 사용                                            | 세션 만료시간을 30분 정도로 짧게 유지하고<br>클라이언트 요청이 있을 때마다 유지시간을 연장한다.<br>또한, 해킹이 의심되는 경우 세션을 강제로 제거한다. |



-----



세션관리는 크게 3가지로 나뉜다.

1. 세션 생성
   - sessionId 생성 (임의의 추정 불가능한 랜덤 값)
   - 세션 저장소에 sessionId와 보관할 값을 저장
2. 세션 조회
   - 클라이언트가 요청한 sessionId쿠키의 값으로, 세션저장소에 보관할 값 조회
3. 세션 만료
   - 클라이언트가 요청한 sessionId쿠키의 값으로, 세션 저장소에 보관한 **sessionId 값 제거**



-----

Servlet이 제공하는 **HttpSession**을 사용하여 만들어보자

HttpSession을 사용하면 Cookie이름이 **JSEESSIONID**으로 값은 불가능한 랜던값이 생성된다.

```log
Cookie: JSESSIONID=5B78E23B513F50164D6FDD8C97B0AD05
```



### 1. 세션 생성

```java
HttpSession session = request.getSession(true); // defalt가 true
session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
```

| 구분                      | 세션 O 반환값 | 세션 X 반환값 |
| ------------------------- | ------------- | ------------- |
| request.getSession(true)  | 기존 세션     | 새로 생성     |
| request.getSession(false) | 기존 세션     | Null          |



````java
@PostMapping("/login")
public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request){
  if(bindingResult.hasErrors()){
    return "login/loginForm";
  }

  Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

  if(loginMember == null){
    bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
    return "login/loginForm";
  }

  HttpSession session = request.getSession(true); // defalt가 true
  session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

  return "redirect:/";
}
````

<img width="1012" alt="image-20220423204952378" src="https://user-images.githubusercontent.com/58017318/164895176-699d65f8-63bf-4914-8db2-75ceb380b9f6.png">

<img width="757" alt="image-20220423205052020" src="https://user-images.githubusercontent.com/58017318/164895177-c9b649b3-eb7d-4a58-8864-e56ec6b0f18d.png">



### 2. Home 진입 시 Session 가져오기

```java
HttpSession session = request.getSession(false);
if(session == null){
  return "home";
}

Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
```

위 5줄 짜리 소스를 아래 어노테이션으로 한번에 처리 가능하다

```java
public String homLogin4(
  @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
  , Model model){
```



```java
@GetMapping("/")
public String homLogin4(
  @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model){

  //        // Home화면 진입이기때문에
  //        // 세션을 생성할 의도가 없기에 getSssion(false)로 지정해줘야한다.
  //        HttpSession session = request.getSession(false);
  //        if(session == null){
  //            return "home";
  //        }
  //
  //        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

  if(member == null){
    return "home";
  }

  if(member == null){
    return "home";
  }

  model.addAttribute("member", member);
  return "loginHome";
}
```



### 3. 세션 만료

- HttpSession session = request.getSession(false);
- session.invalidate();

```java
@PostMapping("/logout")
public String logout3(HttpServletRequest request){
  HttpSession session = request.getSession(false);
  if(session != null){
    session.invalidate(); // 무효화하다 invalidate
  }
  return "redirect:/";
}
```



그런데 URL에 JSESSIONID로 아래와 같이 들어오기때문에 <br>브라우저에서 쿠키를 지원하지 않는 경우로 만들어진 것이다.

<img width="507" alt="image-20220423205910738" src="https://user-images.githubusercontent.com/58017318/164895181-feff746c-895e-4941-b561-a82898463367.png">

Application.properties에 다음 내용을 넣어주자

```java
server.servlet.session.tracking-modes=cookie
```



--------------



```java
- 영속쿠키
- 세션쿠키
- Cookie cookie = new Cookie(“memberId”, String.valueOf(loginMember.getId());  
           HttpResponse.addCookie(cookie);
  - Cookie cookie = new Cookie(“memberId”, null);
       cookie.setMaxAge(0);
       response.addCookie(cookie);

 - 세션
1. 세션 생성
  HttpSession session = request.getSession(true); // defalt가 true
  session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

 2. 세션만료
  HttpSession session = request.getSession(false);
  if(session != null){
    session.invalidate(); // 무효화하다 invalidate
  }

3. 세션가져오기
HttpSession session = request.getSession(false);
Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
   ===> @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
```

