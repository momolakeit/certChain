package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,String> {
}
