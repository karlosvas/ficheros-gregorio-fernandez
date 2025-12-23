<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error detectado</title>
<style>
    body { 
    	font-family: sans-serif; 
    	display: flex;
      	justify-content: center;
      	align-items: center;
      	min-height: 100vh;
    }
    .caja-error { 
    	border: 2px solid red; 
    	padding: 20px; 
    	display: inline-block; 
    	background-color: #ffe6e6; 
    }
    h1 { 
    	color: red; 
    }
    p { 
    	font-size: 18px; 
    }
</style>
</head>
<body>

    <div class="caja-error">
        <h1>TIPO DE ERROR</h1>
        <p>
            <%= request.getAttribute("tipoError") %>
        </p>
        
        <br>
        <a href="TratamientoFich.jsp">Volver al formulario</a>
    </div>

</body>
</html>