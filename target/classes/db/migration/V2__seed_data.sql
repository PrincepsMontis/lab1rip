-- Тестовые данные locations
INSERT INTO locations (code, name, type, address) 
SELECT 'WH-A', 'Warehouse A', 'WAREHOUSE', '123 Main St' 
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE code = 'WH-A');

INSERT INTO locations (code, name, type, address) 
SELECT 'WH-B', 'Warehouse B', 'WAREHOUSE', '456 Oak Ave' 
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE code = 'WH-B');

INSERT INTO locations (code, name, type, address) 
SELECT 'STORE-1', 'Retail Store 1', 'RETAIL', '789 Market St' 
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE code = 'STORE-1');

-- Тестовые данные suppliers
INSERT INTO suppliers (code, name, contact_person, phone, email, address) 
SELECT 'SUP-001', 'Supplier One', 'John Doe', '+1234567890', 'john@supplier1.com', '100 Business Rd' 
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE code = 'SUP-001');

INSERT INTO suppliers (code, name, contact_person, phone, email, address) 
SELECT 'SUP-002', 'Supplier Two', 'Jane Smith', '+0987654321', 'jane@supplier2.com', '200 Commerce Blvd' 
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE code = 'SUP-002');

-- Тестовые данные items
INSERT INTO items (sku, name, description, quantity, unit_price, location_id, supplier_id, status) 
SELECT 'ITEM-001', 'Test Item 1', 'Description for item 1', 100, 10.50, 1, 1, 'AVAILABLE' 
WHERE NOT EXISTS (SELECT 1 FROM items WHERE sku = 'ITEM-001');

INSERT INTO items (sku, name, description, quantity, unit_price, location_id, supplier_id, status) 
SELECT 'ITEM-002', 'Test Item 2', 'Description for item 2', 50, 25.00, 1, 2, 'AVAILABLE' 
WHERE NOT EXISTS (SELECT 1 FROM items WHERE sku = 'ITEM-002');

INSERT INTO items (sku, name, description, quantity, unit_price, location_id, supplier_id, status) 
SELECT 'ITEM-003', 'Test Item 3', 'Description for item 3', 75, 15.75, 2, 1, 'AVAILABLE' 
WHERE NOT EXISTS (SELECT 1 FROM items WHERE sku = 'ITEM-003');
