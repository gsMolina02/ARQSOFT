<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags" %>
<c:set var="titulo" value="Cambiar contraseña" />
<%@ include file="fragmentos/cabeceraApp.jspf" %>
<section class="tarjeta tarjeta-centrada">
    <div class="tarjeta-encabezado">
        <h1 class="titulo-seccion">Cambiar contraseña</h1>
        <p class="texto-tenue">Mínimo 6 caracteres y diferente a la contraseña actual</p>
    </div>

    <form action="${raiz}/sesion/cambiar-contrasenia" method="post" class="formulario" data-bloquear-envio="Guardando...">
        <input type="hidden" name="_token" value="${tokenFormulario}" />
        <label for="contraseniaActual" class="etiqueta">Contraseña actual</label>
        <e:campoContrasenia nombre="contraseniaActual" ayuda="Escriba su contraseña actual" />

        <label for="contraseniaNueva" class="etiqueta">Nueva contraseña</label>
        <e:campoContrasenia nombre="contraseniaNueva" ayuda="Escriba la nueva contraseña" />

        <label for="confirmacion" class="etiqueta">Confirmar nueva contraseña</label>
        <e:campoContrasenia nombre="confirmacion" ayuda="Repita la nueva contraseña" />

        <c:if test="${not empty error}">
            <div class="mensaje-error" role="alert"><c:out value="${error}" /></div>
        </c:if>

        <div class="fila-botones">
            <a class="boton boton-secundario" href="${raiz}/operaciones">Cancelar</a>
            <button type="submit" class="boton boton-primario">Guardar cambios</button>
        </div>
    </form>
</section>
<%@ include file="fragmentos/pieApp.jspf" %>
