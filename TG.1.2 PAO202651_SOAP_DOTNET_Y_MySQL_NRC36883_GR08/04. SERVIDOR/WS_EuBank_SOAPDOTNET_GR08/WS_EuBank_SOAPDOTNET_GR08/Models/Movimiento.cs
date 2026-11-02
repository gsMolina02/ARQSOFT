using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [Table("movimiento")]
    public class Movimiento
    {
        [Key]
        [Column("chr_cuencodigo", Order = 0)]
        public string ChrCuenCodigo { get; set; }

        [Key]
        [Column("int_movinumero", Order = 1)]
        public int IntMoviNumero { get; set; }

        [Column("dtt_movifecha")]
        public DateTime DttMoviFecha { get; set; }

        [Column("chr_emplcodigo")]
        public string ChrEmplCodigo { get; set; }

        [Column("chr_tipocodigo")]
        public string ChrTipoCodigo { get; set; }

        [Column("dec_moviimporte")]
        public decimal DecMoviImporte { get; set; }

        [Column("chr_cuenreferencia")]
        public string ChrCuenReferencia { get; set; }

        public virtual Cuenta Cuenta { get; set; }
    }
}
