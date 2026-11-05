<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Configurar servidor" />
<%@ include file="fragmentos/cabeceraAcceso.jspf" %>
            <h2 class="titulo-acceso">Servidor SOAP</h2>
            <p class="texto-tenue">
                Escriba la IP de la computadora donde corre el servidor SOAP (ej.: 192.168.1.25)
                o la dirección completa. Para usar esta computadora escriba localhost.
            </p>

            <c:if test="${modelo.usaVariableEntorno}">
                <div class="aviso aviso-advertencia" role="alert">
                    La variable de entorno WS_BASE_URL tiene prioridad sobre la dirección guardada.
                </div>
            </c:if>

            <form action="${raiz}/sesion/guardar-servidor" method="post" class="formulario">
                <input type="hidden" name="_token" value="${tokenFormulario}" />
                <label for="direccion" class="etiqueta">Dirección del servidor</label>
                <input type="text" class="campo-texto" id="direccion" name="direccion"
                       value="<c:out value='${modelo.direccion}' />"
                       placeholder="Ej.: 192.168.1.25" maxlength="200" autocomplete="off" autofocus />

                <c:if test="${not empty modelo.error}">
                    <div class="mensaje-error" role="alert"><c:out value="${modelo.error}" /></div>
                </c:if>
                <c:if test="${not empty modelo.exito}">
                    <div class="aviso aviso-exito" role="status"><c:out value="${modelo.exito}" /></div>
                </c:if>

                <c:if test="${modelo.ofrecerGuardarSinConexion}">
                    <input type="hidden" name="guardarSinConexion" value="true" />
                </c:if>

                <div class="fila-botones">
                    <button type="submit" class="boton boton-secundario" formaction="${raiz}/sesion/probar-servidor">
                        Probar conexión
                    </button>
                    <button type="submit" class="boton boton-primario">
                        ${modelo.ofrecerGuardarSinConexion ? 'Guardar de todas formas' : 'Guardar'}
                    </button>
                </div>
            </form>

            <a class="boton boton-secundario boton-ancho" href="${raiz}/sesion/login">Volver al inicio de sesión</a>

            <p class="texto-servidor">Servidor SOAP actual: <c:out value="${modelo.urlActual}" /></p>
<%@ include file="fragmentos/pieAcceso.jspf" %>
