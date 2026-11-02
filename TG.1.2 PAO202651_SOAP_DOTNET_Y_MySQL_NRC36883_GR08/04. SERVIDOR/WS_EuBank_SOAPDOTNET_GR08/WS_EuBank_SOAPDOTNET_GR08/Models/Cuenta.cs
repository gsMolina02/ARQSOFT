using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [Table("cuenta")]
    public class Cuenta
    {
        [Key]
        [Column("chr_cuencodigo")]
        public string ChrCuenCodigo { get; set; }

        [Column("chr_monecodigo")]
        public string ChrMoneCodigo { get; set; }

        [Column("chr_sucucodigo")]
        public string ChrSucuCodigo { get; set; }

        [Column("chr_emplcreacuenta")]
        public string ChrEmplCreaCuenta { get; set; }

        [Column("chr_cliecodigo")]
        public string ChrClieCodigo { get; set; }

        [Column("dec_cuensaldo")]
        public decimal DecCuenSaldo { get; set; }

        [Column("dtt_cuenfechacreacion")]
        public DateTime DttCuenFechaCreacion { get; set; }

        [Column("vch_cuenestado")]
        public string VchCuenEstado { get; set; }

        [Column("int_cuencontmov")]
        public int IntCuenContMov { get; set; }

        [Column("chr_cuenclave")]
        public string ChrCuenClave { get; set; }

        public virtual ICollection<Movimiento> Movimientos { get; set; }
    }
}
