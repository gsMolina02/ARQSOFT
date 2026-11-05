// Comportamiento de las páginas del cliente web EurekaBank GR08.
// Las validaciones definitivas se hacen en el servidor; aquí solo se mejora la experiencia de uso.

// Campos con data-patron (número de cuenta, importe): bloquea lo que no cumple el patrón
// mientras se escribe o se pega texto
document.querySelectorAll("[data-patron]").forEach((campo) => {
    const patron = new RegExp(campo.dataset.patron);
    campo.addEventListener("beforeinput", (evento) => {
        if (evento.data === null || !evento.inputType.startsWith("insert")) {
            return;
        }
        const inicio = campo.selectionStart ?? campo.value.length;
        const fin = campo.selectionEnd ?? campo.value.length;
        const textoNuevo = campo.value.slice(0, inicio) + evento.data + campo.value.slice(fin);

        if (!patron.test(textoNuevo)) {
            evento.preventDefault();
        }
    });
    campo.addEventListener("input", () => campo.classList.remove("campo-invalido"));
});

// Campos de cuenta con lista desplegable (_CampoCuenta): la flecha o Alt+↓ muestran las cuentas del servidor,
// escribir filtra por los dígitos iniciales, ↑/↓ recorren la lista, Enter elige y Esc la cierra
document.querySelectorAll("[data-campo-cuenta]").forEach((contenedor) => {
    const campo = contenedor.querySelector("input");
    const lista = contenedor.querySelector(".lista-cuentas");
    const flecha = contenedor.querySelector("[data-abrir-lista]");
    if (!lista || !flecha) {
        return;
    }
    const opciones = [...lista.querySelectorAll(".opcion-cuenta")];
    const vacia = lista.querySelector(".opcion-vacia");
    let activa = -1;

    const visibles = () => opciones.filter((opcion) => !opcion.hidden);

    function filtrar(texto) {
        opciones.forEach((opcion) => { opcion.hidden = !opcion.dataset.valor.startsWith(texto); });
        vacia.hidden = visibles().length > 0;
    }

    function marcar(indice) {
        const candidatas = visibles();
        opciones.forEach((opcion) => opcion.classList.remove("activa"));
        activa = candidatas.length === 0 ? -1 : (indice + candidatas.length) % candidatas.length;
        if (activa >= 0) {
            candidatas[activa].classList.add("activa");
            candidatas[activa].scrollIntoView({ block: "nearest" });
        }
    }

    function abrir(mostrarTodas) {
        document.querySelectorAll("[data-campo-cuenta].abierta").forEach((otro) => {
            if (otro !== contenedor) {
                otro.querySelector("input").dispatchEvent(new Event("cerrar-lista"));
            }
        });
        filtrar(mostrarTodas ? "" : campo.value.trim());
        opciones.forEach((opcion) => opcion.setAttribute("aria-selected", String(opcion.dataset.valor === campo.value)));
        lista.hidden = false;
        contenedor.classList.add("abierta");
        campo.setAttribute("aria-expanded", "true");
        marcar(Math.max(0, visibles().findIndex((opcion) => opcion.dataset.valor === campo.value)));
    }

    function cerrar() {
        lista.hidden = true;
        contenedor.classList.remove("abierta");
        campo.setAttribute("aria-expanded", "false");
        activa = -1;
    }

    function elegir(opcion) {
        campo.value = opcion.dataset.valor;
        campo.dispatchEvent(new Event("input", { bubbles: true }));
        cerrar();
        campo.focus();
    }

    campo.addEventListener("cerrar-lista", cerrar);
    flecha.addEventListener("mousedown", (evento) => evento.preventDefault());   // el foco se queda en el campo
    flecha.addEventListener("click", () => (lista.hidden ? abrir(true) : cerrar()));

    // mousedown y no click: el campo todavía no pierde el foco cuando se elige
    lista.addEventListener("mousedown", (evento) => {
        evento.preventDefault();
        const opcion = evento.target.closest(".opcion-cuenta");
        if (opcion) {
            elegir(opcion);
        }
    });

    // Con el número completo (8 dígitos) la lista se cierra: Enter envía el formulario
    campo.addEventListener("input", () => {
        const largo = campo.value.length;
        if (document.activeElement === campo && largo > 0 && largo < 8) {
            abrir(false);
        } else {
            cerrar();
        }
    });
    campo.addEventListener("blur", cerrar);

    campo.addEventListener("keydown", (evento) => {
        if (evento.key === "ArrowDown" && (evento.altKey || lista.hidden)) {
            evento.preventDefault();
            abrir(true);
        } else if (lista.hidden) {
            return;
        } else if (evento.key === "ArrowDown" || evento.key === "ArrowUp") {
            evento.preventDefault();
            marcar(activa + (evento.key === "ArrowDown" ? 1 : -1));
        } else if (evento.key === "Enter" && activa >= 0) {
            // Enter elige la cuenta resaltada en lugar de enviar el formulario
            evento.preventDefault();
            elegir(visibles()[activa]);
        } else if (evento.key === "Escape" || evento.key === "Tab") {
            cerrar();
        }
    });
});

