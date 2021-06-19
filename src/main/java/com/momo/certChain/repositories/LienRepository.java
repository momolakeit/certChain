package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Lien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LienRepository extends JpaRepository<Lien,String> {
    @Query("SELECT e FROM  Lien e WHERE e.certification.id=?1")
    List<Lien> findLienByCertificationId(String certId);
}
