package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification,String> {
}
