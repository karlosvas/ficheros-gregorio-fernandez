package controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/ServletXLS")
public class ServletXLS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = (String) request.getAttribute("accion");
        Part filePart = (Part) request.getAttribute("filePart");
        String[] datos = (String[]) request.getAttribute("datos");

        if ("leer".equals(accion)) {
            leerXLS(request, response, filePart);
        } else if ("escribir".equals(accion)) {
            escribirXLS(request, response, filePart, datos);
        }
    }

    private void leerXLS(HttpServletRequest request, HttpServletResponse response, Part filePart)
            throws ServletException, IOException {
        List<String> datosLeidos = new ArrayList<>();

        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("tipoError", "No se ha subido ningún archivo.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        byte[] fileContent;
        try (InputStream is = filePart.getInputStream()) {
            fileContent = is.readAllBytes();
        }

        try (InputStream is = new ByteArrayInputStream(fileContent);
             Workbook workbook = new HSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                for (Row row : sheet) {
                    StringBuilder filaTexto = new StringBuilder();

                    for (Cell cell : row) {
                        String valor = obtenerValorCelda(cell);
                        if (!valor.isEmpty()) {
                            if (filaTexto.length() > 0) {
                                filaTexto.append(" | ");
                            }
                            filaTexto.append(valor);
                        }
                    }

                    if (filaTexto.length() > 0) {
                        datosLeidos.add(filaTexto.toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("tipoError", "Error al leer el archivo XLS: " + e.getMessage());
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        if (datosLeidos.isEmpty()) {
            request.setAttribute("tipoError", "El archivo XLS no tiene datos.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            return;
        }

        request.getSession().setAttribute("resultadoDatos", datosLeidos);
        request.getSession().setAttribute("tipo", "XLS");
        request.getRequestDispatcher("AccesoDatos.jsp").forward(request, response);
    }

    private void escribirXLS(HttpServletRequest request, HttpServletResponse response,
            Part filePart, String[] datos) throws ServletException, IOException {

        List<String> datosValidos = new ArrayList<>();
        if (datos != null) {
            for (String d : datos) {
                if (d != null && !d.trim().isEmpty()) {
                    datosValidos.add(d.trim());
                }
            }
        }

        if (datosValidos.isEmpty()) {
            request.setAttribute("mensajeError", "(*) Tienes que escribir algo para poder guardarlo.");
            request.getRequestDispatcher("TratamientoFich.jsp").forward(request, response);
            return;
        }

        Workbook workbook;
        Sheet sheet;
        int ultimaFila = 0;

        if (filePart != null && filePart.getSize() > 0) {
            byte[] fileContent;
            try (InputStream is = filePart.getInputStream()) {
                fileContent = is.readAllBytes();
            }

            try (InputStream is = new ByteArrayInputStream(fileContent)) {
                workbook = new HSSFWorkbook(is);
                sheet = workbook.getSheetAt(0);
                ultimaFila = sheet.getLastRowNum() + 1;
            } catch (Exception e) {
                workbook = new HSSFWorkbook();
                sheet = workbook.createSheet("Datos");
            }
        } else {
            workbook = new HSSFWorkbook();
            sheet = workbook.createSheet("Datos");
        }

        for (String dato : datosValidos) {
            Row row = sheet.createRow(ultimaFila++);
            Cell cell = row.createCell(0);
            cell.setCellValue(dato);
        }

        sheet.autoSizeColumn(0);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"nuevo_fichero.xls\"");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private String obtenerValorCelda(Cell cell) {
        if (cell == null) return "";

        CellType tipo = cell.getCellType();
        if (tipo == CellType.FORMULA) {
            tipo = cell.getCachedFormulaResultType();
        }

        switch (tipo) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double valor = cell.getNumericCellValue();
                if (valor == Math.floor(valor)) {
                    return String.valueOf((long) valor);
                }
                return String.valueOf(valor);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}