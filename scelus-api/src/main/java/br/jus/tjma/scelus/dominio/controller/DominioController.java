package br.jus.tjma.scelus.dominio.controller;

import br.jus.tjma.scelus.dominio.dto.CepDTO;
import br.jus.tjma.scelus.dominio.dto.ItemDominioDTO;
import br.jus.tjma.scelus.dominio.service.DominioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * javadoc Controladora REST das tabelas de domínio utilizadas nos formulários
 * (tipos de vínculo, polos, justificativas de inclusão de MPU e CEPs).
 * Esta classe mapeia o objeto 'DominioController' — cadastrar no Sentinela
 * com permissão de LEITURA para todos os grupos do sistema.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@RestController
@RequestMapping("/api/dominios")
public class DominioController {

    private final DominioService dominioService;

    public DominioController(DominioService dominioService) {
        this.dominioService = dominioService;
    }

    /**
     * Lista os tipos de vínculo entre vítima e acusado.
     */
    @GetMapping("/tipos-vinculo")
    public ResponseEntity<List<ItemDominioDTO>> listarTiposVinculo() {
        return ResponseEntity.ok(dominioService.listarTiposVinculo());
    }

    /**
     * Lista os polos processuais.
     */
    @GetMapping("/polos")
    public ResponseEntity<List<ItemDominioDTO>> listarPolos() {
        return ResponseEntity.ok(dominioService.listarPolos());
    }

    /**
     * Lista as justificativas de inclusão de MPU.
     */
    @GetMapping("/justificativas-inclusao-mpu")
    public ResponseEntity<List<ItemDominioDTO>> listarJustificativasInclusaoMpu() {
        return ResponseEntity.ok(dominioService.listarJustificativasInclusaoMpu());
    }

    /**
     * Pesquisa CEPs pelo prefixo informado.
     */
    @GetMapping("/ceps")
    public ResponseEntity<List<CepDTO>> pesquisarCeps(@RequestParam("cep") String cep) {
        return ResponseEntity.ok(dominioService.pesquisarCeps(cep));
    }

    /**
     * Lista um domínio genérico da whitelist (ocupações, deficiências, drogas,
     * raças/etnias, benefícios, consequências da violência, etc.) — filtros do CSU001.
     */
    @GetMapping("/{chave}")
    public ResponseEntity<List<ItemDominioDTO>> listarDominio(
            @org.springframework.web.bind.annotation.PathVariable String chave) {
        return ResponseEntity.ok(dominioService.listarDominio(chave));
    }
}
