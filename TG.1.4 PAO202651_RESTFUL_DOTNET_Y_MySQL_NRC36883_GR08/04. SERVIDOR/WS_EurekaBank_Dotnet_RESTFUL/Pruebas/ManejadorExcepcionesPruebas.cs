using System.Data.Common;
using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Servicios;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ManejadorExcepcionesPruebas
{
    [Fact]
    public void DeberiaUsarElCodigoDeCadaExcepcionPropia()
    {
        Assert.Equal(400, ManejadorExcepciones.Traducir(new ExcepcionValidacion("v")).Codigo);
        Assert.Equal(401, ManejadorExcepciones.Traducir(new ExcepcionAutenticacion("a")).Codigo);
        Assert.Equal(404, ManejadorExcepciones.Traducir(new ExcepcionNoEncontrado("n")).Codigo);

        Respuesta conflicto = ManejadorExcepciones.Traducir(new ExcepcionReglaNegocio("La cuenta 00100003 no está activa"));
        Assert.Equal((409, "La cuenta 00100003 no está activa"), (conflicto.Codigo, conflicto.Mensaje));
    }

    [Fact]
    public void DeberiaResponder500SinDetallesSiFallaLaBaseDeDatos()
    {
        Respuesta respuesta = ManejadorExcepciones.Traducir(new ExcepcionBaseDatosFalsa());

        Assert.Equal((500, ManejadorExcepciones.MensajeBaseDatos), (respuesta.Codigo, respuesta.Mensaje));
    }

    [Fact]
    public void DeberiaOcultarLosErroresInesperados()
    {
        Respuesta respuesta = ManejadorExcepciones.Traducir(new InvalidOperationException("detalle interno"));

        Assert.Equal((500, ManejadorExcepciones.MensajeInterno), (respuesta.Codigo, respuesta.Mensaje));
    }

    [Fact]
    public void DeberiaExplicarUnImporteQueNoEsNumero()
    {
        var estado = new ModelStateDictionary();
        estado.AddModelError("$.importe", "The JSON value could not be converted to System.Nullable`1[System.Decimal].");

        Assert.Equal(ValidadorDatos.MensajeImporte, ManejadorExcepciones.MensajeModeloInvalido(estado));
    }

    [Fact]
    public void DeberiaAvisarDeUnJsonMalFormado()
    {
        var estado = new ModelStateDictionary();
        estado.AddModelError("$", "'x' is an invalid start of a value.");

        Assert.Equal(ManejadorExcepciones.MensajeJsonInvalido, ManejadorExcepciones.MensajeModeloInvalido(estado));
    }

    private sealed class ExcepcionBaseDatosFalsa() : DbException("Unable to connect to any of the specified MySQL hosts.");
}
