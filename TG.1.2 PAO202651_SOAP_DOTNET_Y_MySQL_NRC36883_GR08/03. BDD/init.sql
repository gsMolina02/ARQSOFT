-- =============================================================================
-- Universidad de las Fuerzas Armadas ESPE - Arquitectura de Software
-- NRC 36883 - Grupo 08 (Gustavo Molina, Daniela Tituaña)
-- Esquema: eurekabank (Core Bancario EurekaBank)
-- Motor: MySQL 8.0
-- Archivo: Crea_BD.sql (Creación de Base de Datos, Permisos y Tablas)
-- =============================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS eurekabank;
CREATE DATABASE eurekabank CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;

-- Configuración de usuario de aplicación Monster / Monster9
CREATE USER IF NOT EXISTS 'Monster'@'localhost' IDENTIFIED BY 'Monster9';
ALTER USER 'Monster'@'localhost' IDENTIFIED BY 'Monster9';
GRANT ALL PRIVILEGES ON eurekabank.* TO 'Monster'@'localhost';
FLUSH PRIVILEGES;

USE eurekabank;

CREATE TABLE TipoMovimiento (
    chr_tipocodigo       CHAR(3) NOT NULL,
    vch_tipodescripcion  VARCHAR(40) NOT NULL,
    vch_tipoaccion       VARCHAR(10) NOT NULL,
    vch_tipoestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
    CONSTRAINT PK_TipoMovimiento PRIMARY KEY (chr_tipocodigo),
    CONSTRAINT chk_tipomovimiento_vch_tipoaccion CHECK (vch_tipoaccion IN ('INGRESO', 'SALIDA')),
    CONSTRAINT chk_tipomovimiento_vch_tipoestado CHECK (vch_tipoestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
) ENGINE = INNODB;

CREATE TABLE Sucursal (
    chr_sucucodigo       CHAR(3) NOT NULL,
    vch_sucunombre       VARCHAR(50) NOT NULL,
    vch_sucuciudad       VARCHAR(30) NOT NULL,
    vch_sucudireccion    VARCHAR(50) NULL,
    int_sucucontcuenta   INTEGER NOT NULL,
    CONSTRAINT PK_Sucursal PRIMARY KEY (chr_sucucodigo)
) ENGINE = INNODB;

CREATE TABLE Empleado (
    chr_emplcodigo       CHAR(4) NOT NULL,
    vch_emplpaterno      VARCHAR(25) NOT NULL,
    vch_emplmaterno      VARCHAR(25) NOT NULL,
    vch_emplnombre       VARCHAR(30) NOT NULL,
    vch_emplciudad       VARCHAR(30) NOT NULL,
    vch_empldireccion    VARCHAR(50) NULL,
    CONSTRAINT PK_Empleado PRIMARY KEY (chr_emplcodigo)
) ENGINE = INNODB;

CREATE TABLE Modulo (
    int_moducodigo       INTEGER NOT NULL,
    vch_modunombre       VARCHAR(50) NULL,
    vch_moduestado       VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT chk_modulo_vch_moduestado CHECK (vch_moduestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    CONSTRAINT PK_Modulo PRIMARY KEY (int_moducodigo)
) ENGINE = INNODB;

CREATE TABLE Usuario (
    chr_emplcodigo       CHAR(4) NOT NULL,
    vch_emplusuario      VARCHAR(20) NOT NULL,
    vch_emplclave        VARCHAR(50) NOT NULL,
    vch_emplestado       VARCHAR(15) NULL DEFAULT 'ACTIVO',
    CONSTRAINT chk_usuario_vch_emplestado CHECK (vch_emplestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    CONSTRAINT PK_Usuario PRIMARY KEY (chr_emplcodigo),
    CONSTRAINT U_Usuario_vch_emplusuario UNIQUE (vch_emplusuario),
    FOREIGN KEY (chr_emplcodigo) REFERENCES Empleado (chr_emplcodigo)
) ENGINE = INNODB;

CREATE TABLE Permiso (
    chr_emplcodigo       CHAR(4) NOT NULL,
    int_moducodigo       INTEGER NOT NULL,
    vch_permestado       VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT chk_permiso_vch_permestado CHECK (vch_permestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    CONSTRAINT PK_Permiso PRIMARY KEY (chr_emplcodigo, int_moducodigo),
    FOREIGN KEY (int_moducodigo) REFERENCES Modulo (int_moducodigo),
    FOREIGN KEY (chr_emplcodigo) REFERENCES Usuario (chr_emplcodigo)
) ENGINE = INNODB;

CREATE TABLE LOGSESSION (
    ID                 INT NOT NULL AUTO_INCREMENT,
    chr_emplcodigo     CHAR(4) NOT NULL,
    fec_ingreso        DATETIME NOT NULL,
    fec_salida         DATETIME NULL,
    ip                 VARCHAR(20) NOT NULL DEFAULT 'NONE',
    hostname           VARCHAR(100) NOT NULL DEFAULT 'NONE',
    CONSTRAINT PK_LOG_SESSION PRIMARY KEY (ID),
    CONSTRAINT fk_log_session_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES Empleado (chr_emplcodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE Asignado (
    chr_asigcodigo       CHAR(6) NOT NULL,
    chr_sucucodigo       CHAR(3) NOT NULL,
    chr_emplcodigo       CHAR(4) NOT NULL,
    dtt_asigfechaalta    DATE NOT NULL,
    dtt_asigfechabaja    DATE NULL,
    CONSTRAINT PK_Asignado PRIMARY KEY (chr_asigcodigo),
    KEY idx_asignado01 (chr_emplcodigo),
    CONSTRAINT fk_asignado_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES Empleado (chr_emplcodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_asignado02 (chr_sucucodigo),
    CONSTRAINT fk_asignado_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES Sucursal (chr_sucucodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE Cliente (
    chr_cliecodigo       CHAR(5) NOT NULL,
    vch_cliepaterno      VARCHAR(25) NOT NULL,
    vch_cliematerno      VARCHAR(25) NOT NULL,
    vch_clienombre       VARCHAR(30) NOT NULL,
    chr_cliedni          CHAR(8) NOT NULL,
    vch_clieciudad       VARCHAR(30) NOT NULL,
    vch_cliedireccion    VARCHAR(50) NOT NULL,
    vch_clietelefono     VARCHAR(20) NULL,
    vch_clieemail        VARCHAR(50) NULL,
    CONSTRAINT PK_Cliente PRIMARY KEY (chr_cliecodigo)
) ENGINE = INNODB;

CREATE TABLE Moneda (
    chr_monecodigo       CHAR(2) NOT NULL,
    vch_monedescripcion  VARCHAR(20) NOT NULL,
    CONSTRAINT PK_Moneda PRIMARY KEY (chr_monecodigo)
) ENGINE = INNODB;

CREATE TABLE Cuenta (
    chr_cuencodigo       CHAR(8) NOT NULL,
    chr_monecodigo       CHAR(2) NOT NULL,
    chr_sucucodigo       CHAR(3) NOT NULL,
    chr_emplcreacuenta   CHAR(4) NOT NULL,
    chr_cliecodigo       CHAR(5) NOT NULL,
    dec_cuensaldo        DECIMAL(12,2) NOT NULL,
    dtt_cuenfechacreacion DATE NOT NULL,
    vch_cuenestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
    int_cuencontmov      INTEGER NOT NULL,
    chr_cuenclave        CHAR(6) DEFAULT '123456' NOT NULL,
    CONSTRAINT chk_cuenta_vch_cuenestado CHECK (vch_cuenestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    CONSTRAINT PK_Cuenta PRIMARY KEY (chr_cuencodigo),
    KEY idx_cuenta01 (chr_cliecodigo),
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (chr_cliecodigo) REFERENCES Cliente (chr_cliecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_cuenta02 (chr_emplcreacuenta),
    CONSTRAINT fk_cuenta_empleado FOREIGN KEY (chr_emplcreacuenta) REFERENCES Empleado (chr_emplcodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_cuenta03 (chr_sucucodigo),
    CONSTRAINT fk_cuenta_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES Sucursal (chr_sucucodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_cuenta04 (chr_monecodigo),
    CONSTRAINT fk_cuenta_moneda FOREIGN KEY (chr_monecodigo) REFERENCES Moneda (chr_monecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE Movimiento (
    chr_cuencodigo       CHAR(8) NOT NULL,
    int_movinumero       INTEGER NOT NULL,
    dtt_movifecha        DATE NOT NULL,
    chr_emplcodigo       CHAR(4) NOT NULL,
    chr_tipocodigo       CHAR(3) NOT NULL,
    dec_moviimporte      DECIMAL(12,2) NOT NULL,
    chr_cuenreferencia   CHAR(8) NULL,
    CONSTRAINT chk_movimiento_dec_moviimporte CHECK (dec_moviimporte >= 0.0),
    CONSTRAINT PK_Movimiento PRIMARY KEY (chr_cuencodigo, int_movinumero),
    KEY idx_movimiento01 (chr_tipocodigo),
    CONSTRAINT fk_movimiento_tipomovimiento FOREIGN KEY (chr_tipocodigo) REFERENCES TipoMovimiento (chr_tipocodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_movimiento02 (chr_emplcodigo),
    CONSTRAINT fk_movimiento_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES Empleado (chr_emplcodigo) ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY idx_movimiento03 (chr_cuencodigo),
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (chr_cuencodigo) REFERENCES Cuenta (chr_cuencodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE Parametro (
    chr_paracodigo       CHAR(3) NOT NULL,
    vch_paradescripcion  VARCHAR(50) NOT NULL,
    vch_paravalor        VARCHAR(70) NOT NULL,
    vch_paraestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
    CONSTRAINT chk_parametro_vch_paraestado CHECK (vch_paraestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    CONSTRAINT PK_Parametro PRIMARY KEY (chr_paracodigo)
) ENGINE = INNODB;

CREATE TABLE InteresMensual (
    chr_monecodigo       CHAR(2) NOT NULL,
    dec_inteimporte      DECIMAL(12,2) NOT NULL,
    CONSTRAINT PK_InteresMensual PRIMARY KEY (chr_monecodigo),
    KEY idx_interesmensual01 (chr_monecodigo),
    CONSTRAINT fk_interesmensual_moneda FOREIGN KEY (chr_monecodigo) REFERENCES Moneda (chr_monecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE CostoMovimiento (
    chr_monecodigo       CHAR(2) NOT NULL,
    dec_costimporte      DECIMAL(12,2) NOT NULL,
    CONSTRAINT PK_CostoMovimiento PRIMARY KEY (chr_monecodigo),
    KEY idx_costomovimiento (chr_monecodigo),
    CONSTRAINT fk_costomovimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES Moneda (chr_monecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE CargoMantenimiento (
    chr_monecodigo       CHAR(2) NOT NULL,
    dec_cargMontoMaximo  DECIMAL(12,2) NOT NULL,
    dec_cargImporte      DECIMAL(12,2) NOT NULL,
    CONSTRAINT PK_CargoMantenimiento PRIMARY KEY (chr_monecodigo),
    KEY idx_cargomantenimiento01 (chr_monecodigo),
    CONSTRAINT fk_cargomantenimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES Moneda (chr_monecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB;

CREATE TABLE Contador (
    vch_conttabla        VARCHAR(30) NOT NULL,
    int_contitem         INTEGER NOT NULL,
    int_contlongitud     INTEGER NOT NULL,
    CONSTRAINT PK_Contador PRIMARY KEY (vch_conttabla)
) ENGINE = INNODB;

-- =============================================================================
-- Universidad de las Fuerzas Armadas ESPE - Arquitectura de Software
-- NRC 36883 - Grupo 08 (Gustavo Molina, Daniela Tituaña)
-- Esquema: eurekabank (Core Bancario EurekaBank)
-- Archivo: Carga_Datos.sql (Inserción de Datos Iniciales de Prueba)
-- =============================================================================

USE eurekabank;

-- Tabla: Moneda
INSERT INTO Moneda VALUES ('01', 'Soles');
INSERT INTO Moneda VALUES ('02', 'Dolares');

-- Tabla: CargoMantenimiento
INSERT INTO CargoMantenimiento VALUES ('01', 3500.00, 7.00);
INSERT INTO CargoMantenimiento VALUES ('02', 1200.00, 2.50);

-- Tabla: CostoMovimiento
INSERT INTO CostoMovimiento VALUES ('01', 2.00);
INSERT INTO CostoMovimiento VALUES ('02', 0.60);

-- Tabla: InteresMensual
INSERT INTO InteresMensual VALUES ('01', 0.70);
INSERT INTO InteresMensual VALUES ('02', 0.60);

-- Tabla: TipoMovimiento
INSERT INTO TipoMovimiento VALUES ('001', 'Apertura de Cuenta', 'INGRESO', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('002', 'Cancelar Cuenta', 'SALIDA', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('003', 'Deposito', 'INGRESO', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('004', 'Retiro', 'SALIDA', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('005', 'Interes', 'INGRESO', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('006', 'Mantenimiento', 'SALIDA', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('007', 'ITF', 'SALIDA', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('008', 'Transferencia - Ingreso', 'INGRESO', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('009', 'Transferencia - Salida', 'SALIDA', 'ACTIVO');
INSERT INTO TipoMovimiento VALUES ('010', 'Cargo por Movimiento', 'SALIDA', 'ACTIVO');

-- Tabla: Sucursal
INSERT INTO Sucursal VALUES ('001', 'Sipan', 'Chiclayo', 'Av. Balta 1456', 2);
INSERT INTO Sucursal VALUES ('002', 'Chan Chan', 'Trujillo', 'Jr. Independencia 456', 3);
INSERT INTO Sucursal VALUES ('003', 'Los Olivos', 'Lima', 'Av. Central 1234', 1);
INSERT INTO Sucursal VALUES ('004', 'Pardo', 'Lima', 'Av. Pardo 345 - Miraflores', 0);
INSERT INTO Sucursal VALUES ('005', 'Misti', 'Arequipa', 'Bolivar 546', 0);
INSERT INTO Sucursal VALUES ('006', 'Machupicchu', 'Cusco', 'Calle El Sol 534', 0);
INSERT INTO Sucursal VALUES ('007', 'Grau', 'Piura', 'Av. Grau 1528', 0);

-- Tabla: Empleado
INSERT INTO Empleado VALUES ('9999', 'Internet', 'Internet', 'internet', 'Internet', 'internet');
INSERT INTO Empleado VALUES ('0000', 'Molina', 'Tituaña', 'Monster GR08', 'Quito', 'Av. Gral. Rumiñahui ESPE');
INSERT INTO Empleado VALUES ('0001', 'Romero', 'Castillo', 'Carlos Alberto', 'Trujillo', 'Calle 1 Nro. 456');
INSERT INTO Empleado VALUES ('0002', 'Castro', 'Vargas', 'Lidia', 'Lima', 'Federico Villarreal 456 - SMP');
INSERT INTO Empleado VALUES ('0003', 'Reyes', 'Ortiz', 'Claudia', 'Lima', 'Av. Aviación 3456 - San Borja');
INSERT INTO Empleado VALUES ('0004', 'Ramos', 'Garibay', 'Angelica', 'Chiclayo', 'Calle Barcelona 345');
INSERT INTO Empleado VALUES ('0005', 'Ruiz', 'Zabaleta', 'Claudia', 'Cusco', 'Calle Cruz Verde 364');
INSERT INTO Empleado VALUES ('0006', 'Cruz', 'Tarazona', 'Ricardo', 'Arequipa', 'Calle La Gruta 304');

-- Tabla: Usuario (Credenciales activas para pruebas en .NET y Java)
INSERT INTO Usuario VALUES ('9999', 'internet', 'internet', 'ACTIVO');
INSERT INTO Usuario VALUES ('0001', 'MONSTER', 'MONSTER9', 'ACTIVO');
INSERT INTO Usuario VALUES ('0002', 'cromero', 'chicho', 'ACTIVO');
INSERT INTO Usuario VALUES ('0003', 'lcastillo', 'cajero123', 'ACTIVO');
INSERT INTO Usuario VALUES ('0004', 'aramos', 'china', 'ACTIVO');

-- Tabla: Modulo
INSERT INTO Modulo VALUES (1, 'Procesos', 'ACTIVO');
INSERT INTO Modulo VALUES (2, 'Tablas', 'ACTIVO');
INSERT INTO Modulo VALUES (3, 'Consultas', 'ACTIVO');
INSERT INTO Modulo VALUES (4, 'Reportes', 'ACTIVO');
INSERT INTO Modulo VALUES (5, 'Util', 'ACTIVO');
INSERT INTO Modulo VALUES (6, 'Seguridad', 'ACTIVO');

-- Tabla: Permiso
INSERT INTO Permiso VALUES ('0001', 1, 'ACTIVO');
INSERT INTO Permiso VALUES ('0001', 2, 'ACTIVO');
INSERT INTO Permiso VALUES ('0001', 3, 'ACTIVO');
INSERT INTO Permiso VALUES ('0002', 1, 'ACTIVO');
INSERT INTO Permiso VALUES ('0002', 2, 'ACTIVO');
INSERT INTO Permiso VALUES ('0002', 3, 'ACTIVO');

-- Tabla: Asignado
INSERT INTO Asignado VALUES ('000001', '001', '0004', '2024-01-15', NULL);
INSERT INTO Asignado VALUES ('000002', '002', '0001', '2024-01-20', NULL);
INSERT INTO Asignado VALUES ('000003', '003', '0002', '2024-01-28', NULL);

-- Tabla: Parametro
INSERT INTO Parametro VALUES ('001', 'ITF - Impuesto a las Transacciones Financieras', '0.08', 'ACTIVO');
INSERT INTO Parametro VALUES ('002', 'Número de Operaciones Sin Costo', '15', 'ACTIVO');

-- Tabla: Cliente
INSERT INTO Cliente VALUES ('00001', 'GARCIA', 'LOPEZ', 'JUAN CARLOS', '45678912', 'LIMA', 'AV. BRASIL 123', '987654321', 'juan.garcia@email.com');
INSERT INTO Cliente VALUES ('00002', 'RODRIGUEZ', 'TORRES', 'MARIA FERNANDA', '47891234', 'LIMA', 'AV. PARDO 456', '987654322', 'maria.rodriguez@email.com');
INSERT INTO Cliente VALUES ('00003', 'SOTO', 'VALDEZ', 'CARLOS ALBERTO', '48912345', 'LIMA', 'JR. HUANCAVELICA 789', '987654323', 'carlos.soto@email.com');
INSERT INTO Cliente VALUES ('00004', 'CHAVEZ', 'PEREZ', 'ANA GABRIELA', '49123456', 'LIMA', 'AV. ANGAMOS 321', '987654324', 'ana.chavez@email.com');
INSERT INTO Cliente VALUES ('00005', 'VARGAS', 'DIAZ', 'PEDRO JOSE', '51234567', 'LIMA', 'CALLE LAS FLORES 654', '987654325', 'pedro.vargas@email.com');
INSERT INTO Cliente VALUES ('00006', 'MOLINA', 'GUAICO', 'GUSTAVO STIVEN', '17234567', 'QUITO', 'AV. RUMINAHUI ESPE', '0991234567', 'gsmolina@espe.edu.ec');
INSERT INTO Cliente VALUES ('00007', 'TITUANA', 'MORENO', 'DANIELA LISSETTE', '17345678', 'QUITO', 'AV. SIMON BOLIVAR', '0987654321', 'dltituana@espe.edu.ec');
INSERT INTO Cliente VALUES ('00008', 'CORONEL', 'CASTILLO', 'ERIC GUSTAVO', '06914897', 'LIMA', 'LOS OLIVOS', '996664457', 'gcoronelc@gmail.com');

-- Tabla: Cuenta
INSERT INTO Cuenta VALUES ('00100001', '01', '001', '0001', '00001', 5000.00, '2024-01-15', 'ACTIVO', 4, '123456');
INSERT INTO Cuenta VALUES ('00100002', '01', '001', '0001', '00002', 3500.50, '2024-01-20', 'ACTIVO', 3, '123456');
INSERT INTO Cuenta VALUES ('00100003', '02', '002', '0002', '00003', 2500.00, '2024-02-01', 'ACTIVO', 2, '123456');
INSERT INTO Cuenta VALUES ('00100004', '01', '002', '0003', '00004', 10000.00, '2024-02-10', 'ACTIVO', 3, '123456');
INSERT INTO Cuenta VALUES ('00100005', '02', '003', '0002', '00005', 1500.75, '2024-02-15', 'ACTIVO', 2, '123456');
INSERT INTO Cuenta VALUES ('00200001', '01', '002', '0001', '00006', 7000.00, '2024-03-01', 'ACTIVO', 3, '123456');
INSERT INTO Cuenta VALUES ('00200002', '01', '002', '0001', '00007', 6800.00, '2024-03-05', 'ACTIVO', 2, '123456');

-- Tabla: Movimiento
INSERT INTO Movimiento VALUES ('00100001', 1, '2024-01-15', '0001', '001', 1000.00, NULL);
INSERT INTO Movimiento VALUES ('00100001', 2, '2024-01-16', '0002', '003', 2000.00, NULL);
INSERT INTO Movimiento VALUES ('00100001', 3, '2024-01-20', '0002', '004', 500.00, NULL);
INSERT INTO Movimiento VALUES ('00100001', 4, '2024-01-25', '0003', '003', 2500.00, NULL);

INSERT INTO Movimiento VALUES ('00100002', 1, '2024-01-20', '0001', '001', 500.00, NULL);
INSERT INTO Movimiento VALUES ('00100002', 2, '2024-01-22', '0002', '003', 1500.00, NULL);
INSERT INTO Movimiento VALUES ('00100002', 3, '2024-01-28', '0004', '003', 1500.50, NULL);

INSERT INTO Movimiento VALUES ('00100003', 1, '2024-02-01', '0001', '001', 1000.00, NULL);
INSERT INTO Movimiento VALUES ('00100003', 2, '2024-02-05', '0002', '003', 1500.00, NULL);

INSERT INTO Movimiento VALUES ('00100004', 1, '2024-02-10', '0001', '001', 5000.00, NULL);
INSERT INTO Movimiento VALUES ('00100004', 2, '2024-02-12', '0003', '003', 3000.00, NULL);
INSERT INTO Movimiento VALUES ('00100004', 3, '2024-02-15', '0004', '003', 2000.00, NULL);

INSERT INTO Movimiento VALUES ('00100005', 1, '2024-02-15', '0002', '001', 500.00, NULL);
INSERT INTO Movimiento VALUES ('00100005', 2, '2024-02-18', '0004', '003', 1000.75, NULL);

INSERT INTO Movimiento VALUES ('00200001', 1, '2024-03-01', '0001', '001', 5000.00, NULL);
INSERT INTO Movimiento VALUES ('00200001', 2, '2024-03-02', '0001', '003', 3000.00, NULL);
INSERT INTO Movimiento VALUES ('00200001', 3, '2024-03-04', '0001', '004', 1000.00, NULL);

INSERT INTO Movimiento VALUES ('00200002', 1, '2024-03-05', '0001', '001', 4800.00, NULL);
INSERT INTO Movimiento VALUES ('00200002', 2, '2024-03-06', '0001', '003', 2000.00, NULL);

-- Tabla: Contador
INSERT INTO Contador VALUES ('Moneda', 2, 2);
INSERT INTO Contador VALUES ('TipoMovimiento', 10, 3);
INSERT INTO Contador VALUES ('Sucursal', 7, 3);
INSERT INTO Contador VALUES ('Empleado', 7, 4);
INSERT INTO Contador VALUES ('Asignado', 3, 6);
INSERT INTO Contador VALUES ('Parametro', 2, 3);
INSERT INTO Contador VALUES ('Cliente', 8, 5);
