// com.example.studysecurity.web 패키지에 이 클래스가 속함을 선언합니다.
package com.example.studysecurity.web;

// LoginService 클래스를 사용하기 위해 import 합니다.
import com.example.studysecurity.service.LoginService;
// Lombok의 @RequiredArgsConstructor 어노테이션을 사용하기 위해 import 합니다.
import lombok.RequiredArgsConstructor;
// 스프링 MVC에서 Controller로 사용하기 위해 import 합니다.
import org.springframework.stereotype.Controller;
// 스프링 MVC에서 Model 객체를 사용하기 위해 import 합니다.
import org.springframework.ui.Model;
// GET 요청을 처리하기 위한 어노테이션과 RequestParam을 사용하기 위해 import 합니다.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 이 클래스가 스프링 MVC의 컨트롤러임을 명시합니다.
@Controller
// Lombok의 어노테이션으로, final 필드에 대해 생성자를 자동으로 생성합니다.
@RequiredArgsConstructor
public class LoginController {
    // LoginService를 주입받기 위한 final 필드입니다.
    private final LoginService loginService;

    // GET 요청 "/login/google"를 처리하며, "code"라는 쿼리 파라미터를 받아 처리합니다.
    @GetMapping("/login/google")
    public String googleLogin(@RequestParam("code") String code) {
        // Google 로그인 코드(code)를 사용하여 토큰을 발급받습니다.
        String token = loginService.loginWithGoogle(code);
        // 로그인 성공 후, 발급받은 토큰을 쿼리 파라미터로 전달하며 "/login/success"로 리다이렉트합니다.
        return "redirect:/login/success?token=" + token;
    }

    // GET 요청 "/oauthLogin"을 처리합니다.
    @GetMapping("/oauthLogin")
    public String oauthLogin() {
        // "oauthLogin.html" 템플릿 파일을 렌더링합니다.
        return "oauthLogin";  // src/main/resources/templates/oauthLogin.html
    }

    // GET 요청 "/login/success"를 처리하며, 선택적으로 "token" 쿼리 파라미터를 받을 수 있습니다.
    @GetMapping("/login/success")
    public String loginSuccess(@RequestParam(required = false) String token, Model model) {
        // 모델에 "token" 값을 추가하여 뷰 템플릿에서 사용할 수 있도록 합니다.
        model.addAttribute("token", token);
        // "loginSuccess.html" 템플릿 파일을 렌더링합니다.
        return "loginSuccess"; // 템플릿 파일: src/main/resources/templates/loginSuccess.html
    }
}
