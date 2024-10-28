package org.example.repository;

import org.example.entity.Bio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BioRepository extends JpaRepository<Bio, Long> {
}
