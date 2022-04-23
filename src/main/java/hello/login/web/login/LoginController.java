package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form){
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse httpResponse){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처라
        // 쿠키에 시간 정보를 주지 않으면 세션쿠키(브라우저 종료 시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        httpResponse.addCookie(idCookie);
        return "redirect:/";
    }

//    @PostMapping("/login")
    public String login2(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse httpResponse){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // V1
        // 로그인 성공 처라
        // 쿠키에 시간 정보를 주지 않으면 세션쿠키(브라우저 종료 시 모두 종료)
        // Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        // httpResponse.addCookie(idCookie);

        // V2
        sessionManager.createCookie(loginMember, httpResponse);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String login3(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // V1
        // 로그인 성공 처라
        // 쿠키에 시간 정보를 주지 않으면 세션쿠키(브라우저 종료 시 모두 종료)
        // Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        // httpResponse.addCookie(idCookie);

        // V2
        // sessionManager.createCookie(loginMember, httpResponse);

        // V3
        // 세션이 있으면 있는 세션반환, 없으면 신규 세션을 생성
        /**
         request.getSession(true);
            세션이 있으면 기존 세션을 반환
            세션이 없으면 새로운 세션을 생성해서 반환

         request.getSession(true);
            세션이 있으면 기존 세션을 반환
            세션이 없으면 null
         */
        HttpSession session = request.getSession(true); // defalt가 true
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response, "memberId");
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logout2(HttpServletRequest request){
        sessionManager.expired(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout3(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate(); // 무효화하다 invalidate
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
