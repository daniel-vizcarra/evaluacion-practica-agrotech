package com.agrotech.integration;

import org.apache.camel.builder.RouteBuilder;

public class RPCRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        // PATRON 3: RPC SIMULADO
        
        // Servidor RPC (AgroAnalyzer)
        from("direct:rpc.obtenerUltimo")
            .routeId("rpc-servidor")
            .log("=== PATRON RPC - SERVIDOR ===")
            .log("[SERVIDOR AGROANALYZER] Solicitud recibida para sensor ${header.id_sensor}")
            .bean(ServicioAnalitica.class, "getUltimoValor")
            .log("[SERVIDOR AGROANALYZER] Respuesta enviada: ${body}");
            //.log("");
        
        // Cliente RPC (FieldControl) - Se ejecuta periodicamente
        from("timer:rpcCliente?period=20000&delay=15000")
            .routeId("rpc-cliente")
            .setBody(constant("S001"))
            .setHeader("id_sensor", simple("${body}"))
            .log("=== PATRON RPC - CLIENTE ===")
            .log("[CLIENTE FIELDCONTROL] Solicitando lectura del sensor ${header.id_sensor}")
            .to("direct:rpc.obtenerUltimo")
            .log("[CLIENTE FIELDCONTROL] Respuesta recibida: ${body}")
            .log("=== RPC completado ===");
            //.log("");
    }
}