package com.agrotech.integration;

import org.apache.camel.builder.RouteBuilder;

public class RPCRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        // PATRON 3: RPC SIMULADO
        
        // Servidor RPC (AgroAnalyzer)
        from("direct:rpc.obtenerUltimo")
            .routeId("rpc-servidor")
            .process(exchange -> {
                System.out.println("=== PATRON RPC - SERVIDOR ===");
                System.out.println("[SERVIDOR AGROANALYZER] Solicitud recibida para sensor " + exchange.getIn().getHeader("id_sensor"));
            })
            .bean(ServicioAnalitica.class, "getUltimoValor")
            .process(exchange -> {
                System.out.println("[SERVIDOR AGROANALYZER] Respuesta enviada: " + exchange.getIn().getBody());
                System.out.println();
            });
        
        // Cliente RPC (FieldControl) - Se ejecuta periodicamente
        from("timer:rpcCliente?period=20000&delay=15000")
            .routeId("rpc-cliente")
            .setBody(constant("S001"))
            .setHeader("id_sensor", simple("${body}"))
            .process(exchange -> {
                System.out.println("=== PATRON RPC - CLIENTE ===");
                System.out.println("[CLIENTE FIELDCONTROL] Solicitando lectura del sensor " + exchange.getIn().getHeader("id_sensor"));
            })
            .to("direct:rpc.obtenerUltimo")
            .process(exchange -> {
                System.out.println("[CLIENTE FIELDCONTROL] Respuesta recibida: " + exchange.getIn().getBody());
                System.out.println("=== RPC completado ===");
                System.out.println();
            });
    }
}