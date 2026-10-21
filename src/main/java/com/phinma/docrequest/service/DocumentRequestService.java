package com.phinma.docrequest.service;

import com.phinma.docrequest.model.DocumentRequest;
import com.phinma.docrequest.model.Notification;
import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.DocumentRequestRepository;
import com.phinma.docrequest.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentRequestService {
    private final DocumentRequestRepository requestRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public DocumentRequestService(DocumentRequestRepository requestRepository, 
                                  NotificationRepository notificationRepository,
                                  EmailService emailService) {
        this.requestRepository = requestRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public DocumentRequest createRequest(User student, String docType, String purpose, int copies) {
        DocumentRequest req = new DocumentRequest();
        req.setStudent(student);
        req.setDocumentType(docType);
        req.setPurpose(purpose);
        req.setCopies(copies);
        req.setTrackingNumber("SJC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        req.setStatus("PENDING");

        DocumentRequest saved = requestRepository.save(req);

        // Create student notification
        Notification n = new Notification(student, "Request Submitted: " + saved.getTrackingNumber(), 
                "Your request for " + docType + " has been recorded and is currently PENDING review.");
        notificationRepository.save(n);

        emailService.sendEmail(student.getEmail(), "SJC Manila - Document Request Submitted [" + saved.getTrackingNumber() + "]", 
                "Hello " + student.getFullName() + ",\n\nYour request for " + docType + " (Tracking #: " + saved.getTrackingNumber() + ") is now PENDING review at the Registrar's Office.");

        return saved;
    }

    public List<DocumentRequest> getStudentRequests(User student) {
        return requestRepository.findByStudentOrderByRequestedAtDesc(student);
    }

    public List<DocumentRequest> getAllRequests() {
        return requestRepository.findAllByOrderByRequestedAtDesc();
    }

    public void updateStatus(Long requestId, String status, String remarks) {
        requestRepository.findById(requestId).ifPresent(req -> {
            req.setStatus(status);
            if (remarks != null && !remarks.isBlank()) {
                req.setRemarks(remarks);
            }
            requestRepository.save(req);

            User student = req.getStudent();
            String statusText = status.replace("_", " ");
            String note = (remarks != null && !remarks.isBlank()) ? " Remarks: " + remarks : "";

            // Create In-App Notification
            Notification notif = new Notification(student, 
                    "Request " + req.getTrackingNumber() + " is now " + statusText,
                    "Your request for " + req.getDocumentType() + " has been updated to: " + statusText + "." + note);
            notificationRepository.save(notif);

            // Send Email Notification
            emailService.sendEmail(student.getEmail(), 
                    "SJC Manila - Request Status Update [" + req.getTrackingNumber() + "]",
                    "Hello " + student.getFullName() + ",\n\nYour document request (" + req.getTrackingNumber() + " - " + req.getDocumentType() + ") status is now: " + statusText + ".\n" + note + "\n\nRegistrar Office, Saint Jude College Manila");
        });
    }

    public long countByStatus(String status) {
        return requestRepository.countByStatus(status);
    }

    public long countTotal() {
        return requestRepository.count();
    }
}