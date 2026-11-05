<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags" %>
<c:set var="titulo" value="Iniciar sesión" />
<%@ include file="fragmentos/cabeceraAcceso.jspf" %>
            <h2 class="titulo-acceso">Iniciar sesión</h2>
            <p class="texto-tenue">Ingrese sus credenciales para continuar</p>

            <%@ include file="fragmentos/avisos.jspf" %>

            <form action="${raiz}/sesion/login" method="post" class="formulario" data-bloquear-envio="Conectando...">
                <input type="hidden" name="_token" value="${tokenFormulario}" />
                <label for="usuario" class="etiqueta">Usuario</label>
                <input type="text" class="campo-texto" id="usuario" name="usuario" value="<c:out value='${usuario}' />"
                       placeholder="Ej.: Monster" maxlength="50" autocomplete="username" autofocus />

                <label for="contrasenia" class="etiqueta">Contraseña</label>
                <e:campoContrasenia nombre="contrasenia" ayuda="Escriba su contraseña" />

                <c:if test="${not empty error}">
                    <div class="mensaje-error" role="alert"><c:out value="${error}" /></div>
                </c:if>

                <button type="submit" class="boton boton-primario boton-ancho">Iniciar sesión</button>
            </form>

            <p class="texto-servidor">Servidor SOAP: <c:out value="${urlServidor}" /></p>
            <a class="enlace-servidor" href="${raiz}/sesion/servidor">Configurar servidor</a>
<%@ include file="fragmentos/pieAcceso.jspf" %>
