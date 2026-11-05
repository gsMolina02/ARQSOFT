using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ValidacionUtilPruebas
{
    [Theory]
    [InlineData("00100001", "00100001")]
    [InlineData(" 00200001 ", "00200001")]
    public void DeberiaAceptarCuentasDeOchoDigitos(string entrada, string esperada)
    {
        Assert.Equal(esperada, ValidacionUtil.ValidarCuenta(entrada));
    }

    [Theory]
    [InlineData("1234567")]
    [InlineData("123456789")]
    [InlineData("0010000a")]
    [InlineData("")]
    [InlineData(null)]
    public void DeberiaRechazarCuentasSinOchoDigitos(string? entrada)
    {
        var error = Assert.Throws<ArgumentException>(() => ValidacionUtil.ValidarCuenta(entrada));

        Assert.Equal(ValidacionUtil.MensajeCuenta, error.Message);
    }

    [Theory]
    [InlineData("100", "100")]
    [InlineData("25.5", "25.5")]
    [InlineData("25,50", "25.50")]
    [InlineData("0.01", "0.01")]
    public void DeberiaInterpretarImportesConPuntoOComa(string entrada, string esperado)
    {
        Assert.Equal(decimal.Parse(esperado, System.Globalization.CultureInfo.InvariantCulture),
                ValidacionUtil.InterpretarImporte(entrada));
    }

    [Theory]
    [InlineData("0")]
    [InlineData("-5")]
    [InlineData("10.555")]
    [InlineData("abc")]
    [InlineData("1,000.50")]
    [InlineData("1e5")]
    [InlineData("")]
    public void DeberiaRechazarImportesNoValidos(string entrada)
    {
        var error = Assert.Throws<ArgumentException>(() => ValidacionUtil.InterpretarImporte(entrada));

        Assert.Equal(ValidacionUtil.MensajeImporte, error.Message);
    }

    [Fact]
    public void DeberiaRechazarImportesMayoresAlMaximo()
    {
        var error = Assert.Throws<ArgumentException>(() => ValidacionUtil.InterpretarImporte("10000000000"));

        Assert.Equal(ValidacionUtil.MensajeImporteMaximo, error.Message);
    }

    [Theory]
    [InlineData("Monster9", "abc", "abc", ValidacionUtil.MensajeClaveNueva)]
    [InlineData("Monster9", "Monster9", "Monster9", ValidacionUtil.MensajeClaveNueva)]
    [InlineData("Monster9", "NuevaClave", "OtraClave", ValidacionUtil.MensajeConfirmacion)]
    public void DeberiaValidarElCambioDeContrasenia(string actual, string nueva, string confirmacion, string mensaje)
    {
        var error = Assert.Throws<ArgumentException>(() => ValidacionUtil.ValidarCambioClave(actual, nueva, confirmacion));

        Assert.Equal(mensaje, error.Message);
    }

    [Theory]
    [InlineData('5', "", 0, true)]
    [InlineData('.', "25", 2, true)]
    [InlineData(',', "25", 2, true)]
    [InlineData('.', "25.5", 4, false)]
    [InlineData('7', "25.50", 5, false)]
    [InlineData('7', "25.5", 4, true)]
    [InlineData('1', "25.50", 0, true)]
    [InlineData('a', "25", 2, false)]
    [InlineData('-', "", 0, false)]
    [InlineData('\b', "25", 2, true)]
    public void DeberiaFiltrarLasTeclasDelImporte(char tecla, string texto, int cursor, bool permitida)
    {
        Assert.Equal(permitida, ValidacionUtil.EsCaracterImportePermitido(tecla, texto, cursor));
    }
}
