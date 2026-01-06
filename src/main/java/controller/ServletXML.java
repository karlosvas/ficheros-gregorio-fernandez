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

@WebServlet("/ServletXML")
public class ServletXML extends HttpServlet {

    private static final long serialVersionUID = 1L;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = (String) request.getAttribute("accion");
        Part filePart = (Part) request.getAttribute("filePart");
        String[] datos = (String[]) request.getAttribute("datos");

        // Saca el xml si hay
        String contenidoArchivo = leerContenidoArchivo(filePart);

        // Ejecuta leer o escribir
        if ("leer".equals(accion)) {
            procesarLecturaXML(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            procesarEscrituraXML(request, response, contenidoArchivo, datos);
        }
    }

    private String leerContenidoArchivo(Part filePart) throws IOException {
        String contenido = "";

        if (filePart != null) {
            try (InputStream is = filePart.getInputStream();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(is, StandardCharsets.UTF_8))) {

                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    sb.append(linea).append("\n");
                }
                contenido = sb.toString();
            }
        }

        return contenido;
    }

 
    private void procesarLecturaXML(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String contenido)
            throws ServletException, IOException {

        List<String> datosLeidos = new ArrayList<>();

        // Busca el contenido con la etiqueta dato
        for (String linea : contenido.split("\n")) {
            linea = linea.trim();

            if (linea.startsWith("<dato>") && linea.endsWith("</dato>")) {
                String valor = linea.replace("<dato>", "")
                                     .replace("</dato>", "")
                                     .trim();
                datosLeidos.add(valor);
            }
        }

        // Control de error por si no hay datos validos
        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo XML no contiene datos válidos.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        // Manda datos a vista
        request.setAttribute("resultadoDatos", datosLeidos);
        request.setAttribute("tipo", "XML");
        request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
    }

  
    private void procesarEscrituraXML(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String contenidoExistente,
                                      String[] datos)
            throws ServletException, IOException {

        List<String> datosValidos = new ArrayList<>();

        // Filtra vacios
        if (datos != null) {
            for (String dato : datos) {
                if (dato != null && !dato.trim().isEmpty()) {
                    datosValidos.add(dato.trim());
                }
            }
        }

        // Si hay null en escribir, da error
        if (datosValidos.isEmpty()) {
            request.setAttribute("mensajeError", "(*) No hay datos para escribir.");
            request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Crea nodos xml
        StringBuilder nuevosDatos = new StringBuilder();
        for (String dato : datosValidos) {
            nuevosDatos.append("  <dato>")
                       .append(dato)
                       .append("</dato>\n");
        }

        String contenidoFinal;
        int posicionCierre = contenidoExistente.lastIndexOf("</datos>");

        // Añade datos xml o crea si no hay
        if (posicionCierre != -1) {
            contenidoFinal = contenidoExistente.substring(0, posicionCierre)
                    + nuevosDatos
                    + contenidoExistente.substring(posicionCierre);
        } else {
            contenidoFinal =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<datos>\n" +
                    nuevosDatos +
                    "</datos>";
        }

        // Descarga el xml
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"new_data.xml\"");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(contenidoFinal);
    }
}
