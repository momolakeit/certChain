package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRepository extends JpaRepository<Signature,String> {
}
