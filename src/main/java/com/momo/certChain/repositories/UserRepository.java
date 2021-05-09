package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByUsername (String email);
}
