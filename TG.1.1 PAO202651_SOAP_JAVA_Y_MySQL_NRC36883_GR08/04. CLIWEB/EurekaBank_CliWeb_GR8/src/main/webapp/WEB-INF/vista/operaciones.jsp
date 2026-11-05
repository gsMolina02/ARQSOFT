<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags" %>
<c:set var="titulo" value="${modelo.operacion.nombreVisible}" />
<c:set var="operacion" value="${modelo.operacion}" />
<%@ include file="fragmentos/cabeceraApp.jspf" %>
<div class="rejilla-conversor">
    <div class="columna-principal">
        <nav class="pestanias" aria-label="Operación">
            <c:forEach var="pestania" items="${modelo.operaciones}">
                <c:url var="enlacePestania" value="/operaciones">
                    <c:param name="operacion" value="${pestania.parametro}" />
                    <c:if test="${modelo.hayConsulta}">
                        <c:param name="cuenta" value="${modelo.numeroCuenta}" />
                    </c:if>
                </c:url>
                <a class="pestania${pestania == operacion ? ' pestania-activa' : ''}" href="${enlacePestania}"
                   ${pestania == operacion ? 'aria-current="page"' : ''}><c:out value="${pestania.nombreVisible}" /></a>
            </c:forEach>
        </nav>

        <form action="${raiz}/operaciones/ejecutar" method="post" class="tarjeta formulario" data-bloquear-envio="Procesando..."
              <c:if test="${operacion.llevaImporte}">
                  data-confirmar-plantilla="<c:out value='${operacion.plantillaConfirmacion}' />"
                  data-confirmar-titulo="<c:out value='${operacion.tituloConfirmacion}' />"
                  data-confirmar-boton="<c:out value='${operacion.botonConfirmacion}' />"
              </c:if>>
            <input type="hidden" name="_token" value="${tokenFormulario}" />
            <input type="hidden" name="operacion" value="${operacion.parametro}" />
            <p class="texto-tenue"><c:out value="${operacion.ayuda}" /></p>

            <c:choose>
                <c:when test="${operacion.transferencia}">
                    <div class="fila-cuentas">
                        <div>
                            <label for="Cuenta" class="etiqueta">Cuenta de origen</label>
                            <e:campoCuenta id="Cuenta" nombre="cuenta" valor="${modelo.cuenta}" ayuda="Ej.: 00100001"
                                           conError="${modelo.campoConError == 'cuenta'}" autofoco="true" cuentas="${modelo.cuentas}" />
                        </div>
                        <div>
                            <label for="CuentaDestino" class="etiqueta">Cuenta de destino</label>
                            <e:campoCuenta id="CuentaDestino" nombre="cuentaDestino" valor="${modelo.cuentaDestino}" ayuda="Ej.: 00200001"
                                           conError="${modelo.campoConError == 'destino'}" autofoco="false" cuentas="${modelo.cuentas}" />
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <label for="Cuenta" class="etiqueta">Número de cuenta</label>
                    <e:campoCuenta id="Cuenta" nombre="cuenta" valor="${modelo.cuenta}" ayuda="Ejemplo: 00100001"
                                   conError="${modelo.campoConError == 'cuenta'}" autofoco="true" cuentas="${modelo.cuentas}" />
                </c:otherwise>
            </c:choose>

            <c:if test="${operacion.llevaImporte}">
                <label for="Importe" class="etiqueta">Importe en USD</label>
                <input type="text" class="campo-texto campo-grande${modelo.campoConError == 'importe' ? ' campo-invalido' : ''}"
                       id="Importe" name="importe" value="<c:out value='${modelo.importe}' />" placeholder="Ejemplo: 100 o 25.50"
                       maxlength="16" inputmode="decimal" autocomplete="off" data-patron="^\d{0,13}([.,]\d{0,2})?$" />
            </c:if>

            <c:if test="${not empty modelo.error}">
                <div class="mensaje-error" role="alert"><c:out value="${modelo.error}" /></div>
            </c:if>

            <button type="submit" class="boton boton-primario boton-ancho boton-grande"><c:out value="${operacion.boton}" /></button>
        </form>

        <c:if test="${modelo.sinConexion}">
            <%-- La dirección del servidor solo se cambia sin sesión: el token vale únicamente en el servidor que lo emitió --%>
            <form action="${raiz}/sesion/cerrar-sesion" method="post" class="fila-sin-conexion">
                <input type="hidden" name="_token" value="${tokenFormulario}" />
                <input type="hidden" name="configurar" value="true" />
                <button type="submit" class="boton boton-secundario">Cerrar sesión y configurar el servidor</button>
            </form>
        </c:if>

        <section class="tarjeta-resultado" aria-live="polite">
            <p class="resultado-titulo"><c:out value="${modelo.resultadoTitulo}" /></p>
            <p class="resultado-valor" title="<c:out value='${modelo.resultadoValor}' />"><c:out value="${modelo.resultadoValor}" /></p>
        </section>
    </div>

    <aside class="tarjeta columna-historial">
        <div class="historial-encabezado">
            <div>
                <h2 class="titulo-historial">
                    <c:choose>
                        <c:when test="${modelo.hayConsulta}">Cuenta <c:out value="${modelo.numeroCuenta}" /></c:when>
                        <c:otherwise>Movimientos de la cuenta</c:otherwise>
                    </c:choose>
                </h2>
                <c:if test="${not modelo.hayConsulta}">
                    <p class="texto-tenue texto-pequenio">Consulte una cuenta para ver su saldo y sus movimientos.</p>
                </c:if>
            </div>
            <c:if test="${modelo.hayConsulta}">
                <%-- Los movimientos los guarda el servidor: "Actualizar" muestra lo registrado desde los otros clientes --%>
                <c:url var="enlaceActualizar" value="/operaciones">
                    <c:param name="operacion" value="${operacion.parametro}" />
                    <c:param name="cuenta" value="${modelo.numeroCuenta}" />
                </c:url>
                <div class="historial-acciones">
                    <a class="boton boton-suave boton-pequenio" href="${enlaceActualizar}"
                       title="Ver los movimientos registrados desde los otros clientes">Actualizar</a>
                </div>
            </c:if>
        </div>

        <c:if test="${modelo.hayConsulta}">
            <p class="datos-cuenta">
                <strong><c:out value="${modelo.cliente}" /></strong> · cédula <c:out value="${modelo.cedula}" /><br />
                <c:out value="${modelo.sucursal}" /> · <c:out value="${modelo.moneda}" /> ·
                <span class="${modelo.cuentaActiva ? '' : 'estado-no-activo'}">estado <c:out value="${modelo.estado}" /></span><br />
                Saldo <strong><c:out value="${modelo.saldo}" /></strong> · <c:out value="${modelo.mensajeConsulta}" />
            </p>

            <c:choose>
                <c:when test="${empty modelo.movimientos}">
                    <p class="historial-vacio">La cuenta no registra movimientos.</p>
                </c:when>
                <c:otherwise>
                    <div class="historial-tabla-contenedor">
                        <table class="historial-tabla">
                            <thead>
                                <tr>
                                    <th>N°</th>
                                    <th>Fecha</th>
                                    <th>Tipo</th>
                                    <th class="importe">Importe</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="movimiento" items="${modelo.movimientos}">
                                    <tr title="<c:out value='${movimiento.detalle}' />">
                                        <td>${movimiento.numero}</td>
                                        <td><c:out value="${movimiento.fecha}" /><br /><span class="texto-referencia"><c:out value="${movimiento.hora}" /></span></td>
                                        <td>
                                            <c:out value="${movimiento.tipo}" />
                                            <c:if test="${not empty movimiento.referencia}">
                                                <br /><span class="texto-referencia"><c:out value="${movimiento.referencia}" /></span>
                                            </c:if>
                                        </td>
                                        <td class="importe ${movimiento.ingreso ? 'importe-ingreso' : 'importe-salida'}">
                                            <c:out value="${movimiento.importe}" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </aside>
</div>
<%@ include file="fragmentos/pieApp.jspf" %>
