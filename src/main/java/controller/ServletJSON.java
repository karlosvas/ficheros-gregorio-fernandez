package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class ServletJSON
 */
@WebServlet("/ServletJSON")
public class ServletJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJSON() {
        super();
        
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 // Obtener datos desde los atributos (viene del forward de ServletFich)
        String accion = (String) request.getAttribute("accion");
        Part filePart = (Part) request.getAttribute("filePart");
        String[] datos = (String[]) request.getAttribute("datos");

        // Leer el contenido del archivo
        String contenidoArchivo = "";
        if (filePart != null) {
            try (InputStream is = filePart.getInputStream();
                 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    sb.append(linea).append("\n");
                }
                contenidoArchivo = sb.toString();
            }
        }

        if ("leer".equals(accion)) {
            leerJSON(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            escribirJSON(request, response, contenidoArchivo, datos);
        }
		
	}
	
	private void leerJSON(HttpServletRequest request, HttpServletResponse response, String contenido)
			throws ServletException, IOException {
	
		List<String>datosLeidos = new ArrayList<>();
		
		  for (String linea : contenido.split("\n")) {
		        linea = linea.trim();
		        if (linea.isEmpty()) continue;
		        String valor = "";

		        // Recorrer carácter por carácter
		        for (int i = 0; i < linea.length(); i++) {
		            char c = linea.charAt(i);

		            // Ignorar caracteres  
		            if (c == '{' || c == '}' || c == '[' || c == ']' || c == '"' || c == ',') {
		                 if (c == '}' || c == ']') {
		                    datosLeidos.add("");  
		                }
		                continue; 
		            }

		            // Guardar los demás caracteres
		            valor += c;
		        }

		        valor = valor.trim();
		        if (!valor.isEmpty()) {
		            datosLeidos.add(valor);
		        }
		    }
		
		if (datosLeidos.isEmpty()) {
	        request.setAttribute("tipoError", "El archivo JSON no contiene datos válidos.");
	        request.getRequestDispatcher("Error.jsp").forward(request, response);
	        return;
	    }
	 
	    // Guardar en sesión
	    request.getSession().setAttribute("resultadoDatos", datosLeidos);
	    request.getSession().setAttribute("tipo", "JSON");

	    response.sendRedirect("AccesoDatos.jsp");
		
	
	}
	
	 private void escribirJSON(HttpServletRequest request, HttpServletResponse response, 
			 				String contenidoExistente, String[] datos) throws ServletException, IOException {

	        List<String> datosValidos = new ArrayList<>();
 
	        if (datos != null) {
	            for (String dato : datos) {
	                if (dato != null && !dato.trim().isEmpty()) {
	                	datosValidos.add(dato.trim());
	                }
	            }
	        }

	     // Si no hay datos
	        if (datosValidos.isEmpty()) {
	            request.setAttribute("mensajeError", "(*) No hay datos para escribir.");
	            request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
	            return;
	        }
	        //Extraer datos del JSON para evitar duplicados
	        if (contenidoExistente != null && !contenidoExistente.isEmpty()) {
	            for (String linea : contenidoExistente.split("\n")) {
	                linea = linea.trim();
	                if (!linea.isEmpty() && !linea.equals("{") && !linea.equals("}") &&
	                    !linea.equals("[") && !linea.equals("]") && !linea.startsWith("\"datos\"")) {
	                    // Quitar comas y comillas al final
	                    linea = linea.replace(",", "").replace("\"", "").trim();
	                    datosValidos.add(linea);
	                }
	            }
	        }
	        
	        // Construir JSON nuevo
	        StringBuilder nuevosDatos = new StringBuilder();
	        nuevosDatos.append("{\n  \"datos\": [\n");

	        for (int i = 0; i < datosValidos.size(); i++) {
	            nuevosDatos.append("     \"");
	            nuevosDatos.append(datosValidos.get(i));
	            nuevosDatos.append("\"");

	            if (i < datosValidos.size() - 1) {
	                nuevosDatos.append(",");
	            }
	            nuevosDatos.append("\n");
	        }

	        nuevosDatos.append("  ]\n}");
 
	        //Contenido final agregando los datos nuevos
	        String contenidoFinal;
	        if (contenidoExistente != null && !contenidoExistente.isEmpty()) {
	            if (!contenidoExistente.endsWith("\n")) {
	                contenidoFinal = contenidoExistente + "\n" + nuevosDatos.toString();
	            } else {
	                contenidoFinal = contenidoExistente + nuevosDatos.toString();
	            }
	        } else {
	            contenidoFinal = nuevosDatos.toString();
	        }


	        response.setContentType("application/json");
	        response.setHeader("Content-Disposition", "attachment; filename=\"new_data.json\"");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(nuevosDatos.toString());
		 
		  		 
	 }

}
