# Evaluación Práctica - AgroTech Solutions S.A.

## Descripción del Proyecto

Sistema de integración para AgroTech Solutions que conecta tres sistemas independientes mediante la implementación de tres patrones clásicos de integración empresarial:

1. **File Transfer (Transferencia de Archivos)**
2. **Shared Database (Base de Datos Compartida)**
3. **RPC (Remote Procedure Call)**

## Contexto Empresarial

AgroTech Solutions S.A. es una empresa de tecnología agrícola que desarrolla soluciones para optimizar el uso del agua y recursos agrícolas mediante sensores distribuidos en el campo.

### Sistemas involucrados:

- **SensData:** Recopila lecturas de sensores (humedad y temperatura)
- **AgroAnalyzer:** Procesa datos y genera reportes
- **FieldControl:** Controla sistemas de riego

## Objetivo

Automatizar el intercambio de información entre los tres sistemas mediante patrones de integración clásicos, eliminando procesos manuales y mejorando la confiabilidad.

## Stack Tecnológico

- **Lenguaje:** Java 17+
- **Framework:** Apache Camel 4.4.0
- **Gestor de dependencias:** Maven 3.9+
- **Base de datos:** H2 Database
- **IDE:** Visual Studio Code

## Estructura del Proyecto
```
evaluacion-practica-agrotech/
├── src/main/java/com/agrotech/integration/
│   ├── MainApp.java                 # Clase principal
│   ├── DatabaseInitializer.java     # Inicialización de BD
│   ├── FileTransferRoute.java       # Patrón 1: File Transfer
│   ├── SharedDatabaseRoute.java     # Patrón 2: Shared Database
│   ├── RPCRoute.java                # Patrón 3: RPC
│   └── ServicioAnalitica.java       # Servicio para RPC
├── data/
│   ├── input/                       # Archivos CSV de entrada
│   ├── output/                      # Archivos JSON procesados
│   ├── processed/                   # Archivos procesados
│   └── logs/                        # Logs de errores
├── database/                        # Base de datos H2
├── sensores.csv                     # Archivo de ejemplo
├── pom.xml                          # Configuración Maven
└── README.md                        # Este archivo
```

## Patrones de Integración Implementados

### 1File Transfer (SensData → AgroAnalyzer)

**Descripción:** Transferencia automática de archivos CSV con datos de sensores.

**Flujo:**
1. Monitorea la carpeta `data/input` cada 5 segundos
2. Lee archivos CSV con lecturas de sensores
3. Convierte cada registro a formato JSON
4. Inserta los datos en la base de datos compartida
5. Guarda los JSON en `data/output`
6. Elimina el archivo CSV procesado de input

**Formato CSV de entrada:**
```csv
id_sensor,fecha,humedad,temperatura
S001,2025-05-22,45,26.4
S002,2025-05-22,50,25.1
S003,2025-05-22,47,27.3
```

**Formato JSON de salida:**
```json
{"id_sensor":"S001","fecha":"2025-05-22","humedad":45.0,"temperatura":26.4}
```

---

### Shared Database (AgroAnalyzer ↔ FieldControl)

**Descripción:** Base de datos H2 compartida para intercambio de información.

**Esquema de tabla:**
```sql
CREATE TABLE lecturas (
    id_sensor VARCHAR(10),
    fecha TEXT,
    humedad DOUBLE,
    temperatura DOUBLE
);
```

**Flujo:**
1. AgroAnalyzer inserta registros procesados
2. FieldControl consulta cada 15 segundos las últimas 3 lecturas
3. Muestra los valores en consola formateados

**Ubicación de BD:** `./database/agrotech.mv.db`

---

### RPC Simulado (FieldControl → AgroAnalyzer)

**Descripción:** Llamadas síncronas directas entre sistemas.

**Flujo:**
1. **Cliente (FieldControl):**
   - Solicita cada 20 segundos la última lectura del sensor S001
   - Envía solicitud mediante `direct:solicitarLectura`
   - Espera respuesta con timeout de 2000ms

2. **Servidor (AgroAnalyzer):**
   - Recibe solicitud con ID de sensor
   - Consulta el servicio de analítica
   - Retorna JSON con última lectura

**Ejemplo de comunicación:**
```
[CLIENTE] Solicitando lectura del sensor S001
[SERVIDOR] Solicitud recibida para sensor S001
[SERVICIO] Consultando último valor...
[SERVIDOR] Respuesta: {"id":"S001","humedad":48.0,"temperatura":26.7,...}
[CLIENTE] Respuesta recibida
```

## Instalación y Ejecución

### Prerrequisitos

- Java 17 o superior
- Maven 3.9 o superior

### Pasos de instalación

1. **Clonar el repositorio:**
```bash
   git clone https://github.com/daniel-vizcarra/evaluacion-practica-agrotech.git
   cd evaluacion-practica-agrotech
```

2. **Compilar el proyecto:**
```bash
   mvn clean compile
```

3. **Colocar archivo CSV de prueba:**
```bash
   copy sensores.csv data\input\sensores.csv
```

4. **Ejecutar el sistema:**
```bash
   mvn exec:java
```

### Detener la ejecución

Presionar `Ctrl + C` en la terminal.


