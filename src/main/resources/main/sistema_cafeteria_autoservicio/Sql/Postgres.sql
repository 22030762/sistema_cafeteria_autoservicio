-- =========================
-- TABLAS PRINCIPALES POSTGRES
-- =========================

CREATE TABLE usuarios (
                          id_usuario SERIAL PRIMARY KEY,
                          nombre VARCHAR(100),
                          email VARCHAR(100) UNIQUE,
                          password TEXT,
                          fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE productos (
                           id_producto SERIAL PRIMARY KEY,
                           nombre VARCHAR(100),
                           descripcion TEXT,
                           precio_base NUMERIC(10,2),
                           activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE extras (
                        id_extra SERIAL PRIMARY KEY,
                        nombre VARCHAR(100),
                        precio NUMERIC(10,2),
                        activo BOOLEAN DEFAULT TRUE
);

-- =========================
-- PEDIDOS
-- =========================

CREATE TABLE pedidos (
                         id_pedido SERIAL PRIMARY KEY,
                         id_usuario INT REFERENCES usuarios(id_usuario),
                         fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         total NUMERIC(10,2),
                         estado VARCHAR(50) DEFAULT 'PENDIENTE'
);

CREATE TABLE pedido_detalle (
                                id_detalle SERIAL PRIMARY KEY,
                                id_pedido INT REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
                                id_producto INT REFERENCES productos(id_producto),
                                cantidad INT DEFAULT 1,
                                precio_base NUMERIC(10,2)
);

CREATE TABLE pedido_detalle_extras (
                                       id_detalle_extra SERIAL PRIMARY KEY,
                                       id_detalle INT REFERENCES pedido_detalle(id_detalle) ON DELETE CASCADE,
                                       id_extra INT REFERENCES extras(id_extra),
                                       precio_extra NUMERIC(10,2)
);

-- =========================
-- PAGOS (MULTIPLE MÉTODOS)
-- =========================

CREATE TABLE metodos_pago (
                              id_metodo SERIAL PRIMARY KEY,
                              nombre VARCHAR(50) -- Efectivo, Tarjeta, PayPal, etc.
);

CREATE TABLE pagos (
                       id_pago SERIAL PRIMARY KEY,
                       id_pedido INT REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
                       id_metodo INT REFERENCES metodos_pago(id_metodo),
                       monto NUMERIC(10,2),
                       fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       referencia TEXT
);


INSERT INTO usuarios (nombre, email, password) VALUES
                                                   ('Juan Pérez', 'juan@mail.com', '1234'),
                                                   ('María López', 'maria@mail.com', '1234');

INSERT INTO productos (nombre, descripcion, precio_base) VALUES
                                                             ('Hamburguesa', 'Hamburguesa clásica', 50.00),
                                                             ('Hot Dog', 'Hot dog sencillo', 30.00),
                                                             ('Café', 'Café americano', 25.00);

INSERT INTO extras (nombre, precio) VALUES
                                        ('Queso extra', 10.00),
                                        ('Tocino', 15.00),
                                        ('Leche', 5.00),
                                        ('Chocolate', 7.00);

INSERT INTO metodos_pago (nombre) VALUES
                                      ('Efectivo'),
                                      ('Tarjeta'),
                                      ('Transferencia');

INSERT INTO pedidos (id_usuario, total, estado)
VALUES (1, 82.00, 'PAGADO');

INSERT INTO pedido_detalle (id_pedido, id_producto, cantidad, precio_base)
VALUES
    (1, 1, 1, 50.00), -- Hamburguesa
    (1, 3, 1, 25.00); -- Café

INSERT INTO pedido_detalle_extras (id_detalle, id_extra, precio_extra)
VALUES
    (1, 1, 10.00), -- Hamburguesa + Queso
    (1, 2, 15.00), -- Hamburguesa + Tocino
    (2, 3, 5.00),  -- Café + Leche
    (2, 4, 7.00);  -- Café + Chocolate

INSERT INTO pagos (id_pedido, id_metodo, monto, referencia)
VALUES
    (1, 1, 40.00, 'Pago en efectivo'),
    (1, 2, 42.00, 'Pago con tarjeta');