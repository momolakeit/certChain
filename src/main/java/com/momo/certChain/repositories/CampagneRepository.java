package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.Campagne;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampagneRepository extends JpaRepository<Campagne,String> {
}
