# Inventory Manager (Учет оборудования)

Лабораторная работа по Java / Spring Boot.  
Тема: **Система учета оборудования с отслеживанием перемещений**.

Основной функционал:

- управление предметами (Item);
- управление локациями (Location);
- управление поставщиками (Supplier);
- отслеживание перемещений между локациями (Movement);
- отчет по остаткам на складах;
- импорт/экспорт данных в CSV;
- базовая валидация данных и обработка ошибок;
- unit-тесты сервисов.

---

## 1. Стек технологий

- Java 17
- Spring Boot 3
  - Spring Web (REST API)
  - Spring Data JPA / Hibernate
  - Bean Validation (Jakarta Validation)
- База данных:
  - по умолчанию: H2
  - поддержка: PostgreSQL
- Миграции: Liquibase (опционально)
- Сборка: Maven
- Тестирование: JUnit 5, Mockito

---

## 2. Архитектура и пакеты

Корень проекта:
inventory-manager/
  pom.xml
  src/
    main/
      java/com/example/inventory/
        InventoryManagerApplication.java
        config/
          GlobalExceptionHandler.java
        model/
          Item.java
          Location.java
          Movement.java
          Supplier.java
          MovementType.java
        item/
          ItemController.java
          ItemService.java
          ItemRepository.java
          ItemRequest.java
          ItemResponse.java
        location/
          LocationController.java
          LocationRepository.java
          LocationResponse.java
        supplier/
          SupplierController.java
          SupplierRepository.java
          SupplierResponse.java
        movement/
          MovementController.java
          MovementService.java
          MovementRepository.java
          MovementRequest.java
          MovementResponse.java
        report/
          ReportController.java
          ReportService.java
          StockReportResponse.java
        export/
          ExportController.java
          ExportService.java
      resources/
        application.yml
        schema.sql (опционально)
    test/
      java/com/example/inventory/
        movement/
          MovementServiceTest.java
        report/
          ReportServiceTest.java

---

## 3. Валидация и обработка ошибок

Используются аннотации Jakarta Validation:

- `@NotBlank`, `@NotNull`, `@Positive`, `@Min` на полях сущностей и DTO.
- `@Valid` в контроллерах при приёме запросов.

Обработчик ошибок `GlobalExceptionHandler`:

- `MethodArgumentNotValidException` → `400 Bad Request` + список ошибок по полям;
- `IllegalArgumentException` → `400 Bad Request` (недостаточно товара, локация не найдена);
- `EntityNotFoundException` → `404 Not Found` (предмет/локация не существует);
- общий `Exception` → `500 Internal Server Error`.

---

## 4. База данных и инициализация (H2)

Конфигурация в `src/main/resources/application.yml`:

- in-memory база `jdbc:h2:mem:inventorydb`;
- режим `MODE=PostgreSQL` для совместимости;
- авто-генерация схемы `spring.jpa.hibernate.ddl-auto=update`;
- H2 console: `http://localhost:8080/h2-console`.

При запуске приложения выполняется `CommandLineRunner` в `InventoryManagerApplication`:

- создаёт 4 локации (Главный склад, Склад №2, Офис Москва, Офис СПб);
- создаёт 3 поставщиков (Dell, HP, Logitech);
- создаёт несколько предметов с начальным количеством;
- всё это делает демонстрацию проще: сразу есть данные для запросов.

---
## 5. Запуск
mvn clean test
mvn spring-boot:run