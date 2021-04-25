package com.momo.certChain.repositories;

import com.momo.certChain.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
