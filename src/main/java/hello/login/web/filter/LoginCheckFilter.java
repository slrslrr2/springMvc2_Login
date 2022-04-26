package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
public class LoginCheckFilter implements Filter {
    /**
    Filter는 servlet에서 제공해주는것을 사용해야한다.
        import javax.servlet.Filter

    init, destroy는 defalt로 정의되어
    interface이기에 구현안해도된다.

    public default void init(FilterConfig filterConfig) throws ServletException {}
    public default void destroy() {}
    */

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            log.info("인증 체크 필터 시작 {}", requestURI);
            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = ((HttpServletRequest) request).getSession();

                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 {}", requestURI);
                    // 뒤에 ?redirectURL을 붙여주면 요청하였던 requestURI해당 페이지로 넘어간다.
                    httpResponse.sendRedirect("/login?redirectURL="+requestURI);
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e){
            throw e; // 예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야한
                    // 만약 System.out.println(e) 요런거 하면 톰캣으로 못올려주고 끝나겠죠?
                    // 요청-> WAS -> Filter -> Servlet -> handler
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
//        System.out.println("실행");
        // try안에서 49라인 return 된 경우, 아래 sout은 안찍히고 finally까지만 실행된다.
    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
