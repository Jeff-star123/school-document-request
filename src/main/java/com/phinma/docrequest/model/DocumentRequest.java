package com.phinma.docrequest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
public class DocumentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String documentType; // Form 137, Report Card, Good Moral Certificate

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private int copies;

    @Column(nullable = false)
    private String status; // PENDING, PROCESSING, READY_FOR_PICKUP, COMPLETED

    private String remarks;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    public DocumentRequest() {
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}