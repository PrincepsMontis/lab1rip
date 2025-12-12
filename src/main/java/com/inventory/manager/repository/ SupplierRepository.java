package com.inventory.manager.repository;

import com.inventory.manager.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByTaxId(String taxId);
}
