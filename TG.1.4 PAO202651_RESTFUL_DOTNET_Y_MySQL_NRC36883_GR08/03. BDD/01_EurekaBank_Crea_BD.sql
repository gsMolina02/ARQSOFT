/*==============================================================*/
/* Sistema       : EurekaBank - Sistema de Cuentas de Ahorro    */
/* Grupo         : GR08 - NRC 36883 - PAO 202651                */
/* Autores       : Daniela Tituaña, Gustavo Molina              */
/* Script        : 01 - Creación de la base de datos            */
/* DBMS          : MySQL Server 8.0 (InnoDB, utf8mb4)           */
/* Origen        : generado desde el modelo físico              */
/*                 02. MER/EurekaBank_Fisico_GR8.pdm            */
/*                 (PowerDesigner 16.6, CDM -> LDM -> PDM)      */
/* ATENCIÓN      : elimina la base eurekabank si ya existe.     */
/*==============================================================*/

drop database if exists eurekabank;

create database eurekabank character set utf8mb4 collate utf8mb4_spanish_ci;

use eurekabank;

set names utf8mb4;

drop table if exists cliente;

drop table if exists cuenta;

drop table if exists empleado;

drop table if exists moneda;

drop table if exists movimiento;

drop table if exists sesion;

drop table if exists sucursal;

drop table if exists tipo_movimiento;

drop table if exists usuario;

/*==============================================================*/
/* Table: cliente                                               */
/*==============================================================*/
create table cliente
(
   codigo_cliente       char(5) not null,
   cedula               char(10) not null,
   nombres              varchar(50) not null,
   apellidos            varchar(50) not null,
   direccion            varchar(100) not null,
   telefono             varchar(15),
   correo               varchar(80),
   primary key (codigo_cliente)
);

alter table cliente comment 'Personas titulares de las cuentas de ahorro.';

/*==============================================================*/
/* Index: uk_cliente_cedula                                     */
/*==============================================================*/
create unique index uk_cliente_cedula on cliente
(
   cedula
);

/*==============================================================*/
/* Table: cuenta                                                */
/*==============================================================*/
create table cuenta
(
   numero_cuenta        char(8) not null,
   codigo_cliente       char(5) not null,
   codigo_moneda        char(3) not null,
   codigo_sucursal      char(3) not null,
   saldo                decimal(12,2) not null,
   fecha_apertura       date not null,
   estado               varchar(10) not null default 'ACTIVO',
   contador_movimientos int not null default 0,
   primary key (numero_cuenta)
);

alter table cuenta comment 'Cuentas de ahorro (ACTIVO, ANULADO o CANCELADO).';

/*==============================================================*/
/* Table: empleado                                              */
/*==============================================================*/
create table empleado
(
   codigo_empleado      char(4) not null,
   nombres              varchar(50) not null,
   apellidos            varchar(50) not null,
   primary key (codigo_empleado)
);

alter table empleado comment 'Empleados del banco que registran los movimientos.';

/*==============================================================*/
/* Table: moneda                                                */
/*==============================================================*/
create table moneda
(
   codigo_moneda        char(3) not null,
   nombre               varchar(50) not null,
   simbolo              varchar(5) not null,
   primary key (codigo_moneda)
);

alter table moneda comment 'Monedas de las cuentas (el sistema opera solo en USD).';

/*==============================================================*/
/* Table: movimiento                                            */
/*==============================================================*/
create table movimiento
(
   numero_cuenta        char(8) not null,
   numero_movimiento    int not null,
   codigo_tipo          char(3) not null,
   codigo_empleado      char(4) not null,
   cuenta_referencia    char(8),
   fecha_hora           datetime not null,
   importe              decimal(12,2) not null,
   primary key (numero_cuenta, numero_movimiento)
);

alter table movimiento comment 'Movimientos de cada cuenta, numerados por cuenta.';

/*==============================================================*/
/* Table: sesion                                                */
/*==============================================================*/
create table sesion
(
   token                char(36) not null,
   nombre_usuario       varchar(20) not null,
   fecha_ingreso        datetime not null,
   ultima_actividad     datetime not null,
   fecha_salida         datetime,
   ip                   varchar(45) not null,
   primary key (token)
);

alter table sesion comment 'Sesiones de los usuarios; vencen tras 30 min sin uso.';

/*==============================================================*/
/* Table: sucursal                                              */
/*==============================================================*/
create table sucursal
(
   codigo_sucursal      char(3) not null,
   nombre               varchar(50) not null,
   ciudad               varchar(30) not null,
   direccion            varchar(100) not null,
   primary key (codigo_sucursal)
);

alter table sucursal comment 'Sucursales del banco.';

/*==============================================================*/
/* Table: tipo_movimiento                                       */
/*==============================================================*/
create table tipo_movimiento
(
   codigo_tipo          char(3) not null,
   descripcion          varchar(40) not null,
   accion               varchar(10) not null,
   primary key (codigo_tipo)
);

alter table tipo_movimiento comment 'Tipos de movimiento (001, 003, 004, 008 y 009).';

/*==============================================================*/
/* Table: usuario                                               */
/*==============================================================*/
create table usuario
(
   nombre_usuario       varchar(20) not null,
   codigo_empleado      char(4) not null,
   hash_clave           char(64) not null,
   estado               varchar(10) not null default 'ACTIVO',
   primary key (nombre_usuario)
);

alter table usuario comment 'Usuarios de acceso; la clave se guarda en SHA-256.';

/*==============================================================*/
/* Index: uk_usuario_empleado                                   */
/*==============================================================*/
create unique index uk_usuario_empleado on usuario
(
   codigo_empleado
);

alter table cuenta add constraint FK_cuenta_cliente foreign key (codigo_cliente)
      references cliente (codigo_cliente) on delete restrict on update restrict;

alter table cuenta add constraint FK_cuenta_moneda foreign key (codigo_moneda)
      references moneda (codigo_moneda) on delete restrict on update restrict;

alter table cuenta add constraint FK_cuenta_sucursal foreign key (codigo_sucursal)
      references sucursal (codigo_sucursal) on delete restrict on update restrict;

alter table movimiento add constraint FK_movimiento_cuenta foreign key (numero_cuenta)
      references cuenta (numero_cuenta) on delete restrict on update restrict;

alter table movimiento add constraint FK_movimiento_empleado foreign key (codigo_empleado)
      references empleado (codigo_empleado) on delete restrict on update restrict;

alter table movimiento add constraint FK_movimiento_referencia foreign key (cuenta_referencia)
      references cuenta (numero_cuenta) on delete restrict on update restrict;

alter table movimiento add constraint FK_movimiento_tipo foreign key (codigo_tipo)
      references tipo_movimiento (codigo_tipo) on delete restrict on update restrict;

alter table sesion add constraint FK_sesion_usuario foreign key (nombre_usuario)
      references usuario (nombre_usuario) on delete restrict on update restrict;

alter table usuario add constraint FK_usuario_empleado foreign key (codigo_empleado)
      references empleado (codigo_empleado) on delete restrict on update restrict;
