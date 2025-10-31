package com.agrotech.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    
    private static final String DB_URL = "jdbc:h2:./database/agrotech;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public static void init() {
        try {
            System.out.println("[DATABASE] Inicializando base de datos H2...");
            
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // Crear tabla si no existe
            String createTable = """
                CREATE TABLE IF NOT EXISTS lecturas (
                    id_sensor VARCHAR(10),
                    fecha VARCHAR(50),
                    humedad DOUBLE,
                    temperatura DOUBLE
                )
            """;
            
            stmt.execute(createTable);
            System.out.println("[DATABASE] Tabla 'lecturas' creada/verificada exitosamente");
            System.out.println("[DATABASE] URL: " + DB_URL);
            System.out.println();
            
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("[DATABASE] Error al inicializar base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}