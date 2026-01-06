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
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

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

	private void procesarLecturaXML(HttpServletRequest request, HttpServletResponse response, String contenido)
			throws ServletException, IOException {
		List<String> datosLeidos = new ArrayList<>();

		// Extrae todos los nodos con contenido textual
		String[] lineas = contenido.split("\n");
		for (String linea : lineas) {
			linea = linea.trim();

			// Busca cualquier tag con contenido: <tag>contenido</tag>
			if (linea.matches("^<[^/>]+>[^<]+</[^>]+>$")) {
				// Extrae el nombre del tag y su contenido
				int inicioTag = linea.indexOf('>');
				int finContenido = linea.lastIndexOf('<');

				if (inicioTag != -1 && finContenido > inicioTag) {
					String nombreTag = linea.substring(1, inicioTag);
					String contenidoTag = linea.substring(inicioTag + 1, finContenido).trim();

					if (!contenidoTag.isEmpty()) {
						datosLeidos.add(nombreTag + ": " + contenidoTag);
					}
				}
			}
		}

		if (datosLeidos.isEmpty()) {
			request.setAttribute("tipoError", "El archivo XML no contiene elementos con datos válidos.");
			request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}

		request.getSession().setAttribute("resultadoDatos", datosLeidos);
		request.getSession().setAttribute("tipo", "XML");
		response.sendRedirect("AccesoDatos.jsp");
	}

	private void procesarEscrituraXML(HttpServletRequest request, HttpServletResponse response,
			String contenidoExistente, String[] datos) throws ServletException, IOException {

		List<String> datosValidos = new ArrayList<>();

		// Filtra vacíos
		if (datos != null) {
			for (String dato : datos) {
				if (dato != null && !dato.trim().isEmpty()) {
					datosValidos.add(dato.trim());
				}
			}
		}

		if (datosValidos.isEmpty()) {
			request.setAttribute("mensajeError", "(*) No hay datos para escribir.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
			return;
		}

		// Crea nodos XML para los nuevos datos
		StringBuilder nuevosDatos = new StringBuilder();
		for (String dato : datosValidos) {
			nuevosDatos.append("  <dato>").append(dato).append("</dato>\n");
		}

		String contenidoFinal;

		// Si hay contenido existente, lo preserva y añade
		if (contenidoExistente != null && !contenidoExistente.trim().isEmpty()) {
			// Detecta el tag raíz de cierre (puede ser </datos>, </entities>, etc.)
			int ultimoCierre = contenidoExistente.lastIndexOf("</");

			if (ultimoCierre != -1) {
				// Encuentra el nombre completo del tag de cierre
				int finTag = contenidoExistente.indexOf(">", ultimoCierre);
				String tagCierre = contenidoExistente.substring(ultimoCierre, finTag + 1);

				// Inserta los nuevos datos antes del tag de cierre
				contenidoFinal = contenidoExistente.substring(0, ultimoCierre) + nuevosDatos + tagCierre;
			} else {
				// Si no hay tag de cierre válido, envuelve todo
				contenidoFinal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<datos>\n" + contenidoExistente
						+ nuevosDatos + "</datos>";
			}
		} else {
			// Si no hay contenido previo, crea estructura nueva
			contenidoFinal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<datos>\n" + nuevosDatos + "</datos>";
		}

		// Descarga el XML
		response.setContentType("application/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"new_data.xml\"");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(contenidoFinal);
	}
}
