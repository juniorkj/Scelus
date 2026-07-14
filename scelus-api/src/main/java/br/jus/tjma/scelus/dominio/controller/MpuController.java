package br.jus.tjma.scelus.dominio.controller;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuResponse;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaMpus;
import br.jus.tjma.scelus.dominio.dto.MpuDTO;
import br.jus.tjma.scelus.dominio.service.MpuService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * javadoc Controladora REST de medidas protetivas de urgência (CSU008).
 * Esta classe mapeia o objeto 'MpuController' — cadastrar no Sentinela com
 * permissões de LEITURA e INCLUSAO para os grupos habilitados.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@RestController
@RequestMapping("/api/mpus")
public class MpuController {

    private final MpuService mpuService;

    public MpuController(MpuService mpuService) {
        this.mpuService = mpuService;
    }

    /**
     * Endpoint de consulta paginada de MPUs (alimenta o modal seletor do frontend).
     *
     * @param filtro Filtros de número da MPU, número único e concessão.
     * @return Lista paginada de MPUs.
     */
    @GetMapping
    public ResponseEntity<ResultList<MpuDTO>> consultarMpus(FiltroConsultaMpus filtro) {
        return ResponseEntity.ok(mpuService.consultarMpus(filtro));
    }

    /**
     * Endpoint de busca global de MPUs pelo par vítima-acusado (RN008.02),
     * identificado pelas partes do PJe — independe do processo de origem.
     *
     * @param idParteVitima  Identificador da parte (PJe) da vítima.
     * @param idParteAcusado Identificador da parte (PJe) do acusado.
     * @return MPUs encontradas para o par, mais recentes primeiro.
     */
    @GetMapping("/par")
    public ResponseEntity<List<MpuDTO>> buscarPorPar(
            @RequestParam Long idParteVitima, @RequestParam Long idParteAcusado) {
        return ResponseEntity.ok(mpuService.buscarPorPar(idParteVitima, idParteAcusado));
    }

    /**
     * Endpoint de cadastro de MPU via função do banco (pkg_medida_protetiva).
     *
     * @param request Dados da medida protetiva.
     * @return Identificador gerado e mensagem do banco.
     */
    @PostMapping
    public ResponseEntity<CadastroMpuResponse> cadastrarMpu(@RequestBody @Valid CadastroMpuRequest request) {
        CadastroMpuResponse resposta = mpuService.cadastrarMpu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Endpoint de alteração de MPU via função do banco (pkg_medida_protetiva).
     * Exige permissão de ATUALIZACAO para o objeto 'MpuController'.
     *
     * @param id      Identificador da MPU.
     * @param request Dados completos da medida protetiva.
     * @return Mensagem de sucesso do banco.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CadastroMpuResponse> alterarMpu(
            @PathVariable Long id, @RequestBody @Valid CadastroMpuRequest request) {
        return ResponseEntity.ok(mpuService.alterarMpu(id, request));
    }

    /**
     * Endpoint de detalhe de uma MPU pelo identificador (tela de edição).
     *
     * @param id Identificador da MPU.
     * @return DTO completo da medida protetiva.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MpuDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mpuService.buscarPorId(id));
    }

    /**
     * Endpoint de exclusão de MPU via pkg_medida_protetiva.fn_medida_protetiva_urgencia_del.
     * Exige permissão de EXCLUSAO para o objeto 'MpuController'.
     *
     * @param id Identificador da MPU.
     * @return Mensagem de sucesso do banco.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> excluirMpu(
            @PathVariable Long id, @RequestParam(value = "onDeleteCascade", required = false) Boolean onDeleteCascade) {
        return ResponseEntity.ok(Map.of("mensagem", mpuService.excluirMpu(id)));
    }
}
