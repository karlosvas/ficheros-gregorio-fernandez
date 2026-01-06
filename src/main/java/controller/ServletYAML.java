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
 * Servlet implementation class ServletYAML
 */
@WebServlet("/ServletYAML")
public class ServletYAML extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
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
            leerYAML(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            escribirYAML(request, response, contenidoArchivo, datos);
        }
    }

    private void leerYAML(HttpServletRequest request, HttpServletResponse response, 
                          String contenido) throws ServletException, IOException {
        // Extraer datos del contenido YAML
        List<String> datosLeidos = new ArrayList<>();

        if (contenido != null && !contenido.isEmpty()) {
            for (String linea : contenido.split("\n")) {
                if (linea == null) continue;
                linea = linea.trim();
                
                // Ignorar líneas vacías, comentarios (#) y separadores de documentos (---)
                if (!linea.isEmpty() && !linea.startsWith("#") && !linea.equals("---")) {
                    // Procesar líneas con estructura clave: valor
                    if (linea.contains(":")) {
                        String[] partes = linea.split(":", 2);
                        String clave = partes[0].trim();
                        String valor = partes.length > 1 ? partes[1].trim() : "";
                        
                        // Agregar clave y valor como datos separados
                        datosLeidos.add(clave);
                        if (!valor.isEmpty()) {
                            datosLeidos.add(valor);
                        }
                    } else if (linea.startsWith("-")) {
                        // Procesar elementos de lista
                        String elemento = linea.substring(1).trim();
                        if (!elemento.isEmpty()) {
                            datosLeidos.add(elemento);
                        }
                    } else {
                        // Agregar línea como dato si no está vacía
                        datosLeidos.add(linea);
                    }
                    datosLeidos.add("\n");
                }
            }
        }

        // Si no se encontraron datos, mostrar error
        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo YAML no contiene datos válidos.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        // Pasar los datos leídos a la JSP
        request.setAttribute("resultadoDatos", datosLeidos);
        request.setAttribute("tipo", "YAML");
        request.getRequestDispatcher("AccesoDatos.jsp").forward(request, response);
    }

    private void escribirYAML(HttpServletRequest request, HttpServletResponse response, 
                              String contenidoExistente, String[] datos) 
                              throws ServletException, IOException {
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

        // Construir nuevos datos en formato YAML
        StringBuilder nuevosDatos = new StringBuilder();
        
        // Si hay contenido existente, agregar separador de documento
        if (contenidoExistente != null && !contenidoExistente.isEmpty()) {
            if (!contenidoExistente.endsWith("\n")) {
                nuevosDatos.append("\n");
            }
            nuevosDatos.append("---\n");
        } else {
            // Si es nuevo archivo, agregar encabezado YAML
            nuevosDatos.append("---\n");
        }
        
        // Agregar nuevos datos como lista
        nuevosDatos.append("nuevos_datos:\n");
        for (String dato : datosValidos) {
            // Escapar caracteres especiales en YAML si es necesario
            String datoEscapado = escapeYAML(dato);
            nuevosDatos.append("  - ").append(datoEscapado).append("\n");
        }

        // Construir contenido final
        String contenidoFinal;
        if (contenidoExistente != null && !contenidoExistente.isEmpty()) {
            contenidoFinal = contenidoExistente + nuevosDatos.toString();
        } else {
            contenidoFinal = nuevosDatos.toString();
        }

        // Forzar descarga como new_data.yaml
        response.setContentType("application/x-yaml");
        response.setHeader("Content-Disposition", "attachment; filename=\"new_data.yaml\"");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(contenidoFinal);
    }

    /**
     * Escapa caracteres especiales en YAML
     */
    private String escapeYAML(String dato) {
        // Si contiene caracteres especiales, envolver entre comillas
        if (dato.contains(":") || dato.contains("#") || dato.contains("-") || 
            dato.contains("[") || dato.contains("]") || dato.contains("{") || 
            dato.contains("}") || dato.contains("\"") || dato.contains("'")) {
            // Escapar comillas dobles dentro del string
            String escaped = dato.replace("\"", "\\\"");
            return "\"" + escaped + "\"";
        }
        return dato;
    }
}