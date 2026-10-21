package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String rootRedirect(HttpSession session) {
        User loggedUser = (User) session.getAttribute("currentUser");
        if (loggedUser != null) {
            return loggedUser.getRole().equals("ADMIN") ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        Optional<User> user = userService.authenticate(email, password);
        if (user.isPresent()) {
            session.setAttribute("currentUser", user.get());
            if ("ADMIN".equals(user.get().getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/student/dashboard";
        }

        model.addAttribute("error", "Invalid credentials or email does not belong to @phinmaed.com");
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        String result = userService.registerUser(user);
        if ("SUCCESS".equals(result)) {
            return "redirect:/login?registered=true";
        }
        model.addAttribute("error", result);
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}