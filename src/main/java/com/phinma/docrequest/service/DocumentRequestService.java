package com.phinma.docrequest.service;

import com.phinma.docrequest.model.DocumentRequest;
import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.DocumentRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentRequestService {
    private final DocumentRequestRepository requestRepository;

    public DocumentRequestService(DocumentRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public DocumentRequest createRequest(User student, String docType, String purpose, int copies) {
        DocumentRequest req = new DocumentRequest();
        req.setStudent(student);
        req.setDocumentType(docType);
        req.setPurpose(purpose);
        req.setCopies(copies);
        // Clean tracking code like REQ-A1B2C3D4
        req.setTrackingNumber("SJC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        req.setStatus("PENDING");
        return requestRepository.save(req);
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
        });
    }

    public long countByStatus(String status) {
        return requestRepository.countByStatus(status);
    }

    public long countTotal() {
        return requestRepository.count();
    }
}