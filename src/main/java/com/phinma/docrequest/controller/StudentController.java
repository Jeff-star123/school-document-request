package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.NotificationRepository;
import com.phinma.docrequest.service.AiAssistantService;
import com.phinma.docrequest.service.DocumentRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final DocumentRequestService requestService;
    private final NotificationRepository notificationRepository;
    private final AiAssistantService aiAssistantService;

    public StudentController(DocumentRequestService requestService, 
                             NotificationRepository notificationRepository,
                             AiAssistantService aiAssistantService) {
        this.requestService = requestService;
        this.notificationRepository = notificationRepository;
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("requests", requestService.getStudentRequests(user));
        model.addAttribute("notifications", notificationRepository.findByUserOrderByCreatedAtDesc(user));
        model.addAttribute("unreadNotifs", notificationRepository.countByUserAndIsReadFalse(user));
        return "student-dashboard";
    }

    @GetMapping("/notifications")
    public String notificationsView(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        var list = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);

        model.addAttribute("user", user);
        model.addAttribute("notifications", list);
        return "notifications";
    }

    @GetMapping("/assistant")
    public String assistantView(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "ai-assistant";
    }

    @PostMapping("/api/ai/ask")
    @ResponseBody
    public String askAi(@RequestParam String question) {
        return aiAssistantService.getAnswer(question);
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