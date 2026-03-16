-- =========================================================
-- MVP SCHEMA - MULTI SUPPLIER PRODUCT CATALOG
-- PostgreSQL
-- =========================================================

-- ---------------------------------------------------------
-- 1. SUPPLIERS
-- ---------------------------------------------------------
CREATE TABLE supplier (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    country_code    VARCHAR(2),
    currency_code   VARCHAR(3) NOT NULL DEFAULT 'EUR',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 2. CATALOG IMPORTS / PRICE LIST VERSIONS
--    Tracks which file/version produced the imported data
-- ---------------------------------------------------------
CREATE TABLE supplier_catalog (
    id              BIGSERIAL PRIMARY KEY,
    supplier_id     BIGINT NOT NULL REFERENCES supplier(id),
    file_name       VARCHAR(500) NOT NULL,
    file_hash       VARCHAR(128),
    version_label   VARCHAR(100),
    valid_from      DATE,
    valid_to        DATE,
    imported_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          VARCHAR(30) NOT NULL DEFAULT 'IMPORTED',
    notes           TEXT
);

CREATE INDEX idx_supplier_catalog_supplier_id
    ON supplier_catalog(supplier_id);

-- ---------------------------------------------------------
-- 3. UNIT OF MEASURE
-- ---------------------------------------------------------
CREATE TABLE unit_of_measure (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20) NOT NULL UNIQUE,   -- e.g. ud, m2, ml, kg, l
    name            VARCHAR(100) NOT NULL,
    dimension       VARCHAR(30) NOT NULL           -- count, area, length, weight, volume
);

-- Optional seed examples:
-- INSERT INTO unit_of_measure(code, name, dimension) VALUES
-- ('ud', 'Unidad', 'count'),
-- ('m2', 'Metro cuadrado', 'area'),
-- ('ml', 'Metro lineal', 'length'),
-- ('kg', 'Kilogramo', 'weight'),
-- ('l',  'Litro', 'volume');

