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
		
		String accion = request.getParameter("accion"); //Para saber que accion se va a realizar (leer o escribir)
		String tipoSeleccionado = request.getParameter("tipoFichero"); //Para saber el tipo de fichero 
		
		//Recojo los datos (del dato1 al dato6)
		String d1 = request.getParameter("dato1");
		String d2 = request.getParameter("dato2");
		String d3 = request.getParameter("dato3");
		String d4 = request.getParameter("dato4");
		String d5 = request.getParameter("dato5");
		String d6 = request.getParameter("dato6");
		
		//Para saberr si hay datos o no (true si hay al menos un dato, false si no hay ninguno)
		boolean hayDatos = (d1 != null && !d1.isEmpty()) || (d2 != null && !d2.isEmpty()) ||
						   (d3 != null && !d3.isEmpty()) || (d4 != null && !d4.isEmpty()) ||
						   (d5 != null && !d5.isEmpty()) || (d6 != null && !d6.isEmpty());
		
		//Recupero el archivo subido
		Part filePart = request.getPart("fichero");
		
		//Compruebo si se ha subido un archivo o no
		if(filePart == null || filePart.getSize() == 0) { 
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
        
        // Compruebo si el usuario ha seleccionado la accion leer, si la ha seleccionado, compruebo si ha rellenado datos o no
        // Y si a rellenado datos tiro error
        if ("leer".equals(accion) && hayDatos) {
        	request.setAttribute("mensajeError", "(*) En modo Lectura no es necesario rellenar los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        // Igual que antes, pero si no hay datos tiro error
        if ("escribir".equals(accion) && !hayDatos) {
        	request.setAttribute("mensajeError", "(*) En modo Escritura es obligatorio rellenar los datos.");
			request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }
        
        // Pasado todo esto, no deberia de haber errores
        response.getWriter().append("<h1>Todo bien, todo correcto y yo que me alegro</h1>"); //Borrar esto despues 

	}
}
