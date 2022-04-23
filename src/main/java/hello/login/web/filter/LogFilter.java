package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
        Filter.super.init(filterConfig);
    }

    /**
     Filter의 경우
     ServletRequest, ServletResponse를 제공해준다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        String uuid = UUID.randomUUID().toString();

        // ServletRequest을 다운캐스팅한다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);

            // 있으면 다음 필터를 호출해준다
            // 없으면 DispatcherServlet을 호출한다.
            // 해당 로직이 없으면 다음 단계(Servlet or 다음필터)로 진행이 안된다.
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            // Controller 실행 끝난 후 아래 로직이 실행된다.
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
        Filter.super.destroy();
    }
}
