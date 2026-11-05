using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class FormatoUtilPruebas
{
    [Theory]
    [InlineData("540", "$540.00")]
    [InlineData("1250.5", "$1,250.50")]
    [InlineData("0", "$0.00")]
    public void DeberiaFormatearMontosConDosDecimales(string valor, string esperado)
    {
        Assert.Equal(esperado, FormatoUtil.FormatearMonto(decimal.Parse(valor, System.Globalization.CultureInfo.InvariantCulture)));
    }

    [Fact]
    public void DeberiaMostrarElSignoSegunLaAccionDelMovimiento()
    {
        Assert.Equal("+$100.00", FormatoUtil.FormatearMovimiento(100m, esIngreso: true));
        Assert.Equal("-$50.25", FormatoUtil.FormatearMovimiento(50.25m, esIngreso: false));
    }

    [Fact]
    public void DeberiaFormatearFechas()
    {
        Assert.Equal("15/01/2026 09:10:00", FormatoUtil.FormatearFechaHora(new DateTime(2026, 1, 15, 9, 10, 0)));
        Assert.Equal("15/01/2026", FormatoUtil.FormatearFecha(new DateOnly(2026, 1, 15)));
    }

    [Fact]
    public void DeberiaMostrarElEstadoSoloDeLasCuentasNoActivas()
    {
        Assert.Equal("00100001 · Carlos Pérez",
                FormatoUtil.FormatearOpcionCuenta(new CuentaDto { Numero = "00100001", Cliente = "Carlos Pérez", Estado = "ACTIVO" }));
        Assert.Equal("00300002 · Ana Torres (CANCELADO)",
                FormatoUtil.FormatearOpcionCuenta(new CuentaDto { Numero = "00300002", Cliente = "Ana Torres", Estado = "CANCELADO" }));
    }
}
