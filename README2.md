Filter는 Servlet에서 제공하고, Interceptor는 Spring에서 제공해준다.

위 의미가 어떤것인지 정확히 설명을 못하기에

Servlet과 Spring의 차이가 뭔지 궁금하였기에 아래 내용을 정리해본다.

--------

### 1. Servlet

```java
 public abstract class HttpServlet extends GenericServlet {
      //...
       protected void doGet(HttpServletRequest req, HttpServletResponse resp){...}
       protected void doPost(HttpServletRequest req, HttpServletResponse resp){...}
       //...
 }
```

**웹 서버**프로그래밍을 하기 위해 사양을 갖춘 코드라고 할 수 있다.<br>이게 어떤 의미일까?, 

**Servlet**은 **Servlet Container(Tomcat)**에 의해 관리되고 실행됩니다.<br>개발자는 Servlet을 만들어 HTTP요청을 받아 처리하는 서비스 로직만 구현하면 됩니다.

메서드를 보면 알 수 있듯이 Request, Response즉, **웹 서버 기능 동작**이 가능하다.

------

### 2. Tomcat

WAS(Web Application Server) 중 하나로, **Servlet Container**라고도 표현할 수 있다.<br>Java개발자가 작성한 Servlet을 관리한다.

Client가 요청한 정보를 어떤 Servlet이 실행해줄 것인지 제어한다.

----------------

### 3. Web.xml

- WAS가 어떤 Servlet을 생성하고 어떤 Servlet이 어떤요청을 처리할 것인지 기능을 명세한다.

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <servlet> 
        <servlet-name>appServlet</servlet-name> 
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    </servlet> 
    <servlet-mapping>
        <servlet-name>appServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    <filter>
       <filter-name>encodingFilter</filter-name>
       <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
       <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
       </init-param>   
    </filter>
    <filter-mapping>
       <filter-name>encodingFilter</filter-name>
       <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

--------

### 4. DispatcherServlet

모든요청을 받아들이는 프론트 컨드롤러이다.

ServletContainer에서 DispatcherServlet만 등록해놓고, HandlerMapping을 통해 적절한 Controller를 매핑시킨다.

----------

### 5. Servlet Filter

- Servlet 실행 전, 후에 어떤 작업을 하고자할 때 Servlet Filter 를 사용한다.
- Interceptor를 사용할 수 있겠지만 차이점은 실행시점(handler전, 후)에 차이가 있습니다.
- Filter 는 Servlet Container에 등록하고 Interceptor는 스프링 컨테이너에 등록합니다.
- javax.servlet.Filter 인터페이스의 구현체입니다.



> 출처: https://jeong-pro.tistory.com/222 [기본기를 쌓는 정아마추어 코딩블로그:티스토리]
> https://jypthemiracle.medium.com/servletcontainer%EC%99%80-springcontainer%EB%8A%94-%EB%AC%B4%EC%97%87%EC%9D%B4-%EB%8B%A4%EB%A5%B8%EA%B0%80-626d27a80fe5
