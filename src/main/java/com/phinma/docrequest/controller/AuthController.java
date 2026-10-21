package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.service.OtpService;
import com.phinma.docrequest.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;
    private final OtpService otpService;

    public AuthController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
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
        if (session.getAttribute("currentUser") != null) return "redirect:/";
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
            return "ADMIN".equals(user.get().getRole()) ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }

        model.addAttribute("error", "Invalid credentials or email does not belong to @phinmaed.com");
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (session.getAttribute("currentUser") != null) return "redirect:/";
        return "register";
    }

    // Ajax endpoint to send OTP for Registration
    @PostMapping("/api/send-registration-otp")
    @ResponseBody
    public String sendRegistrationOtp(@RequestParam String email) {
        if (email == null || !email.trim().toLowerCase().endsWith("@phinmaed.com")) {
            return "ERROR: Only official @phinmaed.com emails are eligible.";
        }
        otpService.generateAndSendOtp(email, "Student Account Registration");
        return "SUCCESS";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, 
                                  @RequestParam String confirmPassword,
                                  @RequestParam String otp,
                                  Model model) {
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        if (!otpService.verifyOtp(user.getEmail(), otp)) {
            model.addAttribute("error", "Invalid or expired OTP. Please request a new code.");
            return "register";
        }

        String result = userService.registerUser(user);
        if ("SUCCESS".equals(result)) {
            return "redirect:/login?registered=true";
        }
        model.addAttribute("error", result);
        return "register";
    }

    // Forgot Password Flow
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/api/send-reset-otp")
    @ResponseBody
    public String sendResetOtp(@RequestParam String email) {
        if (email == null || !email.trim().toLowerCase().endsWith("@phinmaed.com")) {
            return "ERROR: Must be a valid @phinmaed.com address.";
        }
        otpService.generateAndSendOtp(email, "Account Password Reset");
        return "SUCCESS";
    }

    @PostMapping("/forgot-password")
    public String processResetPassword(@RequestParam String email,
                                       @RequestParam String otp,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match!");
            return "forgot-password";
        }

        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "Invalid or expired OTP!");
            return "forgot-password";
        }

        boolean updated = userService.resetPasswordByEmail(email, newPassword);
        if (!updated) {
            model.addAttribute("error", "Account with that email was not found.");
            return "forgot-password";
        }

        return "redirect:/login?resetSuccess=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}