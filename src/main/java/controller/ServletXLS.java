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

@WebServlet("/ServletXLS")
public class ServletXLS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cogemos los datos que nos pasa el primer Servlet (ServletFich)
        String accion = (String) request.getAttribute("accion");
        Part filePart = (Part) request.getAttribute("filePart");
        String[] datos = (String[]) request.getAttribute("datos");

        // Guardamos todo lo que tiene el archivo en un string
        String contenidoArchivo = "";
        
        if (filePart != null) {
            // Leemos el archivo línea por línea
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

        // Miramos si el usuario quiere leer o escribir
        if ("leer".equals(accion)) {
            leerXLS(request, response, contenidoArchivo);
        } else if ("escribir".equals(accion)) {
            escribirXLS(request, response, contenidoArchivo, datos);
        }
    }

    private void leerXLS(HttpServletRequest request, HttpServletResponse response, String contenido) throws ServletException, IOException {
        // Lista para guardar las frases o datos que encontremos
        List<String> datosLeidos = new ArrayList<>();

        if (contenido != null && !contenido.isEmpty()) {
            // Partimos el texto por líneas
            for (String linea : contenido.split("\n")) {
                linea = linea.trim();
                // Si la línea no está vacía, la guardamos
                if (!linea.isEmpty()) {
                    datosLeidos.add(linea);
                }
            }
        }

        // Si al final no hemos podido leer nada, mandamos un error
        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo XLS no tiene datos que podamos leer.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        // Si todo va bien, mandamos los datos a la página de resultados
        request.setAttribute("resultadoDatos", datosLeidos);
        request.setAttribute("tipo", "XLS");
        request.getRequestDispatcher("AccesoDatos.jsp").forward(request, response);
    }

    private void escribirXLS(HttpServletRequest request, HttpServletResponse response, String contenidoExistente, String[] datos) throws ServletException, IOException {
        // Lista para limpiar los huecos vacíos del formulario
        List<String> datosValidos = new ArrayList<>();
        if (datos != null) {
            for (String d : datos) {
                if (d != null && !d.trim().isEmpty()) {
                    datosValidos.add(d.trim());
                }
            }
        }

        // Si no han escrito nada en los cuadritos, avisamos
        if (datosValidos.isEmpty()) {
            request.setAttribute("mensajeError", "(*) Tienes que escribir algo para poder guardarlo.");
            request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Juntamos lo que ya había en el archivo con lo nuevo
        StringBuilder contenidoFinal = new StringBuilder(contenidoExistente);
        
        // Si el archivo ya tenía algo y no termina en salto de línea, se lo ponemos
        if (contenidoExistente.length() > 0 && !contenidoExistente.endsWith("\n")) {
            contenidoFinal.append("\n");
        }

        // Añadimos cada dato nuevo en una línea diferente
        for (String nuevoDato : datosValidos) {
            contenidoFinal.append(nuevoDato).append("\n");
        }

        // Preparamos la respuesta para que el navegador descargue el archivo
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"nuevo_fichero.xls\"");
        response.setCharacterEncoding("UTF-8");
        
        // Escribimos el contenido en el archivo de descarga
        response.getWriter().write(contenidoFinal.toString());
    }
}