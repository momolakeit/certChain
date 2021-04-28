package com.momo.certChain.repositories;

import com.momo.certChain.model.data.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile,String> {
}
