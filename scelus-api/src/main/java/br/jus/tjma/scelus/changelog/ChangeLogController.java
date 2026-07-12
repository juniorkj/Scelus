package br.jus.tjma.scelus.changelog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * javadoc Controladora REST do histórico de alterações (changelog) do sistema.
 * Esta classe mapeia o objeto 'ChangeLogController' — cadastrar no Sentinela
 * com permissão de LEITURA para todos os grupos (padrão frottas-new).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@RestController
@RequestMapping({"/api/changelog", "/rest/changelog"})
@Tag(name = "Change Log", description = "Endpoints para consulta do histórico de versões do sistema")
public class ChangeLogController {

    private final ChangeLogService service;

    public ChangeLogController(ChangeLogService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    @Operation(summary = "Listar todo o histórico de alterações no formato Markdown")
    public ResponseEntity<String> listarTodos() {
        return ResponseEntity.ok(service.listarMarkdown());
    }
}
