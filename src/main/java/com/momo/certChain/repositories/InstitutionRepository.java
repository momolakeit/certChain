package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution,String> {
}
