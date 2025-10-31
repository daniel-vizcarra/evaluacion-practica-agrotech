package com.agrotech.integration;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class FileTransferRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        // Configurar formato CSV
        CsvDataFormat csv = new CsvDataFormat();
        csv.setUseMaps("true");
        csv.setDelimiter(",");
                
        // PATRON 1: FILE TRANSFER
        // SensData -> AgroAnalyzer (CSV a JSON y guardar en DB)
        from("file:data/input?delete=true&initialDelay=2000&delay=5000&include=.*\\.csv")
            .routeId("FileTransferRoute")
            .log("=== PATRON FILE TRANSFER ===")
            .log("[SENSDATA] Archivo CSV detectado: ${file:name}")
            
            // Convertir CSV a formato Map
            .unmarshal(csv)
            
            .log("[SENSDATA] Datos leidos del CSV, convirtiendo a JSON...")
            
            // Procesar cada linea del CSV
            .split(body())
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    Map<String, String> row = exchange.getIn().getBody(Map.class);
                    
                    // Extraer datos
                    String idSensor = row.get("id_sensor");
                    String fecha = row.get("fecha");
                    String humedad = row.get("humedad");
                    String temperatura = row.get("temperatura");
                    
                    // Crear JSON
                    String json = String.format(
                        "{\"id_sensor\":\"%s\",\"fecha\":\"%s\",\"humedad\":%s,\"temperatura\":%s}",
                        idSensor, fecha, humedad, temperatura
                    );
                    
                    System.out.println("[AGROANALYZER] Dato procesado: " + json);
                    
                    // Guardar en base de datos (Patron 2: Shared Database)
                    try (Connection conn = DatabaseInitializer.getConnection()) {
                        String sql = "INSERT INTO lecturas (id_sensor, fecha, humedad, temperatura) VALUES (?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, idSensor);
                        pstmt.setString(2, fecha);
                        pstmt.setDouble(3, Double.parseDouble(humedad));
                        pstmt.setDouble(4, Double.parseDouble(temperatura));
                        pstmt.executeUpdate();
                        
                        System.out.println("[SHARED DATABASE] Registro insertado: " + idSensor);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Al guardar en base de datos: " + e.getMessage());
                    }
                    
                    exchange.getIn().setBody(json);
                })
                
                // Guardar JSON en output
                .to("file:data/output?fileName=sensor-${date:now:yyyyMMdd-HHmmss}.json")
                .log("[AGROANALYZER] JSON guardado en data/output/")
            .end()
            
            .log("=== Transferencia completada ===");
            //.log("");
    }
}