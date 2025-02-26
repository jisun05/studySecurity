package com.example.studysecurity.web;

import com.example.studysecurity.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login/google")
    public String googleLogin(@RequestParam("code") String code) {
        String token = loginService.loginWithGoogle(code);
        return "redirect:/login/success?token=" + token;
    }

    @GetMapping("/oauthLogin")
    public String oauthLogin() {
        return "oauthLogin";  // src/main/resources/templates/oauthLogin.html
    }

    @GetMapping("/login/success")
    public String loginSuccess(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "loginSuccess"; // 템플릿 파일: src/main/resources/templates/loginSuccess.html
    }
}
