package com.tdbang.crm.controllers;

import java.time.LocalDate;

import javax.naming.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2PagesController {
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("appName", "CRM OAuth2 Server");
        model.addAttribute("year", LocalDate.now().getYear());

        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            model.addAttribute("redirectUri", redirectUri);
        }
        Object ex = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex instanceof AuthenticationException) {
            model.addAttribute("errorMessage", ((AuthenticationException) ex).getMessage());
        }
        return "login";
    }
}
