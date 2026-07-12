package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.scelus.comum.AppException;
import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.comum.ResultadoFuncao;
import br.jus.tjma.scelus.dominio.dto.AlteracaoCrimeRequest;
import br.jus.tjma.scelus.dominio.dto.AlteracaoCrimeResponse;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeResponse;
import br.jus.tjma.scelus.dominio.dto.ParteCadastroDTO;
import br.jus.tjma.scelus.dominio.model.Acusado;
import br.jus.tjma.scelus.dominio.model.ProcessoCrime;
import br.jus.tjma.scelus.dominio.model.Vinculo;
import br.jus.tjma.scelus.dominio.model.Vitima;
import br.jus.tjma.scelus.dominio.repository.AcusadoRepository;
import br.jus.tjma.scelus.dominio.repository.ProcessoCrimeRepository;
import br.jus.tjma.scelus.dominio.repository.VinculoRepository;
import br.jus.tjma.scelus.dominio.repository.VitimaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço transacional de cadastro de crimes do processo (CSU002).
 * Prioriza as funções do banco (pkg_litigancia, pkg_fato_ocorrido); utiliza JPA
 * apenas para as tabelas sem função correspondente (tb_processo_crime, tb_vitima,
 * tb_acusado e tb_vinculo).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Service
public class CadastroCrimeService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProcessoCrimeRepository processoCrimeRepository;
    private final VitimaRepository vitimaRepository;
    private final AcusadoRepository acusadoRepository;
    private final VinculoRepository vinculoRepository;

    public CadastroCrimeService(
            NamedParameterJdbcTemplate jdbcTemplate,
            ProcessoCrimeRepository processoCrimeRepository,
            VitimaRepository vitimaRepository,
            AcusadoRepository acusadoRepository,
            VinculoRepository vinculoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.processoCrimeRepository = processoCrimeRepository;
        this.vitimaRepository = vitimaRepository;
        this.acusadoRepository = acusadoRepository;
        this.vinculoRepository = vinculoRepository;
    }

    /**
     * Cadastra o crime de forma relacionada: processo, litigâncias das partes,
     * vítima, acusado, vínculo e fato ocorrido — tudo na mesma transação.
     *
     * @param request Dados do cadastro importados do PJe e complementados pelo usuário.
     * @return Identificadores gerados e mensagem de sucesso do banco.
     */
    @Transactional
    public CadastroCrimeResponse cadastrar(CadastroCrimeRequest request) {
        if (request.vitima().idCep() == null) {
            throw new AppException("O CEP de residência da vítima é obrigatório.");
        }

        ProcessoCrime processo = processoCrimeRepository
                .findFirstByNumeroUnico(request.numeroProcesso())
                .orElseGet(() -> criarProcesso(request));

        Long idLitiganciaVitima = inserirLitigancia(request.vitima(), request.numeroProcesso());
        vincularOcupacao(idLitiganciaVitima, request.vitima().idOcupacao());
        Vitima vitima = new Vitima();
        vitima.setIdLitigancia(idLitiganciaVitima);
        vitima.setIdCep(request.vitima().idCep());
        vitima = vitimaRepository.save(vitima);

        Long idLitiganciaAcusado = inserirLitigancia(request.acusado(), request.numeroProcesso());
        vincularOcupacao(idLitiganciaAcusado, request.acusado().idOcupacao());
        Acusado acusado = new Acusado();
        acusado.setIdLitigancia(idLitiganciaAcusado);
        acusado.setPossuiAntecedentes(request.acusado().possuiAntecedentes());
        acusado.setReincidente(request.acusado().reincidente());
        acusado.setObservacaoAntecedentes(request.acusado().observacaoAntecedentes());
        acusado = acusadoRepository.save(acusado);

        if (request.idTipoVinculo() != null) {
            Vinculo vinculo = new Vinculo();
            vinculo.setIdTipoVinculo(request.idTipoVinculo());
            vinculo.setIdVitima(vitima.getId());
            vinculo.setIdAcusado(acusado.getId());
            vinculo.setObservacao(request.observacaoVinculo());
            vinculoRepository.save(vinculo);
        }

        ResultadoFuncao fato = inserirFatoOcorrido(request, vitima.getId(), acusado.getId(), processo.getId());
        fato.validar();

        return new CadastroCrimeResponse(fato.id(), processo.getId(), vitima.getId(), acusado.getId(), fato.mensagem());
    }

    /**
     * Altera os dados do fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_upd,
     * mantendo vítima, acusado e processo originais, e atualiza/cria o vínculo
     * entre as partes quando informado.
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @param request        Dados alteráveis (data do fato, CEP, medida protetiva e vínculo).
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public AlteracaoCrimeResponse alterar(Long idFatoOcorrido, AlteracaoCrimeRequest request) {
        MapSqlParameterSource busca = new MapSqlParameterSource("id", idFatoOcorrido);
        List<Map<String, Object>> fatos = jdbcTemplate.queryForList(
                "SELECT int_acusado_id, int_vitima_id, int_processo_crime_id "
                        + "FROM public.tb_fato_ocorrido WHERE int_fato_ocorrido_id = :id",
                busca);

        if (fatos.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Fato ocorrido não encontrado: " + idFatoOcorrido);
        }
        Map<String, Object> fato = fatos.get(0);
        Long idAcusado = ((Number) fato.get("int_acusado_id")).longValue();
        Long idVitima = ((Number) fato.get("int_vitima_id")).longValue();
        Long idProcessoCrime = fato.get("int_processo_crime_id") == null
                ? null
                : ((Number) fato.get("int_processo_crime_id")).longValue();

        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("id", idFatoOcorrido)
                .addValue("idAcusado", idAcusado)
                .addValue("idVitima", idVitima)
                .addValue("dataFato", java.sql.Timestamp.valueOf(request.dataFato()))
                .addValue("idCep", request.idCep())
                .addValue("medidaProtetiva", request.medidaProtetiva())
                .addValue("idProcessoCrime", idProcessoCrime);

        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_fato_ocorrido.fn_fato_ocorrido_upd("
                        + ":id, :idAcusado, :idVitima, :dataFato, :idCep, :medidaProtetiva, :idProcessoCrime)",
                parametros,
                String.class);
        new ResultadoFuncao(idFatoOcorrido, mensagem).validar();

        if (request.idTipoVinculo() != null) {
            Vinculo vinculo = vinculoRepository
                    .findFirstByIdVitimaAndIdAcusado(idVitima, idAcusado)
                    .orElseGet(Vinculo::new);
            vinculo.setIdVitima(idVitima);
            vinculo.setIdAcusado(idAcusado);
            vinculo.setIdTipoVinculo(request.idTipoVinculo());
            vinculo.setObservacao(request.observacaoVinculo());
            vinculoRepository.save(vinculo);
        }

        return new AlteracaoCrimeResponse(idFatoOcorrido, mensagem);
    }

    /**
     * Exclui o fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_del.
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public String excluir(Long idFatoOcorrido) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("id", idFatoOcorrido);
        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_fato_ocorrido.fn_fato_ocorrido_del(:id)", parametros, String.class);
        new ResultadoFuncao(idFatoOcorrido, mensagem).validar();
        return mensagem;
    }

    /**
     * Cria o registro do processo de crime (tb_processo_crime não possui função de inclusão no banco).
     */
    private ProcessoCrime criarProcesso(CadastroCrimeRequest request) {
        ProcessoCrime processo = new ProcessoCrime();
        processo.setNumeroUnico(request.numeroProcesso());
        processo.setCodigoAssunto(request.codigoAssunto());
        processo.setDataInicioTipificacao(LocalDateTime.now());
        return processoCrimeRepository.save(processo);
    }

    /**
     * Insere a litigância da parte via pkg_litigancia.fn_litigancia_ins e retorna o id gerado.
     */
    private Long inserirLitigancia(ParteCadastroDTO parte, String numeroProcesso) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idPolo", parte.idPolo())
                .addValue("numeroUnico", numeroProcesso)
                .addValue("idParte", parte.idParte())
                .addValue("idSituacaoUsoDroga", parte.idSituacaoUsoDroga())
                .addValue("idEstadoCivil", parte.idEstadoCivil())
                .addValue("idEscolaridade", parte.idEscolaridade())
                .addValue("idRenda", parte.idRenda())
                .addValue("idReligiao", parte.idReligiao())
                .addValue("idPosicaoProle", parte.idPosicaoProle())
                .addValue("idRacaEtnia", parte.idRacaEtnia())
                .addValue("observacoesPosicaoProle", parte.observacoesPosicaoProle());

        ResultadoFuncao resultado = jdbcTemplate.queryForObject(
                "SELECT p_int_litigancia_id AS id, p_resultado AS mensagem "
                        + "FROM pkg_litigancia.fn_litigancia_ins("
                        + ":idPolo, :numeroUnico, :idParte, :idSituacaoUsoDroga, :idEstadoCivil, "
                        + ":idEscolaridade, :idRenda, :idReligiao, :idPosicaoProle, :idRacaEtnia, "
                        + ":observacoesPosicaoProle)",
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));

        resultado.validar();
        return resultado.id();
    }

    /**
     * Insere o fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_ins.
     */
    private ResultadoFuncao inserirFatoOcorrido(
            CadastroCrimeRequest request, Long idVitima, Long idAcusado, Long idProcessoCrime) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idAcusado", idAcusado)
                .addValue("idVitima", idVitima)
                .addValue("dataFato", java.sql.Timestamp.valueOf(request.dataFato()))
                .addValue("idCep", request.idCep())
                .addValue("medidaProtetiva", request.medidaProtetiva())
                .addValue("idProcessoCrime", idProcessoCrime);

        return jdbcTemplate.queryForObject(
                "SELECT p_int_fato_ocorrido_id AS id, p_resultado AS mensagem "
                        + "FROM pkg_fato_ocorrido.fn_fato_ocorrido_ins("
                        + ":idAcusado, :idVitima, :dataFato, :idCep, :medidaProtetiva, :idProcessoCrime)",
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));
    }

    private ResultadoFuncao mapearResultado(long id, boolean idNulo, String mensagem) {
        return new ResultadoFuncao(idNulo ? null : id, mensagem);
    }

    private void vincularOcupacao(Long idLitigancia, Long idOcupacao) {
        if (idOcupacao != null) {
            MapSqlParameterSource parametros = new MapSqlParameterSource()
                    .addValue("idOcupacao", idOcupacao)
                    .addValue("idLitigancia", idLitigancia);
            ResultadoFuncao res = jdbcTemplate.queryForObject(
                    "SELECT p_int_litigancia_ocupacao_id AS id, p_resultado AS mensagem "
                            + "FROM pkg_litigancia.fn_litigancia_ocupacao_ins(:idOcupacao, :idLitigancia)",
                    parametros,
                    (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));
            res.validar();
        }
    }
}
