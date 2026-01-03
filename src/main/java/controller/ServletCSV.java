package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/ServletCSV")
public class ServletCSV extends HttpServlet {
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
            leerCSV(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            escribirCSV(request, response, contenidoArchivo, datos);
        }
    }

    private void leerCSV(HttpServletRequest request, HttpServletResponse response, String contenido) throws ServletException, IOException {
        // Extraer líneas válidas del contenido CSV
        List<String> datosLeidos = new ArrayList<>();

        if (contenido != null && !contenido.isEmpty()) {
            for (String linea : contenido.split("\n")) {
                if (linea == null) continue;
                linea = linea.trim();
                // Ignorar líneas vacías y líneas comentadas que comienzan con '#'
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                	for (String dato : linea.split(";")) {
                		datosLeidos.add(dato);
					}
                    datosLeidos.add("\n");
                }
            }
        }

        // Si no se encontraron datos, mostrar error
        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo CSV no contiene datos válidos.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        // Pasar los datos leídos a la JSP
        request.setAttribute("resultadoDatos", datosLeidos);
        request.setAttribute("tipo", "CSV");
        request.getRequestDispatcher("AccesoDatos.jsp").forward(request, response);
    }

    private void escribirCSV(HttpServletRequest request, HttpServletResponse response, String contenidoExistente, String[] datos) throws ServletException, IOException {
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

        // Construir nuevas líneas CSV (cada elemento de datosValidos será una fila con un solo campo)
        StringBuilder nuevosDatos = new StringBuilder();
        for (String dato : datosValidos) {
            //nuevosDatos.append(escapeCSV(dato)).append(";");
            nuevosDatos.append(dato).append(";");
        }

        // Añadir al contenido existente (si existe) o crear nuevo contenido
        String contenidoFinal;
        if (contenidoExistente != null && !contenidoExistente.isEmpty()) {
            // Asegurarse de que termine en nueva línea antes de añadir
            if (!contenidoExistente.endsWith("\n")) {
                contenidoFinal = contenidoExistente + "\n" + nuevosDatos.toString();
            } else {
                contenidoFinal = contenidoExistente + nuevosDatos.toString();
            }
        } else {
            contenidoFinal = nuevosDatos.toString();
        }

        // Forzar descarga como new_data.csv
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"new_data.csv\"");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(contenidoFinal);
    }
}
