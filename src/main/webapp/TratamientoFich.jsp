<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <% String error = (String)
request.getAttribute("mensajeError"); %>
<!DOCTYPE html>
<html>
  <style>
    body {
      font-family: monospace;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
    }
    .form1 {
      text-align: center;
      width: 800px;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 5px;
      background-color: #f9f9f9;
    }
    .parte-principal {
      display: flex;
      justify-content: center;
      gap: 50px;
      align-items: flex-start;
      margin-top: 20px;
    }
    .input-dato {
      background-color: #f4f4f4;
      border: 1px solid #ccc;
      padding: 3px;
      width: 150px;
    }
  </style>
  <head>
    <meta charset="UTF-8" />
    <title>Tratamiento de Ficheros</title>
  </head>
  <body>
    <form class="form1" action="ServletFich" method="post" enctype="multipart/form-data">
      <h1>Tratamiento de Ficheros</h1>

      <div class="parte-principal">
        <div class="bloque-izq">
          <table cellpadding="5">
            <tr>
              <td><label>Sube el fichero aquí:</label></td>
              <td><input type="file" name="fichero" /></td>
            </tr>

            <tr>
              <td><label>Formato del fichero:</label></td>
              <td>
                <select name="tipoFichero">
                  <option value="xls">XLS</option>
                  <option value="csv">CSV</option>
                  <option value="json">JSON</option>
                  <option value="xml">XML</option>
                  <option value="rdf">RDF</option>
                </select>
              </td>
            </tr>

            <tr>
              <td colspan="2">
                <br />
                <p>¿Qué quiere hacer con el fichero?</p>
              </td>
            </tr>

            <tr>
              <td>Lectura:</td>
              <td><input type="radio" name="accion" value="leer" checked /></td>
            </tr>

            <tr>
              <td>Escritura:</td>
              <td><input type="radio" name="accion" value="escribir" /></td>
            </tr>
          </table>
        </div>

        <div class="bloque-der">
          <table cellpadding="3">
            <tr>
              <td>DATO1:</td>
              <td><input type="text" name="dato" class="input-dato" /></td>
            </tr>

            <tr>
              <td>DATO2:</td>
              <td><input type="text" name="dato" class="input-dato" /></td>
            </tr>

            <tr>
              <td>DATO3:</td>
              <td><input type="text" name="dato" class="input-dato" /></td>
            </tr>

            <tr>
              <td>DATO4:</td>
              <td><input type="text" name="dato" class="input-dato" /></td>
            </tr>

            <tr>
              <td style="vertical-align: top">DATO5:</td>
              <td><textarea name="dato" class="input-dato"></textarea></td>
            </tr>

            <tr>
              <td>DATO6:</td>
              <td><input type="text" name="dato" class="input-dato" /></td>
            </tr>
          </table>
        </div>
      </div>
      <br />
      <input type="submit" value="Enviar" />
      <br />
      <% if (error != null) { %>
      <p style="color: red; font-weight: bold; margin-top: 15px"><%= error %></p>
      <% } %>
    </form>
  </body>
</html>
