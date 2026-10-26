package com.myciclo.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint que consume la app Android.
 * GET /api/guias
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GuiaController {

    private final GuiaService service;

    public GuiaController(GuiaService service) {
        this.service = service;
    }

    @GetMapping("/guias")
    public List<Guia> obtenerGuias() {
        return service.listar();
    }
}