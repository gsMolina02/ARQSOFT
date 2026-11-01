/*==============================================================*/
/* Sistema       : EurekaBank - Sistema de Cuentas de Ahorro        */
/* Grupo         : GR08 - NRC 36883 - PAO 202651                   */
/* Script        : 03 - Usuario de MySQL para los servidores      */
/* Descripción   : el servicio web se conecta con este usuario,    */
/*                 que solo tiene permisos de datos sobre          */
/*                 eurekabank y solo desde la misma computadora.  */
/*                 Los clientes nunca se conectan a MySQL.        */
/*==============================================================*/

drop user if exists 'Monster'@'localhost';

create user 'Monster'@'localhost' identified by 'Monster9';

grant select, insert, update, delete on eurekabank.* to 'Monster'@'localhost';

flush privileges;
