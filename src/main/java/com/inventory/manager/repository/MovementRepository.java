package com.inventory.manager.repository;

import com.inventory.manager.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {
    
    List<Movement> findByItemId(Long itemId);
    
    List<Movement> findByFromLocationId(Long locationId);
    
    List<Movement> findByToLocationId(Long locationId);
    
    @Query("SELECT m FROM Movement m WHERE m.movementDate BETWEEN :startDate AND :endDate")
    List<Movement> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}
