package com.inventory.manager.repository;

import com.inventory.manager.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LocationRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Test
    void whenFindByCode_thenReturnLocation() {
        // given
        Location location = Location.builder()
                .name("Test Warehouse")
                .code("WH-TEST")
                .type(Location.LocationType.WAREHOUSE)
                .build();
        entityManager.persist(location);
        entityManager.flush();
        
        // when
        Optional<Location> found = locationRepository.findByCode("WH-TEST");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Warehouse");
    }
    
    @Test
    void whenFindByInvalidCode_thenReturnEmpty() {
        // when
        Optional<Location> found = locationRepository.findByCode("INVALID");
        
        // then
        assertThat(found).isEmpty();
    }
}