// Botón de ojo: muestra u oculta la contraseña de su campo
document.querySelectorAll("[data-alternar-contrasenia]").forEach((boton) => {
    boton.addEventListener("click", () => {
        const campo = document.getElementById(boton.dataset.alternarContrasenia);
        const mostrar = campo.type === "password";
        campo.type = mostrar ? "text" : "password";
        boton.classList.toggle("mostrando", mostrar);
        campo.focus();
    });
});

// Evita enviar dos veces el mismo formulario y muestra que se está procesando
function bloquearEnvio(formulario) {
    const boton = formulario.querySelector("button[type='submit']");
    if (boton && formulario.dataset.bloquearEnvio) {
        boton.disabled = true;
        boton.textContent = formulario.dataset.bloquearEnvio;
    }
    document.body.style.cursor = "progress";
}

// Mensaje de confirmación: el fijo (data-confirmar) o la plantilla con los valores escritos
// en el formulario (data-confirmar-plantilla="Se va a depositar ${Importe} en la cuenta {Cuenta}.")
function mensajeConfirmacion(formulario) {
    if (formulario.dataset.confirmar) {
        return formulario.dataset.confirmar;
    }
    return formulario.dataset.confirmarPlantilla.replace(/\{(\w+)\}/g,
        (_, id) => (document.getElementById(id)?.value ?? "").trim());
}

// Formularios que piden confirmación (operaciones, cerrar sesión).
// Usan el cuadro propio del sitio (_DialogoConfirmacion) en lugar del confirm() del navegador.
const dialogo = document.getElementById("dialogoConfirmacion");
if (dialogo) {
    const ventana = new bootstrap.Modal(dialogo);
    const botonAceptar = document.getElementById("dialogoAceptar");
    let formularioPendiente = null;

    // La vista escribe los atributos aunque estén vacíos (Movimientos no pide confirmación): solo cuentan los que tienen texto
    document.querySelectorAll("form[data-confirmar]:not([data-confirmar='']), "
            + "form[data-confirmar-plantilla]:not([data-confirmar-plantilla=''])").forEach((formulario) => {
        formulario.addEventListener("submit", (evento) => {
            evento.preventDefault();
            formularioPendiente = formulario;
            document.getElementById("dialogoTitulo").textContent = formulario.dataset.confirmarTitulo || "¿Está seguro?";
            document.getElementById("dialogoMensaje").textContent = mensajeConfirmacion(formulario);
            botonAceptar.textContent = formulario.dataset.confirmarBoton || "Aceptar";
            botonAceptar.disabled = false;
            ventana.show();
        });
    });

    // Enfoca "Cancelar" al abrir, así Enter no confirma por accidente
    dialogo.addEventListener("shown.bs.modal", () => dialogo.querySelector("[data-bs-dismiss]").focus());

    botonAceptar.addEventListener("click", () => {
        botonAceptar.disabled = true;
        if (formularioPendiente) {
            bloquearEnvio(formularioPendiente);
            formularioPendiente.submit();   // submit() no vuelve a disparar el evento: no se pregunta dos veces
        }
    });
}

document.querySelectorAll("form[data-bloquear-envio]").forEach((formulario) => {
    formulario.addEventListener("submit", (evento) => {
        // Si el formulario espera la confirmación del diálogo, todavía no se envía
        if (!evento.defaultPrevented) {
            bloquearEnvio(formulario);
        }
    });
});
