package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.data.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,String> {
}
