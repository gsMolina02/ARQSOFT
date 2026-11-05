<%-- Campo de contraseña con botón de ojo para mostrar u ocultar lo escrito.
     Uso: <e:campoContrasenia nombre="contrasenia" ayuda="Escriba su contraseña" /> --%>
<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="nombre" required="true" %>
<%@ attribute name="ayuda" required="true" %>
<div class="campo-contrasenia">
    <input type="password" class="campo-texto" id="${nombre}" name="${nombre}"
           placeholder="${ayuda}" maxlength="50" autocomplete="off" />
    <button type="button" class="boton-ojo" data-alternar-contrasenia="${nombre}"
            title="Mostrar u ocultar la contraseña" aria-label="Mostrar u ocultar la contraseña">
        <svg class="icono-ver" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
            <path fill="currentColor" d="M12 5c-5 0-9.3 3.1-11 7 1.7 3.9 6 7 11 7s9.3-3.1 11-7c-1.7-3.9-6-7-11-7Zm0 11.5A4.5 4.5 0 1 1 12 7.5a4.5 4.5 0 0 1 0 9Zm0-7a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5Z" />
        </svg>
        <svg class="icono-ocultar" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
            <path fill="currentColor" d="M2.1 3.5 3.5 2.1l18.4 18.4-1.4 1.4-3.3-3.3A11.8 11.8 0 0 1 12 19c-5 0-9.3-3.1-11-7a12.3 12.3 0 0 1 4.2-5.1L2.1 3.5ZM12 5c5 0 9.3 3.1 11 7a12.4 12.4 0 0 1-3.2 4.4l-3.1-3.1A4.5 4.5 0 0 0 10.7 7.6L8.4 5.3A11.6 11.6 0 0 1 12 5Zm-4.4 5 1.6 1.6a2.5 2.5 0 0 0 3.2 3.2l1.6 1.6A4.5 4.5 0 0 1 7.6 10Z" />
        </svg>
    </button>
</div>
