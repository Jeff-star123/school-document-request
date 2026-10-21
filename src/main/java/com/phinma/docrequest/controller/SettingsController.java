package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.Notification;
import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.NotificationRepository;
import com.phinma.docrequest.service.EmailService;
import com.phinma.docrequest.service.OtpService;
import com.phinma.docrequest.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingsController {
    private final UserService userService;
    private final OtpService otpService;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public SettingsController(UserService userService, 
                              OtpService otpService,
                              NotificationRepository notificationRepository,
                              EmailService emailService) {
        this.userService = userService;
        this.otpService = otpService;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/api/send-settings-otp")
    @ResponseBody
    public String sendSettingsOtp(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "UNAUTHORIZED";

        otpService.generateAndSendOtp(user.getEmail(), "Account Security Password Change");
        return "SUCCESS";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @RequestParam String otp,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Current password does not match!");
            model.addAttribute("user", user);
            return "settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match!");
            model.addAttribute("user", user);
            return "settings";
        }

        if (!otpService.verifyOtp(user.getEmail(), otp)) {
            model.addAttribute("error", "Invalid or expired security OTP code!");
            model.addAttribute("user", user);
            return "settings";
        }

        userService.updatePassword(user.getId(), newPassword);
        user.setPassword(newPassword);
        session.setAttribute("currentUser", user);

        // Record In-App notification & send security confirmation email
        Notification notif = new Notification(user, "Security Alert: Password Updated", 
                "Your account portal password was successfully updated.");
        notificationRepository.save(notif);

        emailService.sendEmail(user.getEmail(), "SJC Manila - Security Alert: Password Changed", 
                "Hello " + user.getFullName() + ",\n\nYour account password has been updated. If you did not perform this change, contact the Registrar or MIS department immediately.");

        model.addAttribute("success", "Password updated successfully with OTP verification.");
        model.addAttribute("user", user);
        return "settings";
    }
}