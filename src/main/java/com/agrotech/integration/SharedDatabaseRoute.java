package com.agrotech.integration;

import org.apache.camel.builder.RouteBuilder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SharedDatabaseRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        // PATRON 2: SHARED DATABASE
        // Consulta periodica desde FieldControl
        from("timer:consultaDB?period=15000&delay=10000")
            .routeId("SharedDatabaseRoute")
            .process(exchange -> {
                System.out.println("=== PATRON SHARED DATABASE ===");
                System.out.println("[FIELDCONTROL] Consultando ultimos valores de sensores...");
                
                try (Connection conn = DatabaseInitializer.getConnection()) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM lecturas ORDER BY fecha DESC LIMIT 3");
                    
                    System.out.println("[FIELDCONTROL] Lecturas mas recientes:");
                    System.out.println("ID_SENSOR | FECHA        | HUMEDAD | TEMPERATURA");
                    System.out.println("----------|--------------|---------|------------");
                    
                    while (rs.next()) {
                        System.out.printf("%-10s | %-12s | %-7.1f | %-7.1f%n",
                            rs.getString("id_sensor"),
                            rs.getString("fecha"),
                            rs.getDouble("humedad"),
                            rs.getDouble("temperatura")
                        );
                    }
                    
                    System.out.println("=== Consulta completada ===");
                    //System.out.println();
                    
                } catch (Exception e) {
                    System.err.println("[ERROR] Al consultar base de datos: " + e.getMessage());
                }
            });
    }
}