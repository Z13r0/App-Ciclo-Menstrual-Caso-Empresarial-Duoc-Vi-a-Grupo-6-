package com.myciclo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend.
 * Arranca el servidor en el puerto 8080.
 */
@SpringBootApplication
public class MyCicloApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCicloApplication.class, args);
    }
}