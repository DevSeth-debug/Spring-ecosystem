package com.example.bpa.modules.authorizationserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("{segment}")
public class Oauth2ErrorController implements ErrorController {
  @RequestMapping("/error")
  public String handleError(
      Model model,
      HttpServletRequest request,
      Authentication authentication,
      @PathVariable String segment) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
    String redirectUrl =
        (authentication != null && authentication.isAuthenticated())
            ? "/realm/home"
            : "/realm/login";
    model.addAttribute("status", statusCode);
    model.addAttribute(
        "error", errorMessage != null ? errorMessage : "An unexpected error occurred.");
    model.addAttribute("redirectUrl", redirectUrl);
    return "error";
  }
}
