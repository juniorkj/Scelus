package br.jus.tjma.scelus.dominio.controller;

import br.jus.tjma.scelus.dominio.dto.ProcessoPjeDTO;
import br.jus.tjma.scelus.dominio.service.ProcessoPjeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * javadoc Controladora REST de consulta de processos do PJe (CSU002).
 * Esta classe mapeia o objeto 'ProcessoPjeController' — cadastrar no Sentinela
 * com permissão de LEITURA para os grupos que utilizam o cadastro de crimes.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@RestController
@RequestMapping("/api/processos-pje")
public class ProcessoPjeController {

    private final ProcessoPjeService processoPjeService;

    public ProcessoPjeController(ProcessoPjeService processoPjeService) {
        this.processoPjeService = processoPjeService;
    }

    /**
     * Endpoint de consulta de processo no PJe pelo número único formatado (CNJ),
     * retornando o processo e suas partes para pré-preenchimento do cadastro.
     *
     * @param numero Número único formatado do processo.
     * @return Processo com dados básicos e partes.
     */
    @GetMapping
    public ResponseEntity<ProcessoPjeDTO> consultarPorNumero(@RequestParam("numero") String numero) {
        return ResponseEntity.ok(processoPjeService.consultarPorNumero(numero));
    }
}