-- ---------------------------------------------------------
-- 4. CANONICAL PRODUCT
--    The internal "master product"
-- ---------------------------------------------------------
CREATE TABLE product (
    id                  BIGSERIAL PRIMARY KEY,
    canonical_name      VARCHAR(255) NOT NULL,
    category            VARCHAR(100),
    brand               VARCHAR(100),
    description         TEXT,
    main_uom_id         BIGINT REFERENCES unit_of_measure(id),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_category
    ON product(category);

CREATE INDEX idx_product_canonical_name
    ON product(canonical_name);

-- ---------------------------------------------------------
-- 5. PRODUCT VARIANT
--    Exact comparable item (size/color/material/etc.)
-- ---------------------------------------------------------
CREATE TABLE product_variant (
    id                  BIGSERIAL PRIMARY KEY,
    product_id          BIGINT NOT NULL REFERENCES product(id),
    sku_internal        VARCHAR(100) UNIQUE,
    variant_name        VARCHAR(255) NOT NULL,
    color               VARCHAR(100),
    material            VARCHAR(100),
    width_cm            NUMERIC(10,2),
    length_cm           NUMERIC(10,2),
    height_cm           NUMERIC(10,2),
    diameter_cm         NUMERIC(10,2),
    capacity_l          NUMERIC(10,2),
    gtin                VARCHAR(50),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_variant_product_id
    ON product_variant(product_id);

CREATE INDEX idx_product_variant_gtin
    ON product_variant(gtin);

-- ---------------------------------------------------------
-- 6. SUPPLIER PRODUCT
--    Raw supplier item as it appears in the supplier catalog
-- ---------------------------------------------------------
CREATE TABLE supplier_product (
    id                      BIGSERIAL PRIMARY KEY,
    supplier_id             BIGINT NOT NULL REFERENCES supplier(id),
    supplier_catalog_id     BIGINT NOT NULL REFERENCES supplier_catalog(id),
    supplier_ref            VARCHAR(150) NOT NULL,
    supplier_name           VARCHAR(255) NOT NULL,
    raw_description         TEXT,
    product_variant_id      BIGINT REFERENCES product_variant(id),
    raw_attributes_json     JSONB,
    active                  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_supplier_product UNIQUE (supplier_id, supplier_ref, supplier_catalog_id)
);

CREATE INDEX idx_supplier_product_supplier_id
    ON supplier_product(supplier_id);

CREATE INDEX idx_supplier_product_variant_id
    ON supplier_product(product_variant_id);

CREATE INDEX idx_supplier_product_supplier_ref
    ON supplier_product(supplier_ref);

-- ---------------------------------------------------------
-- 7. SUPPLIER PRICE
--    Prices for supplier products, including quantity tiers
-- ---------------------------------------------------------
CREATE TABLE supplier_price (
    id                  BIGSERIAL PRIMARY KEY,
    supplier_product_id BIGINT NOT NULL REFERENCES supplier_product(id) ON DELETE CASCADE,
    price               NUMERIC(14,4) NOT NULL,
    currency_code       VARCHAR(3) NOT NULL DEFAULT 'EUR',
    uom_id              BIGINT NOT NULL REFERENCES unit_of_measure(id),
    min_qty             NUMERIC(14,3) NOT NULL DEFAULT 1,
    valid_from          DATE,
    valid_to            DATE,
    tax_included        BOOLEAN NOT NULL DEFAULT FALSE,
    source_catalog_id   BIGINT REFERENCES supplier_catalog(id),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_supplier_price_positive CHECK (price >= 0),
    CONSTRAINT chk_supplier_price_min_qty CHECK (min_qty > 0)
);

CREATE INDEX idx_supplier_price_supplier_product_id
    ON supplier_price(supplier_product_id);

CREATE INDEX idx_supplier_price_validity
    ON supplier_price(valid_from, valid_to);

CREATE INDEX idx_supplier_price_min_qty
    ON supplier_price(min_qty);

-- Very useful for "best price by variant"
CREATE INDEX idx_supplier_price_lookup
    ON supplier_price(supplier_product_id, min_qty, valid_from, valid_to, price);

-- ---------------------------------------------------------
-- 8. OPTIONAL STOCK / AVAILABILITY
-- ---------------------------------------------------------
CREATE TABLE supplier_stock (
    id                  BIGSERIAL PRIMARY KEY,
    supplier_product_id BIGINT NOT NULL REFERENCES supplier_product(id) ON DELETE CASCADE,
    in_stock            BOOLEAN,
    stock_qty           NUMERIC(14,3),
    lead_time_days      INTEGER,
    stock_notes         TEXT,
    checked_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_supplier_stock_supplier_product_id
    ON supplier_stock(supplier_product_id);

-- ---------------------------------------------------------
-- 9. MATCH / MAPPING AUDIT
--    Track how supplier products were matched to internal variants
-- ---------------------------------------------------------
CREATE TABLE product_match (
    id                  BIGSERIAL PRIMARY KEY,
    supplier_product_id BIGINT NOT NULL REFERENCES supplier_product(id) ON DELETE CASCADE,
    product_variant_id  BIGINT NOT NULL REFERENCES product_variant(id),
    match_status        VARCHAR(20) NOT NULL,   -- PENDING, MATCHED, REJECTED
    confidence          NUMERIC(5,4),
    matched_by          VARCHAR(100),
    matched_at          TIMESTAMP,
    notes               TEXT
);

CREATE INDEX idx_product_match_supplier_product_id
    ON product_match(supplier_product_id);

CREATE INDEX idx_product_match_variant_id
    ON product_match(product_variant_id);

-- ---------------------------------------------------------
-- 10. OPTIONAL VIEW: CURRENT BEST PRICE PER VARIANT
--    Simple current snapshot view
-- ---------------------------------------------------------
CREATE OR REPLACE VIEW vw_current_supplier_prices AS
SELECT
    pv.id AS product_variant_id,
    p.id AS product_id,
    p.canonical_name,
    sp.id AS supplier_product_id,
    s.id AS supplier_id,
    s.name AS supplier_name,
    sp.supplier_ref,
    sp.supplier_name AS supplier_product_name,
    pr.price,
    pr.currency_code,
    u.code AS uom_code,
    pr.min_qty,
    pr.valid_from,
    pr.valid_to,
    pr.tax_included
FROM product_variant pv
JOIN product p
    ON p.id = pv.product_id
JOIN supplier_product sp
    ON sp.product_variant_id = pv.id
JOIN supplier s
    ON s.id = sp.supplier_id
JOIN supplier_price pr
    ON pr.supplier_product_id = sp.id
JOIN unit_of_measure u
    ON u.id = pr.uom_id
WHERE sp.active = TRUE
  AND pv.active = TRUE
  AND p.active = TRUE
  AND (
        pr.valid_from IS NULL OR pr.valid_from <= CURRENT_DATE
      )
  AND (
        pr.valid_to IS NULL OR pr.valid_to >= CURRENT_DATE
      );

-- ---------------------------------------------------------
-- 11. SAMPLE QUERY: BEST PRICE FOR A VARIANT AND QUANTITY
-- ---------------------------------------------------------
-- Example usage:
-- Find the cheapest current price for a variant and quantity
--
-- SELECT *
-- FROM vw_current_supplier_prices
-- WHERE product_variant_id = :variant_id
--   AND min_qty <= :requested_qty
-- ORDER BY price ASC, min_qty DESC
-- LIMIT 1;