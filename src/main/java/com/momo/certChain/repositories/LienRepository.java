package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Lien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LienRepository extends JpaRepository<Lien,String> {
}
