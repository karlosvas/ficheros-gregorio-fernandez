package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/ServletRDF")
public class ServletRDF extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener datos desde los atributos (viene del forward de ServletFich)
        String accion = (String) request.getAttribute("accion");
        Part filePart = (Part) request.getAttribute("filePart");
        String[] datos = (String[]) request.getAttribute("datos");

        // Leer el contenido del archivo
        String contenidoArchivo = "";
        if (filePart != null) {
            try (InputStream is = filePart.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    sb.append(linea).append("\n");
                }
                contenidoArchivo = sb.toString();
            }
        }

        if ("leer".equals(accion)) {
            leerRDF(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            escribirRDF(request, response, contenidoArchivo, datos);
        }
    }

    private void leerRDF(HttpServletRequest request, HttpServletResponse response, String contenido) throws ServletException, IOException {
        // Extraer datos simples del contenido RDF
        List<String> datosLeidos = new ArrayList<>();
        
        for (String linea : contenido.split("\n")) {
            linea = linea.trim();
            if (!linea.isEmpty() && !linea.startsWith("#") && !linea.startsWith("@")) {
                datosLeidos.add(linea);
            }
        }

        // Si no se encontraron datos, mostrar error
        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo RDF no contiene datos válidos.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        // Guardar datos en sesión y redirigir 
        request.getSession().setAttribute("resultadoDatos", datosLeidos);
        request.getSession().setAttribute("tipo", "RDF");
        response.sendRedirect("AccesoDatos.jsp");
    }

    private void escribirRDF(HttpServletRequest request, HttpServletResponse response, String contenidoExistente, String[] datos) throws ServletException, IOException {
        // Verificar si hay datos válidos
        List<String> datosValidos = new ArrayList<>();
        if (datos != null) {
            for (String dato : datos) {
                if (dato != null && !dato.trim().isEmpty()) {
                    datosValidos.add(dato.trim());
                }
            }
        }

        // Si no hay datos, mostrar error
        if (datosValidos.isEmpty()) {
            request.setAttribute("mensajeError", "(*) No hay datos para escribir.");
            request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Construir nuevos datos en formato RDF/XML
        StringBuilder nuevosDatos = new StringBuilder();
        for (String dato : datosValidos) {
            // Crear un rdf:Description con el dato como rdf:value
            nuevosDatos.append("  <rdf:Description>\n");
            nuevosDatos.append("    <rdf:value>").append(dato).append("</rdf:value>\n");
            nuevosDatos.append("  </rdf:Description>\n");
        }

        // Buscar la etiqueta de cierre </rdf:RDF>
        String contenidoFinal;
        int indexCierre = contenidoExistente.lastIndexOf("</rdf:RDF>");

        if (indexCierre != -1) {
            // Insertar ANTES de </rdf:RDF>
            contenidoFinal = contenidoExistente.substring(0, indexCierre)
                           + nuevosDatos.toString()
                           + contenidoExistente.substring(indexCierre);
        } else {
            // Si no hay etiqueta de cierre, agregar al final con estructura completa
            contenidoFinal = contenidoExistente + nuevosDatos.toString();
        }

        // Forzar descarga como new_data.rdf
        response.setContentType("application/rdf+xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"new_data.rdf\"");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(contenidoFinal);
    }
}