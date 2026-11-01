/*==============================================================*/
/* Sistema       : EurekaBank - Sistema de Cuentas de Ahorro    */
/* Grupo         : GR08 - NRC 36883 - PAO 202651                */
/* Autores       : Daniela Tituaña, Gustavo Molina              */
/* Script        : 02 - Carga de datos de prueba                */
/* DBMS          : MySQL Server 8.0 (InnoDB, utf8mb4)           */
/* Moneda        : solo USD (dólar estadounidense)              */
/* Credenciales  : usuario Monster / clave Monster9             */
/*                 (la clave se guarda como hash SHA-256)       */
/* Coherencia    : el saldo y el contador de cada cuenta        */
/*                 cuadran con sus movimientos.                 */
/*==============================================================*/

use eurekabank;

set names utf8mb4;

start transaction;

-- Tabla: moneda

insert into moneda values ( 'USD', 'Dólar estadounidense', '$' );

-- Tabla: tipo_movimiento

insert into tipo_movimiento values ( '001', 'Apertura de cuenta', 'INGRESO' );
insert into tipo_movimiento values ( '003', 'Depósito', 'INGRESO' );
insert into tipo_movimiento values ( '004', 'Retiro', 'SALIDA' );
insert into tipo_movimiento values ( '008', 'Transferencia recibida', 'INGRESO' );
insert into tipo_movimiento values ( '009', 'Transferencia enviada', 'SALIDA' );

-- Tabla: sucursal

insert into sucursal values ( '001', 'Matriz Quito', 'Quito', 'Av. Amazonas N34-120 y Av. Atahualpa' );
insert into sucursal values ( '002', 'Sucursal Sangolquí', 'Sangolquí', 'Av. General Enríquez y Calle Riofrío' );
insert into sucursal values ( '003', 'Sucursal Guayaquil', 'Guayaquil', 'Av. 9 de Octubre 1120 y Pedro Moncayo' );
insert into sucursal values ( '004', 'Sucursal Cuenca', 'Cuenca', 'Av. Remigio Crespo y Av. Solano' );

-- Tabla: empleado

insert into empleado values ( '0001', 'Monster', 'EurekaBank' );

-- Tabla: usuario

insert into usuario values ( 'Monster', '0001', '7fbdfcdd431cb77a5bce52182f863d807544fdbea2edb49dc396d5ba30481f64', 'ACTIVO' );

-- Tabla: cliente

insert into cliente values ( '00001', '1712345675', 'Carlos Andrés', 'Pérez Salazar', 'Av. 6 de Diciembre N25-32, Quito', '0991234567', 'carlos.perez@correo.ec' );
insert into cliente values ( '00002', '1723456784', 'María Fernanda', 'López Andrade', 'Calle Montúfar 452, Sangolquí', '0987654321', 'maria.lopez@correo.ec' );
insert into cliente values ( '00003', '0914567896', 'José Luis', 'Villamar Cedeño', 'Cdla. Kennedy Norte Mz. 5, Guayaquil', '0998765432', 'jose.villamar@correo.ec' );
insert into cliente values ( '00004', '0105678916', 'Ana Lucía', 'Ordóñez Vintimilla', 'Calle Larga 7-45, Cuenca', '0976543210', null );
insert into cliente values ( '00005', '1806789127', 'Diego Sebastián', 'Naranjo Toapanta', 'Av. Cevallos 12-30, Ambato', null, 'diego.naranjo@correo.ec' );
insert into cliente values ( '00006', '1737891232', 'Gabriela Estefanía', 'Chávez Morales', 'Av. Mariscal Sucre S12-80, Quito', '0965432109', 'gabriela.chavez@correo.ec' );

-- Tabla: cuenta (número, cliente, moneda, sucursal, saldo, apertura, estado, contador)

insert into cuenta values ( '00100001', '00001', 'USD', '001', '540.00', '2026-01-15', 'ACTIVO', 4 );
insert into cuenta values ( '00100002', '00001', 'USD', '001', '400.00', '2026-03-02', 'ACTIVO', 2 );
insert into cuenta values ( '00200001', '00002', 'USD', '002', '530.25', '2026-02-10', 'ACTIVO', 3 );
insert into cuenta values ( '00300001', '00003', 'USD', '003', '579.50', '2026-02-20', 'ACTIVO', 3 );
insert into cuenta values ( '00400001', '00004', 'USD', '004', '575.00', '2026-04-05', 'ACTIVO', 3 );
insert into cuenta values ( '00100003', '00005', 'USD', '001', '20.00', '2026-05-12', 'ANULADO', 1 );
insert into cuenta values ( '00200002', '00006', 'USD', '002', '220.75', '2026-06-01', 'ACTIVO', 3 );
insert into cuenta values ( '00300002', '00006', 'USD', '003', '0.00', '2026-03-18', 'CANCELADO', 2 );

-- Tabla: movimiento (cuenta, número, tipo, empleado, cuenta de referencia, fecha y hora, importe)

insert into movimiento values ( '00100001', 1, '001', '0001', null, '2026-01-15 09:10:00', '500.00' );
insert into movimiento values ( '00100001', 2, '003', '0001', null, '2026-06-10 12:00:00', '250.00' );
insert into movimiento values ( '00100001', 3, '009', '0001', '00200001', '2026-08-05 10:30:00', '150.00' );
insert into movimiento values ( '00100001', 4, '004', '0001', null, '2026-10-01 12:40:00', '60.00' );
insert into movimiento values ( '00100002', 1, '001', '0001', null, '2026-03-02 15:45:00', '100.00' );
insert into movimiento values ( '00100002', 2, '008', '0001', '00300001', '2026-09-03 17:20:00', '300.00' );
insert into movimiento values ( '00100003', 1, '001', '0001', null, '2026-05-12 14:00:00', '20.00' );
insert into movimiento values ( '00200001', 1, '001', '0001', null, '2026-02-10 10:00:00', '300.00' );
insert into movimiento values ( '00200001', 2, '003', '0001', null, '2026-07-01 09:45:00', '80.25' );
insert into movimiento values ( '00200001', 3, '008', '0001', '00100001', '2026-08-05 10:30:00', '150.00' );
insert into movimiento values ( '00200002', 1, '001', '0001', null, '2026-06-01 10:15:00', '200.00' );
insert into movimiento values ( '00200002', 2, '003', '0001', null, '2026-09-15 08:50:00', '45.75' );
insert into movimiento values ( '00200002', 3, '009', '0001', '00400001', '2026-10-20 15:05:00', '25.00' );
insert into movimiento values ( '00300001', 1, '001', '0001', null, '2026-02-20 11:30:00', '1000.00' );
insert into movimiento values ( '00300001', 2, '004', '0001', null, '2026-06-15 16:30:00', '120.50' );
insert into movimiento values ( '00300001', 3, '009', '0001', '00100002', '2026-09-03 17:20:00', '300.00' );
insert into movimiento values ( '00300002', 1, '001', '0001', null, '2026-03-18 08:20:00', '50.00' );
insert into movimiento values ( '00300002', 2, '004', '0001', null, '2026-07-20 13:10:00', '50.00' );
insert into movimiento values ( '00400001', 1, '001', '0001', null, '2026-04-05 09:00:00', '750.00' );
insert into movimiento values ( '00400001', 2, '004', '0001', null, '2026-08-12 11:00:00', '200.00' );
insert into movimiento values ( '00400001', 3, '008', '0001', '00200002', '2026-10-20 15:05:00', '25.00' );

commit;
