package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/ServletFich")
@MultipartConfig
public class ServletFich extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServletFich() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Saber que accion se va a realizar (leer o escribir)
		String accion = request.getParameter("accion");
		// Saber el tipo de fichero
		String tipoSeleccionado = request.getParameter("tipoFichero");
		// Recojer los datos (del dato1 al dato6)
		String[] datos = request.getParameterValues("dato");
		//Recuperar el archivo subido
		Part filePart = request.getPart("fichero");

		// Validar que se haya seleccionado un tipo de fichero
		if(tipoSeleccionado == null || tipoSeleccionado.trim().isEmpty()) {
			request.setAttribute("mensajeError", "(*) Error: Debe seleccionar un tipo de fichero.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
		}

		//Compruebo si se ha subido un archivo o no
		if(filePart == null || filePart.getSize() == 0 || filePart.getSubmittedFileName() == null || filePart.getSubmittedFileName().trim().isEmpty()) {
			request.setAttribute("mensajeError", "(*) Error: Debe seleccionar un archivo.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }
		
		// Si se ha subido un archivo, obtengo su nombre y extension
		String nombreArchivo = filePart.getSubmittedFileName();
        String extensionArchivo = "";
        
        // Aqui obtengo la extension del archivo
        int i = nombreArchivo.lastIndexOf('.');
        if (i > 0) {
            extensionArchivo = nombreArchivo.substring(i + 1).toLowerCase();
        }
        
        // Despues comparo la extension del archivo con el tipo seleccionado
        if (tipoSeleccionado != null && !extensionArchivo.equals(tipoSeleccionado.toLowerCase())) {
        	String msg = "El formato seleccionado (" + tipoSeleccionado + ") no coincide con el archivo subido (." + extensionArchivo + ")";
            request.setAttribute("tipoError", msg);
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        boolean hayDatos = false;
        // Comprobamos si hay datos (validar que datos no sea null)
        if(datos != null) {
            for (String dato: datos) {
                // Si hay datos terminamos el for
                if(dato != null && !dato.trim().isEmpty()) {
                    hayDatos = true;
                    break;
                }
            }
        }

        // Compruebo si el usuario ha seleccionado la accion leer con datos rellenados (error)
        if ("leer".equals(accion) && hayDatos) {
        	request.setAttribute("mensajeError", "(*) En modo Lectura NO debe rellenar los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Compruebo si el usuario ha seleccionado la accion escribir sin datos (error)
        if ("escribir".equals(accion) && !hayDatos) {
        	request.setAttribute("mensajeError", "(*) En modo Escritura es obligatorio rellenar los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Si pasamos todas las validaciones, redirigimos al servlet correspondiente
        // Pasar los atributos necesarios para que los otros servlets puedan acceder
        request.setAttribute("accion", accion);
        request.setAttribute("filePart", filePart);
        request.setAttribute("nombreArchivo", nombreArchivo);
        request.setAttribute("datos", datos);

        String selectedServlet = String.format("Servlet%s", tipoSeleccionado.toUpperCase());
		request.getRequestDispatcher(selectedServlet).forward(request, response);

	}
}
