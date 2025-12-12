package com.inventory.manager.repository;

import com.inventory.manager.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findBySku(String sku);
    
    List<Item> findByLocationId(Long locationId);
    
    List<Item> findBySupplierId(Long supplierId);
    
    List<Item> findByStatus(Item.ItemStatus status);
    
    @Query("SELECT i FROM Item i WHERE i.quantity < :threshold")
    List<Item> findLowStockItems(@Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByName(@Param("keyword") String keyword);
}
