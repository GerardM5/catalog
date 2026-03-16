CREATE TABLE product (
    id BIGINT PRIMARY KEY,
    referencia TEXT NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    peso BIGINT,
    volumen BIGINT,
    color TEXT,
    ancho BIGINT,
    largo BIGINT,
    alto BIGINT,
    precio DECIMAL(10,2) NOT NULL,
    empresa TEXT NOT NULL,
    categoria TEXT NOT NULL,
    origen_pdf TEXT NOT NULL
);