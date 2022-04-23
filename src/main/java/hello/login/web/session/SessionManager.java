package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리
 */
@Component
public class SessionManager {
    public static final String SESSIN_COOKIE_NAME = "mySessionId";

    // 동시에 여러 요청이 있는 경우 사용
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    // 세션 생성
    public void createCookie(Object object, HttpServletResponse response){
        // sessionId 생성
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, object);

        // 쿠카 생성
        Cookie cookie = new Cookie(SESSIN_COOKIE_NAME, sessionId);
        response.addCookie(cookie);
    }

    // 세션 조회
    public Object getSession(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSIN_COOKIE_NAME);

        if(sessionCookie == null){
            return null;
        }

        return sessionStore.get(sessionCookie.getValue());
    }

    // 세션 만료
    public void expired(HttpServletRequest request){
        Cookie cookie = findCookie(request, SESSIN_COOKIE_NAME);
        if(cookie != null){
            sessionStore.remove(cookie.getValue());
        }
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null){
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }
}
