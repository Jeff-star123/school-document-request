package com.phinma.docrequest.repository;

import com.phinma.docrequest.model.DocumentRequest;
import com.phinma.docrequest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    List<DocumentRequest> findByStudentOrderByRequestedAtDesc(User student);
    List<DocumentRequest> findAllByOrderByRequestedAtDesc();
    long countByStatus(String status);
}