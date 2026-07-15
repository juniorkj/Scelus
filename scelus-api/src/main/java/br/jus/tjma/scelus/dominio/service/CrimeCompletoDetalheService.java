package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.dominio.dto.BeneficioDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.ComunicanteDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.ConfiguracaoFamiliarDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.ConsequenciaViolenciaDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.CrimeCometidoDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.CrimeCompletoDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.FatoOcorridoDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.ParteDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.VinculoDetalheDTO;
import br.jus.tjma.scelus.dominio.model.Acusado;
import br.jus.tjma.scelus.dominio.model.Comunicante;
import br.jus.tjma.scelus.dominio.model.FatoOcorrido;
import br.jus.tjma.scelus.dominio.model.FatoOcorridoComunicante;
import br.jus.tjma.scelus.dominio.model.ProcessoCrime;
import br.jus.tjma.scelus.dominio.model.Vitima;
import br.jus.tjma.scelus.dominio.repository.AcusadoRepository;
import br.jus.tjma.scelus.dominio.repository.BeneficioRepository;
import br.jus.tjma.scelus.dominio.repository.CepRepository;
import br.jus.tjma.scelus.dominio.repository.ComunicanteRepository;
import br.jus.tjma.scelus.dominio.repository.ConfiguracaoFamiliarRepository;
import br.jus.tjma.scelus.dominio.repository.ConsequenciaViolenciaRepository;
import br.jus.tjma.scelus.dominio.repository.FatoOcorridoComunicanteRepository;
import br.jus.tjma.scelus.dominio.repository.FatoOcorridoRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaDeficienciaRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaDrogaRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaEscutaJudicialRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaOcupacaoRepository;
import br.jus.tjma.scelus.dominio.repository.ProcessoCrimeRepository;
import br.jus.tjma.scelus.dominio.repository.VinculoRepository;
import br.jus.tjma.scelus.dominio.repository.VitimaRepository;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço de leitura do detalhe completo de um crime/fato ocorrido
 * cadastrado pelo wizard do CSU002, reunindo crimes cometidos, vítima, acusado,
 * vínculo, fato ocorrido (com comunicantes) e consequências da violência — usado
 * para carregar as telas de edição e visualização em formato de passos (stepper).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
