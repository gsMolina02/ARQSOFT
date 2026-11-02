using System.Data.Entity;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    public class EurekaBankContext : DbContext
    {
        public EurekaBankContext() : base("name=EurekaBankDB") { }

        public DbSet<Cuenta> Cuentas { get; set; }
        public DbSet<Movimiento> Movimientos { get; set; }
        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Cliente> Clientes { get; set; }
        public DbSet<TipoMovimiento> TiposMovimiento { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Cuenta>().HasKey(c => c.ChrCuenCodigo);
            modelBuilder.Entity<Movimiento>().HasKey(m => new { m.ChrCuenCodigo, m.IntMoviNumero });
            modelBuilder.Entity<Usuario>().HasKey(u => u.ChrEmplCodigo);
            modelBuilder.Entity<Cliente>().HasKey(cl => cl.ChrClieCodigo);
            modelBuilder.Entity<TipoMovimiento>().HasKey(tm => tm.ChrTipoCodigo);
        }
    }
}
