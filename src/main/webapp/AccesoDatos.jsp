<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Resultado del Fichero</title>
<style>
	body {
		font-family: monospace;
		text-align: center;
	}
    .caja-datos {
    
        border: 1px solid #ccc;
        padding: 20px;
        margin: 25px auto;
        background-color: #f9f9f9;
        width: 50%;
    }
</style>
</head>
<body>

    <%
        // Leer datos de la sesión
        List<?> resultadoDatos = (List<?>) session.getAttribute("resultadoDatos");
        String tipo = (String) session.getAttribute("tipo");

        // Limpiar la sesión después de leer (importante para evitar datos obsoletos)
        session.removeAttribute("resultadoDatos");
        session.removeAttribute("tipo");
    %>

    <h1>Datos procesados del formato: <%= tipo != null ? tipo : "Ninguno" %></h1>

    <div class="caja-datos">
        <%
            if (resultadoDatos == null || resultadoDatos.isEmpty()) {
        %>
                No se han encontrado datos o hubo un error.
        <%
            } else {
                for (Object dato : resultadoDatos) {
        %>
                    <%= dato %><br>
        <%
                }
            }
        %>
    </div>

    <a href="TratamientoFich.jsp">Volver atrás</a>

</body>
</html>