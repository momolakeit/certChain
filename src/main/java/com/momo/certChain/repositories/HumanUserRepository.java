package com.momo.certChain.repositories;

import com.momo.certChain.model.data.HumanUser;

import com.momo.certChain.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HumanUserRepository extends JpaRepository<HumanUser,String> {
    Optional<HumanUser> findByUsername (String email);
}
