package com.example.bpa.modules.authorizationserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("{segment}")
public class LoginController {

  @GetMapping("/login")
  public String login(
      Model model,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout,
      @RequestParam(value = "expired", required = false) String expired,
      @PathVariable("segment") String segment) {
    if (error != null) {
      model.addAttribute("error", "Invalid username or password.");
    }
    if (logout != null) {
      model.addAttribute("message", "You have been logged out successfully.");
    }
    if (expired != null) {
      model.addAttribute("message", "Your session has expired. Please log in again.");
    }
    return "login";
  }

  @GetMapping("/home")
  public String home(
      Authentication authentication, Model model, @PathVariable("segment") String segment) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return "redirect:/login";
    }
    model.addAttribute("authentication", authentication);
    return "home";
  }
}
