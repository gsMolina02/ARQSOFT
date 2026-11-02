using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [Table("tipomovimiento")]
    public class TipoMovimiento
    {
        [Key]
        [Column("chr_tipocodigo")]
        public string ChrTipoCodigo { get; set; }

        [Column("vch_tipodescripcion")]
        public string VchTipoDescripcion { get; set; }

        [Column("vch_tipoaccion")]
        public string VchTipoAccion { get; set; }

        [Column("vch_tipoestado")]
        public string VchTipoEstado { get; set; }
    }
}
