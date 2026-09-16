package com.thartheeb.admin.infrastructure.persistence;

import com.thartheeb.admin.domain.DocumentPolicyVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentPolicyRepository extends JpaRepository<DocumentPolicyVersion, UUID> {
    List<DocumentPolicyVersion> findByActiveTrueOrderByDocumentTypeAsc();
    Optional<DocumentPolicyVersion> findFirstByDocumentTypeAndActiveTrueOrderByPolicyVersionDesc(String type);
}
