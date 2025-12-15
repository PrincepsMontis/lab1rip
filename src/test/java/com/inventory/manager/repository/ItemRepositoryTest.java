package com.inventory.manager.repository;

import com.inventory.manager.entity.Item;
import com.inventory.manager.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .name("Test Warehouse")
                .code("TEST-WH-001")
                .type(Location.LocationType.WAREHOUSE)
                .build();
        entityManager.persist(testLocation);
        entityManager.flush();
    }

    @Test
    void whenFindBySku_thenReturnItem() {
        // given
        Item item = Item.builder()
                .name("Test Laptop")
                .sku("TEST-LAPTOP-001")
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(999.99))
                .location(testLocation)
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        entityManager.persist(item);
        entityManager.flush();

        // when
        Optional<Item> found = itemRepository.findBySku("TEST-LAPTOP-001");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Laptop");
    }

    @Test
    void whenFindByLocationId_thenReturnItems() {
        // given
        Item item1 = Item.builder()
                .name("Item 1")
                .sku("TEST-ITEM-101")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(100.00))
                .location(testLocation)
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        Item item2 = Item.builder()
                .name("Item 2")
                .sku("TEST-ITEM-102")
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(200.00))
                .location(testLocation)
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        // when
        List<Item> items = itemRepository.findByLocationId(testLocation.getId());

        // then
        assertThat(items).hasSize(2);
    }

    @Test
    void whenFindLowStockItems_thenReturnCorrectItems() {
        // given
        Item lowStock = Item.builder()
                .name("Low Stock Item")
                .sku("TEST-LOW-001")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(50.00))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        Item highStock = Item.builder()
                .name("High Stock Item")
                .sku("TEST-HIGH-001")
                .quantity(100)
                .unitPrice(BigDecimal.valueOf(50.00))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        entityManager.persist(lowStock);
        entityManager.persist(highStock);
        entityManager.flush();

        // when
        List<Item> items = itemRepository.findLowStockItems(10);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getSku()).isEqualTo("TEST-LOW-001");
    }

    @Test
    void whenSearchByName_thenReturnMatchingItems() {
        // given
        Item laptop = Item.builder()
                .name("Dell Laptop")
                .sku("TEST-LAP-001")
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(999.99))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        Item mouse = Item.builder()
                .name("Wireless Mouse")
                .sku("TEST-MOU-001")
                .quantity(50)
                .unitPrice(BigDecimal.valueOf(29.99))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        entityManager.persist(laptop);
        entityManager.persist(mouse);
        entityManager.flush();

        // when
        List<Item> items = itemRepository.searchByName("laptop");

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).contains("Laptop");
    }
}