@Service
@Transactional(readOnly = true)
public class CrimeCompletoDetalheService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FatoOcorridoRepository fatoOcorridoRepository;
    private final ProcessoCrimeRepository processoCrimeRepository;
    private final VitimaRepository vitimaRepository;
    private final AcusadoRepository acusadoRepository;
    private final VinculoRepository vinculoRepository;
    private final CepRepository cepRepository;
    private final BeneficioRepository beneficioRepository;
    private final ConfiguracaoFamiliarRepository configuracaoFamiliarRepository;
    private final ConsequenciaViolenciaRepository consequenciaViolenciaRepository;
    private final ComunicanteRepository comunicanteRepository;
    private final FatoOcorridoComunicanteRepository fatoOcorridoComunicanteRepository;
    private final LitigenciaOcupacaoRepository litigenciaOcupacaoRepository;
    private final LitigenciaDeficienciaRepository litigenciaDeficienciaRepository;
    private final LitigenciaDrogaRepository litigenciaDrogaRepository;
    private final LitigenciaEscutaJudicialRepository litigenciaEscutaJudicialRepository;

    public CrimeCompletoDetalheService(
            NamedParameterJdbcTemplate jdbcTemplate,
            FatoOcorridoRepository fatoOcorridoRepository,
            ProcessoCrimeRepository processoCrimeRepository,
            VitimaRepository vitimaRepository,
            AcusadoRepository acusadoRepository,
            VinculoRepository vinculoRepository,
            CepRepository cepRepository,
            BeneficioRepository beneficioRepository,
            ConfiguracaoFamiliarRepository configuracaoFamiliarRepository,
            ConsequenciaViolenciaRepository consequenciaViolenciaRepository,
            ComunicanteRepository comunicanteRepository,
            FatoOcorridoComunicanteRepository fatoOcorridoComunicanteRepository,
            LitigenciaOcupacaoRepository litigenciaOcupacaoRepository,
            LitigenciaDeficienciaRepository litigenciaDeficienciaRepository,
            LitigenciaDrogaRepository litigenciaDrogaRepository,
            LitigenciaEscutaJudicialRepository litigenciaEscutaJudicialRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.fatoOcorridoRepository = fatoOcorridoRepository;
        this.processoCrimeRepository = processoCrimeRepository;
        this.vitimaRepository = vitimaRepository;
        this.acusadoRepository = acusadoRepository;
        this.vinculoRepository = vinculoRepository;
        this.cepRepository = cepRepository;
        this.beneficioRepository = beneficioRepository;
        this.configuracaoFamiliarRepository = configuracaoFamiliarRepository;
        this.consequenciaViolenciaRepository = consequenciaViolenciaRepository;
        this.comunicanteRepository = comunicanteRepository;
        this.fatoOcorridoComunicanteRepository = fatoOcorridoComunicanteRepository;
        this.litigenciaOcupacaoRepository = litigenciaOcupacaoRepository;
        this.litigenciaDeficienciaRepository = litigenciaDeficienciaRepository;
        this.litigenciaDrogaRepository = litigenciaDrogaRepository;
        this.litigenciaEscutaJudicialRepository = litigenciaEscutaJudicialRepository;
    }

    /**
     * Monta o detalhe completo de um crime/fato ocorrido para as telas de
     * edição/visualização em formato de passos (stepper).
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @return Detalhe completo com crimes cometidos, vítima, acusado, vínculo,
     *     fato ocorrido e consequências da violência.
     */
    public CrimeCompletoDetalheDTO buscarDetalhe(Long idFatoOcorrido) {
        FatoOcorrido fato = fatoOcorridoRepository
                .findById(idFatoOcorrido)
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Fato ocorrido não encontrado: " + idFatoOcorrido));

        ProcessoCrime processoCrimeFato = processoCrimeRepository
                .findById(fato.getIdProcessoCrime())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Processo de crime não encontrado: " + fato.getIdProcessoCrime()));

        List<CrimeCometidoDetalheDTO> crimesCometidos =
                processoCrimeRepository.findByNumeroUnico(processoCrimeFato.getNumeroUnico()).stream()
                        .map(pc -> new CrimeCometidoDetalheDTO(
                                pc.getId(),
                                pc.getCodigoAssunto(),
                                pc.getDescricaoAssunto(),
                                pc.getDataInicioTipificacao(),
                                pc.getDataFimTipificacao()))
                        .toList();

        Vitima vitima = vitimaRepository
                .findById(fato.getIdVitima())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Vítima não encontrada: " + fato.getIdVitima()));
        Acusado acusado = acusadoRepository
                .findById(fato.getIdAcusado())
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Acusado não encontrado: " + fato.getIdAcusado()));

        ParteDetalheDTO vitimaDetalhe = montarParteVitima(vitima);
        ParteDetalheDTO acusadoDetalhe = montarParteAcusado(acusado);

        VinculoDetalheDTO vinculoDetalhe = vinculoRepository
                .findFirstByIdVitimaAndIdAcusado(vitima.getId(), acusado.getId())
                .map(v -> new VinculoDetalheDTO(v.getId(), v.getIdTipoVinculo(), v.getObservacao()))
                .orElse(null);

        FatoOcorridoDetalheDTO fatoDetalhe = new FatoOcorridoDetalheDTO(
                processoCrimeFato.getCodigoAssunto(),
                fato.getDataFato(),
                fato.getIdCep(),
                descricaoCep(fato.getIdCep()),
                fato.getMedidaProtetiva(),
                montarComunicantes(idFatoOcorrido));

        List<ConsequenciaViolenciaDetalheDTO> consequencias =
                montarConsequencias(vitimaDetalhe.idLitigancia(), acusadoDetalhe.idLitigancia());

        return new CrimeCompletoDetalheDTO(
                idFatoOcorrido,
                processoCrimeFato.getNumeroUnico(),
                crimesCometidos,
                vitimaDetalhe,
                acusadoDetalhe,
                vinculoDetalhe,
                fatoDetalhe,
                consequencias);
    }

    private ParteDetalheDTO montarParteVitima(Vitima vitima) {
        Map<String, Object> litigancia = consultarLitigancia(vitima.getIdLitigancia());
        return new ParteDetalheDTO(
                vitima.getIdLitigancia(),
                vitima.getId(),
                null,
                numero(litigancia.get("int_parte_id")),
                numero(litigancia.get("int_polo_id")),
                numero(litigancia.get("int_situacao_uso_droga_id")),
                numero(litigancia.get("int_estado_civil_id")),
                numero(litigancia.get("int_escolaridade_id")),
                numero(litigancia.get("int_renda_id")),
                numero(litigancia.get("int_religiao_id")),
                numero(litigancia.get("int_posicao_prole_id")),
                numero(litigancia.get("int_raca_etnia_id")),
                (String) litigancia.get("str_observacoes_posicao_prole"),
                litigenciaOcupacaoRepository.findByIdLitigancia(vitima.getIdLitigancia()).stream()
                        .map(l -> l.getIdOcupacao())
                        .toList(),
                litigenciaDeficienciaRepository.findByIdLitigancia(vitima.getIdLitigancia()).stream()
                        .map(l -> l.getIdDeficiencia())
                        .toList(),
                litigenciaDrogaRepository.findByIdLitigancia(vitima.getIdLitigancia()).stream()
                        .map(l -> l.getIdDroga())
                        .toList(),
                litigenciaEscutaJudicialRepository.findByIdLitigancia(vitima.getIdLitigancia()).stream()
                        .findFirst()
                        .map(l -> l.getIdEscutaJudicial())
                        .orElse(null),
                vitima.getIdCep(),
                descricaoCep(vitima.getIdCep()),
                beneficioRepository.findByIdLitigancia(vitima.getIdLitigancia()).stream()
                        .map(b -> new BeneficioDetalheDTO(b.getId(), b.getIdTipoBeneficio(), b.getDataInicio()))
                        .toList(),
                configuracaoFamiliarRepository.findByIdVitima(vitima.getId()).stream()
                        .map(c -> new ConfiguracaoFamiliarDetalheDTO(
                                c.getId(), c.getIdTipoConfiguracaoFamiliar(), c.getDataDeclaracao(), c.getObservacao()))
                        .toList(),
                null,
                null,
                null);
    }

    private ParteDetalheDTO montarParteAcusado(Acusado acusado) {
        Map<String, Object> litigancia = consultarLitigancia(acusado.getIdLitigancia());
        return new ParteDetalheDTO(
                acusado.getIdLitigancia(),
                null,
                acusado.getId(),
                numero(litigancia.get("int_parte_id")),
                numero(litigancia.get("int_polo_id")),
                numero(litigancia.get("int_situacao_uso_droga_id")),
                numero(litigancia.get("int_estado_civil_id")),
                numero(litigancia.get("int_escolaridade_id")),
                numero(litigancia.get("int_renda_id")),
                numero(litigancia.get("int_religiao_id")),
                numero(litigancia.get("int_posicao_prole_id")),
                numero(litigancia.get("int_raca_etnia_id")),
                (String) litigancia.get("str_observacoes_posicao_prole"),
                litigenciaOcupacaoRepository.findByIdLitigancia(acusado.getIdLitigancia()).stream()
                        .map(l -> l.getIdOcupacao())
                        .toList(),
                litigenciaDeficienciaRepository.findByIdLitigancia(acusado.getIdLitigancia()).stream()
                        .map(l -> l.getIdDeficiencia())
                        .toList(),
                litigenciaDrogaRepository.findByIdLitigancia(acusado.getIdLitigancia()).stream()
                        .map(l -> l.getIdDroga())
                        .toList(),
                litigenciaEscutaJudicialRepository.findByIdLitigancia(acusado.getIdLitigancia()).stream()
                        .findFirst()
                        .map(l -> l.getIdEscutaJudicial())
                        .orElse(null),
                null,
                null,
                beneficioRepository.findByIdLitigancia(acusado.getIdLitigancia()).stream()
                        .map(b -> new BeneficioDetalheDTO(b.getId(), b.getIdTipoBeneficio(), b.getDataInicio()))
                        .toList(),
                List.of(),
                acusado.getPossuiAntecedentes(),
                acusado.getReincidente(),
                acusado.getObservacaoAntecedentes());
    }

    private List<ComunicanteDetalheDTO> montarComunicantes(Long idFatoOcorrido) {
        return fatoOcorridoComunicanteRepository.findByIdFatoOcorrido(idFatoOcorrido).stream()
                .map(this::montarComunicante)
                .toList();
    }

    private ComunicanteDetalheDTO montarComunicante(FatoOcorridoComunicante associacao) {
        Comunicante comunicante = comunicanteRepository
                .findById(associacao.getIdComunicante())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Comunicante não encontrado: " + associacao.getIdComunicante()));
        return new ComunicanteDetalheDTO(
                comunicante.getId(),
                associacao.getId(),
                comunicante.getNome(),
                comunicante.getTelefone(),
                comunicante.getEmail(),
                comunicante.getCpfCnpj(),
                associacao.getIdTipoComunicante(),
                associacao.getDataDenuncia(),
                associacao.getObservacao(),
                Long.valueOf(1).equals(associacao.getAnonimizado()));
    }

    private List<ConsequenciaViolenciaDetalheDTO> montarConsequencias(
            Long idLitiganciaVitima, Long idLitiganciaAcusado) {
        List<ConsequenciaViolenciaDetalheDTO> consequencias = new java.util.ArrayList<>();
        consequenciaViolenciaRepository
                .findByIdLitigancia(idLitiganciaVitima)
                .forEach(c -> consequencias.add(new ConsequenciaViolenciaDetalheDTO(
                        c.getId(), "vitima", c.getIdTipoConsequenciaViolencia(), c.getObservacao())));
        consequenciaViolenciaRepository
                .findByIdLitigancia(idLitiganciaAcusado)
                .forEach(c -> consequencias.add(new ConsequenciaViolenciaDetalheDTO(
                        c.getId(), "acusado", c.getIdTipoConsequenciaViolencia(), c.getObservacao())));
        return consequencias;
    }

    /**
     * Consulta o perfil demográfico da litigância via pkg_litigancia.fn_litigancia_con.
     */
    private Map<String, Object> consultarLitigancia(Long idLitigancia) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idLitigancia", idLitigancia);
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                "SELECT * FROM pkg_litigancia.fn_litigancia_con(p_int_litigancia_id => :idLitigancia)", parametros);
        if (resultado.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Litigância não encontrada: " + idLitigancia);
        }
        return resultado.get(0);
    }

    /**
     * Monta a descrição legível do CEP (mesmo formato usado na busca de CEP do
     * frontend: "CEP — logradouro município/UF"), evitando que a tela de
     * edição/visualização mostre apenas o id numérico do registro de CEP.
     */
    private String descricaoCep(Long idCep) {
        if (idCep == null) {
            return null;
        }
        return cepRepository
                .findById(idCep)
                .map(cep -> "%s — %s %s/%s"
                        .formatted(
                                cep.getCep(),
                                cep.getLogradouro() == null ? "" : cep.getLogradouro(),
                                cep.getMunicipio() == null ? "" : cep.getMunicipio(),
                                cep.getUf() == null ? "" : cep.getUf()))
                .orElse(null);
    }

    private Long numero(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }
}
