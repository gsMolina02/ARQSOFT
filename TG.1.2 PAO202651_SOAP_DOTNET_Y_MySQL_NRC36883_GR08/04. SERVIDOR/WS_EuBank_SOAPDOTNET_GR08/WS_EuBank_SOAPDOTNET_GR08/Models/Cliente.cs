using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [Table("cliente")]
    public class Cliente
    {
        [Key]
        [Column("chr_cliecodigo")]
        public string ChrClieCodigo { get; set; }

        [Column("vch_cliepaterno")]
        public string VchCliePaterno { get; set; }

        [Column("vch_cliematerno")]
        public string VchClieMaterno { get; set; }

        [Column("vch_clienombre")]
        public string VchClieNombre { get; set; }

        [Column("chr_cliedni")]
        public string ChrClieDni { get; set; }

        [Column("vch_clieciudad")]
        public string VchClieCiudad { get; set; }

        [Column("vch_cliedireccion")]
        public string VchClieDireccion { get; set; }

        [Column("vch_clietelefono")]
        public string VchClieTelefono { get; set; }

        [Column("vch_clieemail")]
        public string VchClieEmail { get; set; }
    }
}
