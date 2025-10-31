package com.agrotech.integration;

import org.apache.camel.main.Main;

public class MainApp {
    
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        
        // Registrar las rutas de integración
        main.configure().addRoutesBuilder(new FileTransferRoute());
        main.configure().addRoutesBuilder(new SharedDatabaseRoute());
        main.configure().addRoutesBuilder(new RPCRoute());
        
        // Inicializar base de datos
        DatabaseInitializer.init();
        
        System.out.println("==============================================");
        System.out.println("  Sistema de Integracion AgroTech Solutions");
        System.out.println("==============================================");
        System.out.println("Iniciando rutas de integracion...");
        System.out.println();
        
        // Ejecutar Camel
        main.run();
    }
}