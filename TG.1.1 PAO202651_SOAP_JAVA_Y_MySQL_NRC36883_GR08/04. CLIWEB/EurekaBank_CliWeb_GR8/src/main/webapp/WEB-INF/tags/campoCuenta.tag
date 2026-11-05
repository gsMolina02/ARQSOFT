<%-- Campo de número de cuenta con flecha que despliega las cuentas del servidor (ver site.js).
     Se puede escribir el número (8 dígitos) o elegirlo de la lista; escribir filtra la lista.
     Uso: <e:campoCuenta id="Cuenta" nombre="cuenta" valor="${modelo.cuenta}" ayuda="Ejemplo: 00100001"
                         conError="${modelo.campoConError == 'cuenta'}" autofoco="true" cuentas="${modelo.cuentas}" /> --%>
<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="nombre" required="true" %>
<%@ attribute name="valor" %>
<%@ attribute name="ayuda" required="true" %>
<%@ attribute name="conError" type="java.lang.Boolean" %>
<%@ attribute name="autofoco" type="java.lang.Boolean" %>
<%@ attribute name="cuentas" type="java.util.List" required="true" %>
<div class="campo-cuenta" data-campo-cuenta>
    <input type="text" class="campo-texto campo-grande${conError ? ' campo-invalido' : ''}" id="${id}" name="${nombre}"
           value="<c:out value='${valor}' />" placeholder="${ayuda}" maxlength="8" inputmode="numeric"
           autocomplete="off" data-patron="^\d{0,8}$" role="combobox" aria-expanded="false"
           aria-controls="lista${id}" aria-autocomplete="list" ${autofoco ? 'autofocus' : ''} />
    <c:if test="${not empty cuentas}">
        <button type="button" class="boton-lista" tabindex="-1" data-abrir-lista
                title="Ver las cuentas (Alt+↓)" aria-label="Ver las cuentas">
            <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                <path fill="currentColor" d="M7.4 8.6 12 13.2l4.6-4.6L18 10l-6 6-6-6 1.4-1.4Z" />
            </svg>
        </button>
        <ul class="lista-cuentas" id="lista${id}" role="listbox" hidden>
            <c:forEach var="cuenta" items="${cuentas}">
                <li class="opcion-cuenta${cuenta.estaActiva() ? '' : ' opcion-no-activa'}" role="option"
                    data-valor="${cuenta.numero()}" aria-selected="false">
                    <span class="opcion-numero">${cuenta.numero()}</span>
                    <span class="opcion-cliente"><c:out value="${cuenta.cliente()}" /></span>
                    <c:if test="${not cuenta.estaActiva()}">
                        <span class="opcion-estado"><c:out value="${cuenta.estado()}" /></span>
                    </c:if>
                </li>
            </c:forEach>
            <li class="opcion-vacia" hidden>Ninguna cuenta empieza con esos dígitos</li>
        </ul>
    </c:if>
</div>
