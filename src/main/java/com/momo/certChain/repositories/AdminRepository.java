package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,String> {
}
