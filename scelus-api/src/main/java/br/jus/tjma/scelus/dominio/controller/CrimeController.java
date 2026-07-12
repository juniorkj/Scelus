package br.jus.tjma.scelus.dominio.controller;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.dominio.dto.AlteracaoCrimeRequest;
import br.jus.tjma.scelus.dominio.dto.AlteracaoCrimeResponse;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeResponse;
import br.jus.tjma.scelus.dominio.dto.CrimeDTO;
import br.jus.tjma.scelus.dominio.dto.CrimeDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaCrimes;
import br.jus.tjma.scelus.dominio.dto.MpuVinculadaDTO;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuRequest;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuResponse;
import br.jus.tjma.scelus.dominio.service.CadastroCrimeService;
import br.jus.tjma.scelus.dominio.service.CrimeService;
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
 * javadoc Controladora REST responsável pelas operações de crimes.
 * Esta classe mapeia o objeto 'CrimeController' cadastrado no Sentinela.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@RestController
@RequestMapping("/api/crimes")
public class CrimeController {

    private final CrimeService crimeService;
    private final CadastroCrimeService cadastroCrimeService;
    private final MpuService mpuService;

    public CrimeController(
            CrimeService crimeService, CadastroCrimeService cadastroCrimeService, MpuService mpuService) {
        this.crimeService = crimeService;
        this.cadastroCrimeService = cadastroCrimeService;
        this.mpuService = mpuService;
    }

    /**
     * Endpoint para consulta paginada de crimes e processos associados.
     * Exige token válido e permissão de leitura para o objeto 'CrimeController'.
     *
     * @param filtro Parâmetros de pesquisa enviados via query parameters.
     * @return Entidade contendo a lista resultante e dados de paginação.
     */
    @GetMapping
    public ResponseEntity<ResultList<CrimeDTO>> consultarCrimes(FiltroConsultaCrimes filtro) {
        ResultList<CrimeDTO> resultado = crimeService.consultarCrimes(filtro);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint de detalhe de um crime/fato ocorrido pelo identificador,
     * com os identificadores brutos para a tela de edição.
     *
     * @param id Identificador do fato ocorrido.
     * @return DTO de detalhe do crime.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CrimeDetalheDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(crimeService.buscarPorId(id));
    }

    /**
     * Endpoint de alteração do fato ocorrido (data, CEP, medida protetiva e vínculo).
     * Exige permissão de ATUALIZACAO para o objeto 'CrimeController'.
     *
     * @param id      Identificador do fato ocorrido.
     * @param request Dados alteráveis do fato.
     * @return Mensagem de sucesso do banco.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlteracaoCrimeResponse> alterarCrime(
            @PathVariable Long id, @RequestBody @Valid AlteracaoCrimeRequest request) {
        return ResponseEntity.ok(cadastroCrimeService.alterar(id, request));
    }

    /**
     * Endpoint de exclusão do fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_del.
     * Exige permissão de EXCLUSAO para o objeto 'CrimeController'.
     *
     * @param id Identificador do fato ocorrido.
     * @return Mensagem de sucesso do banco.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> excluirCrime(
            @PathVariable Long id, @RequestParam(value = "onDeleteCascade", required = false) Boolean onDeleteCascade) {
        String mensagem = cadastroCrimeService.excluir(id);
        return ResponseEntity.ok(Map.of("mensagem", mensagem));
    }

    /**
     * Endpoint de cadastro de crime importando dados e partes do PJe (CSU002).
     * Exige permissão de inclusão para o objeto 'CrimeController'.
     *
     * @param request Dados do processo, partes, vínculo e fato ocorrido.
     * @return Identificadores gerados e mensagem de sucesso do banco.
     */
    @PostMapping
    public ResponseEntity<CadastroCrimeResponse> cadastrarCrime(@RequestBody @Valid CadastroCrimeRequest request) {
        CadastroCrimeResponse resposta = cadastroCrimeService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Endpoint de listagem das MPUs vinculadas ao fato ocorrido (CSU008).
     *
     * @param id Identificador do fato ocorrido.
     * @return Lista das MPUs vinculadas com justificativa.
     */
    @GetMapping("/{id}/mpus")
    public ResponseEntity<List<MpuVinculadaDTO>> listarMpusDoFato(@PathVariable Long id) {
        return ResponseEntity.ok(mpuService.listarMpusDoFato(id));
    }

    /**
     * Endpoint de vínculo de MPU ao fato ocorrido com justificativa obrigatória (CSU008).
     *
     * @param id      Identificador do fato ocorrido.
     * @param request MPU, justificativa e observação complementar.
     * @return Identificador do vínculo gerado e mensagem do banco.
     */
    @PostMapping("/{id}/mpus")
    public ResponseEntity<VinculoMpuResponse> vincularMpu(
            @PathVariable Long id, @RequestBody @Valid VinculoMpuRequest request) {
        VinculoMpuResponse resposta = mpuService.vincularMpu(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Endpoint de alteração do vínculo de MPU do fato ocorrido (justificativa/observação).
     *
     * @param id        Identificador do fato ocorrido.
     * @param idVinculo Identificador do vínculo (tb_fato_ocorrido_mpu).
     * @param request   Nova MPU, justificativa e observação.
     * @return Mensagem de sucesso do banco.
     */
    @PutMapping("/{id}/mpus/{idVinculo}")
    public ResponseEntity<VinculoMpuResponse> alterarVinculoMpu(
            @PathVariable Long id, @PathVariable Long idVinculo, @RequestBody @Valid VinculoMpuRequest request) {
        return ResponseEntity.ok(mpuService.alterarVinculoMpu(idVinculo, id, request));
    }

    /**
     * Endpoint de remoção do vínculo de MPU do fato ocorrido.
     *
     * @param id        Identificador do fato ocorrido (validação de rota).
     * @param idVinculo Identificador do vínculo (tb_fato_ocorrido_mpu).
     * @return Mensagem de sucesso do banco.
     */
    @DeleteMapping("/{id}/mpus/{idVinculo}")
    public ResponseEntity<Map<String, String>> removerVinculoMpu(@PathVariable Long id, @PathVariable Long idVinculo) {
        String mensagem = mpuService.removerVinculo(idVinculo);
        return ResponseEntity.ok(Map.of("mensagem", mensagem));
    }
}
