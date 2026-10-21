package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingsController {
    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Existing password does not match!");
            model.addAttribute("user", user);
            return "settings";
        }

        userService.updatePassword(user.getId(), newPassword);
        user.setPassword(newPassword);
        session.setAttribute("currentUser", user);

        model.addAttribute("success", "Password updated successfully.");
        model.addAttribute("user", user);
        return "settings";
    }
}