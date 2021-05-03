package com.momo.certChain.repositories;

import com.momo.certChain.model.data.InstitutionWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<InstitutionWallet,String> {
}
