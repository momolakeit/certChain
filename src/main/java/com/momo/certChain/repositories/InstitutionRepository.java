package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//todo tester ce custom query
public interface InstitutionRepository extends JpaRepository<Institution,String> {
    @Query("SELECT e FROM  Institution e WHERE e.approuved=false")
    List<Institution> findNonApprouvedInstitution();
}
