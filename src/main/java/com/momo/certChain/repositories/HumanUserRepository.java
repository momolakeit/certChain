package com.momo.certChain.repositories;

import com.momo.certChain.model.data.HumanUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HumanUserRepository extends JpaRepository<HumanUser,String> {
}
