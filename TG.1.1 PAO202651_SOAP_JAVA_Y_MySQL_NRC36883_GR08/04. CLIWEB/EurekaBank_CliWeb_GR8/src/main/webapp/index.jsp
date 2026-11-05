<%-- La raíz del sitio lleva a las operaciones; sin sesión, el filtro redirige al inicio de sesión --%>
<% response.sendRedirect(request.getContextPath() + "/operaciones"); %>
