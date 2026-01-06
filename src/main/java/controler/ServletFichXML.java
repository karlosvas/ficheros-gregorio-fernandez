package controler;

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
public class ServletFichXML extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Acción elegida: leer o escribir
		String accion = request.getParameter("accion");

		// Tipo de tratamiento (xml, json, etc.)
		String tipoSeleccionado = request.getParameter("tipoFichero");

		// Datos del formulario (solo se usan en escritura)
		String[] datos = request.getParameterValues("dato");

		// Archivo subido
		Part filePart = request.getPart("fichero");

		// Comprobamos que se ha subido un archivo
		if (filePart == null || filePart.getSize() == 0) {
			request.setAttribute("mensajeError", "Debe seleccionar un archivo.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
			return;
		}

		// Miramos si hay algún dato rellenado
		boolean hayDatos = false;
		if (datos != null) {
			for (String dato : datos) {
				if (!dato.isEmpty()) {
					hayDatos = true;
					break;
				}
			}
		}

		// En lectura no debe haber datos
		if ("leer".equals(accion) && hayDatos) {
			request.setAttribute("mensajeError",
					"En modo lectura no deben rellenarse los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
			return;
		}

		// En escritura es obligatorio rellenarlos
		if ("escribir".equals(accion) && !hayDatos) {
			request.setAttribute("mensajeError",
					"En modo escritura es obligatorio rellenar los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
			return;
		}

		// Pasamos el archivo y la acción al servlet específico
		request.setAttribute("archivo", filePart);
		request.setAttribute("accion", accion);

		// Redirigimos al servlet correspondiente (ServletXML, ServletJSON, ...)
		String servletDestino = "Servlet" + tipoSeleccionado.toUpperCase();
		request.getRequestDispatcher(servletDestino).forward(request, response);
	}
}

