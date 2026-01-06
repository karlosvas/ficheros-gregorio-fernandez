# 📁 Ficheros Gregorio Fernández

> Aplicación web para el tratamiento de múltiples formatos de archivos - Proyecto de **Acceso a Datos** del IES Gregorio Fernández

![Java](https://img.shields.io/badge/Java-22-orange?style=flat-square&logo=openjdk)
![Tomcat](https://img.shields.io/badge/Tomcat-11-yellow?style=flat-square&logo=apache-tomcat)
![Maven](https://img.shields.io/badge/Maven-Build-blue?style=flat-square&logo=apache-maven)

---

## 📖 Descripción

Aplicación web que permite procesar y manipular diferentes tipos de archivos a través de una interfaz intuitiva. Sube tus archivos, visualiza su contenido y añade nuevos datos de forma sencilla.

## ✨ Funcionalidades

### 📤 Formatos Soportados

| Formato  | Extensión | Descripción                    |
| -------- | --------- | ------------------------------ |
| **XLS**  | `.xls`    | Archivos Excel                 |
| **CSV**  | `.csv`    | Valores Separados por Comas    |
| **JSON** | `.json`   | JavaScript Object Notation     |
| **XML**  | `.xml`    | eXtensible Markup Language     |
| **RDF**  | `.rdf`    | Resource Description Framework |
| **YAML** | `.yaml`   | YAML Ain't Markup Language     |

### ⚡ Operaciones

- **Lectura**: Procesa y visualiza el contenido del archivo
- **Escritura**: Añade nuevos datos y descarga el archivo actualizado

## 🛠️ Tecnologías Utilizadas

```text
☕ Backend   → Java Servlets (Jakarta EE)
🖼️  Frontend  → JSP (JavaServer Pages)
🚀 Servidor  → Apache Tomcat 11
📦 Build     → Maven
```

## 📂 Estructura del Proyecto

```tree
ficheros-gregorio-fernandez/
├── src/main/java/controller/
│   ├── ServletFich.java      # 🎯 Servlet principal (controlador)
│   ├── ServletXLS.java       # 📊 Procesamiento de archivos XLS
│   ├── ServletCSV.java       # 📄 Procesamiento de archivos CSV
│   ├── ServletJSON.java      # 🔧 Procesamiento de archivos JSON
│   ├── ServletXML.java       # 🏷️  Procesamiento de archivos XML
│   └── ServletRDF.java       # 🌐 Procesamiento de archivos RDF
├── src/main/webapp/
│   ├── TratamientoFich.jsp   # 📝 Formulario principal
│   ├── AccesoDatos.jsp       # 👁️  Visualización de resultados
│   ├── Error.jsp             # ❌ Página de errores
│   └── WEB-INF/
│       └── web.xml           # ⚙️  Configuración de la aplicación
└── pom.xml                   # 📦 Configuración Maven
```

### 📋 Requisitos Previos

- JDK 22
- Apache Tomcat 11
- Maven

### 💻 Pasos de Instalación

#### 1️⃣ Compilar el proyecto

```bash
mvn clean package
```

#### 2️⃣ Desplegar en Tomcat

- Copiar el archivo WAR generado en `target/` a la carpeta `webapps` de Tomcat
- O desplegar directamente desde el IDE

#### 3️⃣ Acceder a la aplicación

```text
http://localhost:8080/ficheros-gregorio-fernandez/TratamientoFich.jsp
```

## 📚 Guía de Uso

1. Selecciona un archivo desde tu equipo
2. Elige el formato del archivo en el desplegable
3. Selecciona la operación a realizar:
   - Lectura: Solo sube el archivo (no rellenes los campos de datos)
   - Escritura: Rellena los campos de datos que deseas añadir al archivo
4. Haz clic en "Enviar"

## ✔️ Validaciones

La aplicación incluye las siguientes validaciones:

- ✅ El tipo de archivo seleccionado debe coincidir con la extensión del archivo subido
- ✅ En modo lectura no se deben rellenar los campos de datos
- ✅ En modo escritura es obligatorio rellenar al menos un campo de datos
- ✅ Es obligatorio seleccionar un formato de archivo

---
