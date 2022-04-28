package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentResolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    public final MemberRepository memberRepository;
    public final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homLogin(@CookieValue(name="memberId", required = false) Long memberId, Model model){
        if(memberId == null){
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homLogin2(HttpServletRequest request, Model model){
        // 세션관리
        Member member = (Member)sessionManager.getSession(request);

        if(member == null){
            return "home";
        }

        if(member == null){
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homLogin3(HttpServletRequest request, Model model){

        // Home화면 진입이기때문에
        // 세션을 생성할 의도가 없기에 getSssion(false)로 지정해줘야한다.
        HttpSession session = request.getSession(false);
        if(session == null){
            return "home";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(member == null){
            return "home";
        }

        if(member == null){
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homLogin4(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model){

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

    @GetMapping("/")
    public String homLogin5(@Login Member member, Model model){

        if(member == null){
            return "home";
        }

        if(member == null){
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }
}