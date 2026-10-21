package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.service.DocumentRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final DocumentRequestService requestService;

    public StudentController(DocumentRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("requests", requestService.getStudentRequests(user));
        return "student-dashboard";
    }

    @GetMapping("/new-request")
    public String newRequestForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "new-request";
    }

    @PostMapping("/new-request")
    public String submitRequest(@RequestParam String documentType,
                                @RequestParam String purpose,
                                @RequestParam(defaultValue = "1") int copies,
                                HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        requestService.createRequest(user, documentType, purpose, copies);
        return "redirect:/student/dashboard?success=true";
    }
}