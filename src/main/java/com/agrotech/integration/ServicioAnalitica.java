package com.agrotech.integration;

import org.apache.camel.Header;

public class ServicioAnalitica {
    
    public String getUltimoValor(@Header("id_sensor") String id) {
        // Simular consulta a la base de datos
        System.out.println("[SERVICIO ANALITICA] Consultando ultimo valor para sensor: " + id);
        
        // Retornar JSON simulado
        return String.format(
            "{\"id\":\"%s\",\"humedad\":48.0,\"temperatura\":26.7,\"fecha\":\"2025-05-22T10:30:00Z\"}",
            id
        );
    }
}