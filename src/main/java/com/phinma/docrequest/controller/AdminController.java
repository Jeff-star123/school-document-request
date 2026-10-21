package com.phinma.docrequest.controller;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.service.DocumentRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final DocumentRequestService requestService;

    public AdminController(DocumentRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("requests", requestService.getAllRequests());
        model.addAttribute("totalRequests", requestService.countTotal());
        model.addAttribute("pendingCount", requestService.countByStatus("PENDING"));
        model.addAttribute("processingCount", requestService.countByStatus("PROCESSING"));
        model.addAttribute("readyCount", requestService.countByStatus("READY_FOR_PICKUP"));

        return "admin-dashboard";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long requestId,
                               @RequestParam String status,
                               @RequestParam(required = false) String remarks,
                               HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        requestService.updateStatus(requestId, status, remarks);
        return "redirect:/admin/dashboard?updated=true";
    }
}