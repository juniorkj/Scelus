package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.scelus.comum.AppException;
import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.comum.ResultadoFuncao;
import br.jus.tjma.scelus.dominio.dto.BeneficioWizardDTO;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeCompletoRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeCompletoResponse;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuRequest;
import br.jus.tjma.scelus.dominio.dto.ComunicanteWizardDTO;
import br.jus.tjma.scelus.dominio.dto.ConfiguracaoFamiliarWizardDTO;
import br.jus.tjma.scelus.dominio.dto.ConsequenciaViolenciaWizardDTO;
import br.jus.tjma.scelus.dominio.dto.CrimeCometidoWizardDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculoWizardDTO;
import br.jus.tjma.scelus.dominio.dto.ParteWizardDTO;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuRequest;
import br.jus.tjma.scelus.dominio.dto.VinculoWizardDTO;
import br.jus.tjma.scelus.dominio.model.Acusado;
import br.jus.tjma.scelus.dominio.model.Comunicante;
import br.jus.tjma.scelus.dominio.model.ConfiguracaoFamiliar;
import br.jus.tjma.scelus.dominio.model.ConsequenciaViolencia;
import br.jus.tjma.scelus.dominio.model.FatoOcorrido;
import br.jus.tjma.scelus.dominio.model.FatoOcorridoComunicante;
import br.jus.tjma.scelus.dominio.model.ProcessoCrime;
import br.jus.tjma.scelus.dominio.model.Vinculo;
import br.jus.tjma.scelus.dominio.model.Vitima;
import br.jus.tjma.scelus.dominio.repository.AcusadoRepository;
import br.jus.tjma.scelus.dominio.repository.BeneficioRepository;
import br.jus.tjma.scelus.dominio.repository.ComunicanteRepository;
import br.jus.tjma.scelus.dominio.repository.ConfiguracaoFamiliarRepository;
import br.jus.tjma.scelus.dominio.repository.ConsequenciaViolenciaRepository;
import br.jus.tjma.scelus.dominio.repository.FatoOcorridoComunicanteRepository;
import br.jus.tjma.scelus.dominio.repository.FatoOcorridoRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaDeficienciaRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaDrogaRepository;
import br.jus.tjma.scelus.dominio.repository.LitigenciaOcupacaoRepository;
import br.jus.tjma.scelus.dominio.repository.ProcessoCrimeRepository;
import br.jus.tjma.scelus.dominio.repository.VinculoRepository;
import br.jus.tjma.scelus.dominio.repository.VitimaRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço transacional do wizard completo de cadastro de crimes do processo
 * (CSU002), com persistência diferida — toda a árvore de dados (litigâncias, vítimas,
 * acusados, vínculos, fato ocorrido, comunicantes, MPUs e consequências da violência)
 * é gravada em uma única transação ao final do fluxo (RN01).
 *
 * <p>Prioriza as funções do banco (pkg_litigancia, pkg_fato_ocorrido, pkg_medida_protetiva);
 * usa JPA apenas para as tabelas sem função de inclusão correspondente (tb_comunicante,
 * tb_fato_ocorrido_comunicante, tb_configuracao_familiar, tb_consequencia_violencia,
 * tb_vitima, tb_acusado, tb_vinculo, tb_processo_crime).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Service
public class CadastroCrimeCompletoService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProcessoCrimeRepository processoCrimeRepository;
    private final VitimaRepository vitimaRepository;
    private final AcusadoRepository acusadoRepository;
    private final VinculoRepository vinculoRepository;
    private final ComunicanteRepository comunicanteRepository;
    private final FatoOcorridoRepository fatoOcorridoRepository;
    private final FatoOcorridoComunicanteRepository fatoOcorridoComunicanteRepository;
    private final ConfiguracaoFamiliarRepository configuracaoFamiliarRepository;
    private final ConsequenciaViolenciaRepository consequenciaViolenciaRepository;
    private final BeneficioRepository beneficioRepository;
    private final LitigenciaOcupacaoRepository litigenciaOcupacaoRepository;
    private final LitigenciaDeficienciaRepository litigenciaDeficienciaRepository;
    private final LitigenciaDrogaRepository litigenciaDrogaRepository;
    private final MpuService mpuService;

    public CadastroCrimeCompletoService(
            NamedParameterJdbcTemplate jdbcTemplate,
            ProcessoCrimeRepository processoCrimeRepository,
            VitimaRepository vitimaRepository,
            AcusadoRepository acusadoRepository,
            VinculoRepository vinculoRepository,
            ComunicanteRepository comunicanteRepository,
            FatoOcorridoRepository fatoOcorridoRepository,
            FatoOcorridoComunicanteRepository fatoOcorridoComunicanteRepository,
            ConfiguracaoFamiliarRepository configuracaoFamiliarRepository,
            ConsequenciaViolenciaRepository consequenciaViolenciaRepository,
            BeneficioRepository beneficioRepository,
            LitigenciaOcupacaoRepository litigenciaOcupacaoRepository,
            LitigenciaDeficienciaRepository litigenciaDeficienciaRepository,
            LitigenciaDrogaRepository litigenciaDrogaRepository,
            MpuService mpuService) {
        this.jdbcTemplate = jdbcTemplate;
        this.processoCrimeRepository = processoCrimeRepository;
        this.vitimaRepository = vitimaRepository;
        this.acusadoRepository = acusadoRepository;
        this.vinculoRepository = vinculoRepository;
        this.comunicanteRepository = comunicanteRepository;
        this.fatoOcorridoRepository = fatoOcorridoRepository;
        this.fatoOcorridoComunicanteRepository = fatoOcorridoComunicanteRepository;
        this.configuracaoFamiliarRepository = configuracaoFamiliarRepository;
        this.consequenciaViolenciaRepository = consequenciaViolenciaRepository;
        this.beneficioRepository = beneficioRepository;
        this.litigenciaOcupacaoRepository = litigenciaOcupacaoRepository;
        this.litigenciaDeficienciaRepository = litigenciaDeficienciaRepository;
        this.litigenciaDrogaRepository = litigenciaDrogaRepository;
        this.mpuService = mpuService;
    }

    /**
     * Processa o wizard completo do CSU002 numa única transação (RN01).
     *
     * @param request Payload consolidado do wizard (todas as telas 2.1 a 2.9).
     * @return Identificadores do fato ocorrido e do processo, e mensagem de sucesso do banco.
     */
    @Transactional
    public CadastroCrimeCompletoResponse cadastrar(CadastroCrimeCompletoRequest request) {
        Map<Long, ProcessoCrime> assuntoParaProcessoCrime = criarCrimesCometidos(request);

        Map<String, Long> chaveParaIdLitigancia = new HashMap<>();
        Map<String, Long> chaveParaIdVitima = new HashMap<>();
        Map<String, Long> chaveParaIdAcusado = new HashMap<>();

        for (ParteWizardDTO parteVitima : request.vitimas()) {
            if (parteVitima.idCep() == null) {
                throw new AppException("O CEP de residência da vítima '" + parteVitima.chave() + "' é obrigatório.");
            }
            Long idLitigancia = inserirLitigancia(parteVitima, request.numeroProcesso());
            processarAssociacoesLitigancia(idLitigancia, parteVitima);

            Vitima vitima = new Vitima();
            vitima.setIdLitigancia(idLitigancia);
            vitima.setIdCep(parteVitima.idCep());
            vitima = vitimaRepository.save(vitima);

            processarBeneficios(idLitigancia, parteVitima.beneficios());
            processarConfiguracoesFamiliares(vitima.getId(), parteVitima.configuracoesFamiliares());

            chaveParaIdLitigancia.put(parteVitima.chave(), idLitigancia);
            chaveParaIdVitima.put(parteVitima.chave(), vitima.getId());
        }

        for (ParteWizardDTO parteAcusado : request.acusados()) {
            Long idLitigancia = inserirLitigancia(parteAcusado, request.numeroProcesso());
            processarAssociacoesLitigancia(idLitigancia, parteAcusado);

            Acusado acusado = new Acusado();
            acusado.setIdLitigancia(idLitigancia);
            acusado.setPossuiAntecedentes(parteAcusado.possuiAntecedentes());
            acusado.setReincidente(parteAcusado.reincidente());
            acusado.setObservacaoAntecedentes(parteAcusado.observacaoAntecedentes());
            acusado = acusadoRepository.save(acusado);

            processarBeneficios(idLitigancia, parteAcusado.beneficios());

            chaveParaIdLitigancia.put(parteAcusado.chave(), idLitigancia);
            chaveParaIdAcusado.put(parteAcusado.chave(), acusado.getId());
        }

        if (request.vinculos() != null) {
            for (VinculoWizardDTO vinculoDTO : request.vinculos()) {
                Long idVitima = idVitimaPorChave(chaveParaIdVitima, vinculoDTO.chaveVitima());
                Long idAcusado = idAcusadoPorChave(chaveParaIdAcusado, vinculoDTO.chaveAcusado());

                Vinculo vinculo = new Vinculo();
                vinculo.setIdVitima(idVitima);
                vinculo.setIdAcusado(idAcusado);
                vinculo.setIdTipoVinculo(vinculoDTO.idTipoVinculo());
                vinculo.setObservacao(vinculoDTO.observacao());
                vinculoRepository.save(vinculo);
            }
        }

        Long idVitimaFato =
                idVitimaPorChave(chaveParaIdVitima, request.fatoOcorrido().chaveVitima());
        Long idAcusadoFato =
                idAcusadoPorChave(chaveParaIdAcusado, request.fatoOcorrido().chaveAcusado());
        ProcessoCrime processoCrimeFato = processoCrimePorAssunto(
                assuntoParaProcessoCrime, request.fatoOcorrido().codigoAssunto());

        ResultadoFuncao fato = inserirFatoOcorrido(request, idVitimaFato, idAcusadoFato, processoCrimeFato.getId());
        fato.validar();
        Long idFatoOcorrido = fato.id();

        if (request.fatoOcorrido().comunicantes() != null) {
            for (ComunicanteWizardDTO comunicanteDTO : request.fatoOcorrido().comunicantes()) {
                processarComunicante(idFatoOcorrido, comunicanteDTO);
            }
        }

        if (request.mpus() != null) {
            for (MpuVinculoWizardDTO mpuDTO : request.mpus()) {
                processarMpu(idFatoOcorrido, idVitimaFato, idAcusadoFato, mpuDTO);
            }
        }

        if (request.consequenciasViolencia() != null) {
            for (ConsequenciaViolenciaWizardDTO consequenciaDTO : request.consequenciasViolencia()) {
                Long idLitigancia = chaveParaIdLitigancia.get(consequenciaDTO.chaveParte());
                if (idLitigancia == null) {
                    throw new AppException(
                            "Parte não encontrada para a consequência da violência: " + consequenciaDTO.chaveParte());
                }
                ConsequenciaViolencia consequencia = new ConsequenciaViolencia();
                consequencia.setIdLitigancia(idLitigancia);
                consequencia.setIdTipoConsequenciaViolencia(consequenciaDTO.idTipoConsequenciaViolencia());
                consequencia.setObservacao(consequenciaDTO.observacao());
                consequenciaViolenciaRepository.save(consequencia);
            }
        }

        return new CadastroCrimeCompletoResponse(idFatoOcorrido, processoCrimeFato.getId(), fato.mensagem());
    }

    /**
     * Atualiza um crime/fato ocorrido já cadastrado (edição via wizard), com
     * atualização cirúrgica por tabela: cada bloco (crimes cometidos, perfil da
     * parte, associações, benefícios, configuração familiar, vínculo,
     * comunicantes e consequências) é comparado com o estado atual e ajustado
     * por update/insert/delete pontual — preservando os identificadores
     * estáveis (litigância, vítima, acusado, fato ocorrido, processo de crime).
     *
     * @param idFatoOcorrido Identificador do fato ocorrido a ser atualizado.
     * @param request Payload consolidado do wizard com os dados editados.
     * @return Identificadores do fato ocorrido e do processo, e mensagem de sucesso do banco.
     */
    @Transactional
    public CadastroCrimeCompletoResponse atualizar(Long idFatoOcorrido, CadastroCrimeCompletoRequest request) {
        FatoOcorrido fato = fatoOcorridoRepository
                .findById(idFatoOcorrido)
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Fato ocorrido não encontrado: " + idFatoOcorrido));

        Vitima vitima = vitimaRepository
                .findById(fato.getIdVitima())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Vítima não encontrada: " + fato.getIdVitima()));
        Acusado acusado = acusadoRepository
                .findById(fato.getIdAcusado())
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Acusado não encontrado: " + fato.getIdAcusado()));

        ParteWizardDTO parteVitima = primeiraParte(request.vitimas(), "vítima");
        ParteWizardDTO parteAcusado = primeiraParte(request.acusados(), "acusado");

        if (parteVitima.idCep() == null) {
            throw new AppException("O CEP de residência da vítima é obrigatório.");
        }

        Map<Long, ProcessoCrime> assuntoParaProcessoCrime =
                atualizarCrimesCometidos(request.numeroProcesso(), request.crimesCometidos());

        atualizarPerfilLitigancia(vitima.getIdLitigancia(), request.numeroProcesso(), parteVitima);
        atualizarAssociacoesLitigancia(vitima.getIdLitigancia(), parteVitima);
        vitima.setIdCep(parteVitima.idCep());
        vitimaRepository.save(vitima);
        atualizarBeneficios(vitima.getIdLitigancia(), parteVitima.beneficios());
        atualizarConfiguracoesFamiliares(vitima.getId(), parteVitima.configuracoesFamiliares());

        atualizarPerfilLitigancia(acusado.getIdLitigancia(), request.numeroProcesso(), parteAcusado);
        atualizarAssociacoesLitigancia(acusado.getIdLitigancia(), parteAcusado);
        acusado.setPossuiAntecedentes(parteAcusado.possuiAntecedentes());
        acusado.setReincidente(parteAcusado.reincidente());
        acusado.setObservacaoAntecedentes(parteAcusado.observacaoAntecedentes());
        acusadoRepository.save(acusado);
        atualizarBeneficios(acusado.getIdLitigancia(), parteAcusado.beneficios());

        atualizarVinculo(vitima.getId(), acusado.getId(), request.vinculos());

        ProcessoCrime processoCrimeFato = processoCrimePorAssunto(
                assuntoParaProcessoCrime, request.fatoOcorrido().codigoAssunto());

        MapSqlParameterSource parametrosFato = new MapSqlParameterSource()
                .addValue("id", idFatoOcorrido)
                .addValue("idAcusado", acusado.getId())
                .addValue("idVitima", vitima.getId())
                .addValue("dataFato", paraTimestamp(request.fatoOcorrido().dataFato()))
                .addValue("idCep", request.fatoOcorrido().idCep())
                .addValue("medidaProtetiva", request.fatoOcorrido().medidaProtetiva())
                .addValue("idProcessoCrime", processoCrimeFato.getId());
        String mensagemFato = jdbcTemplate.queryForObject(
                "SELECT pkg_fato_ocorrido.fn_fato_ocorrido_upd("
                        + ":id, :idAcusado, :idVitima, :dataFato, :idCep, :medidaProtetiva, :idProcessoCrime)",
                parametrosFato,
                String.class);
        validarMensagem(mensagemFato);

        atualizarComunicantes(idFatoOcorrido, request.fatoOcorrido().comunicantes());
        atualizarConsequencias(
                vitima.getIdLitigancia(),
                acusado.getIdLitigancia(),
                parteVitima.chave(),
                parteAcusado.chave(),
                request.consequenciasViolencia());

        return new CadastroCrimeCompletoResponse(idFatoOcorrido, processoCrimeFato.getId(), mensagemFato);
    }

    private ParteWizardDTO primeiraParte(List<ParteWizardDTO> partes, String rotulo) {
        if (partes == null || partes.isEmpty()) {
            throw new AppException("Os dados da " + rotulo + " são obrigatórios.");
        }
        return partes.get(0);
    }

    /**
     * Cria os crimes cometidos ainda não existentes e atualiza a tipificação dos
     * já cadastrados. Crimes cometidos removidos da tela não são excluídos aqui —
     * a exclusão de um crime já referenciado por outros fatos é responsabilidade
     * de um fluxo dedicado, para não arriscar órfãos por FK.
     */
    private Map<Long, ProcessoCrime> atualizarCrimesCometidos(
            String numeroProcesso, List<CrimeCometidoWizardDTO> crimesCometidos) {
        Map<Long, ProcessoCrime> existentes = processoCrimeRepository.findByNumeroUnico(numeroProcesso).stream()
                .collect(java.util.stream.Collectors.toMap(ProcessoCrime::getCodigoAssunto, pc -> pc));

        Map<Long, ProcessoCrime> assuntoParaProcessoCrime = new HashMap<>();
        for (CrimeCometidoWizardDTO crimeCometido : crimesCometidos) {
            Long codigoAssunto = crimeCometido.codigoAssunto().longValue();
            ProcessoCrime processoCrime = existentes.get(codigoAssunto);
            if (processoCrime == null) {
                processoCrime = new ProcessoCrime();
                processoCrime.setNumeroUnico(numeroProcesso);
                processoCrime.setCodigoAssunto(codigoAssunto);
            }
            processoCrime.setDescricaoAssunto(crimeCometido.descricaoAssunto());
            processoCrime.setDataInicioTipificacao(crimeCometido.dataInicioTipificacao());
            processoCrime.setDataFimTipificacao(crimeCometido.dataFimTipificacao());
            processoCrime = processoCrimeRepository.save(processoCrime);
            assuntoParaProcessoCrime.put(codigoAssunto, processoCrime);
        }
        return assuntoParaProcessoCrime;
    }

    /**
     * Atualiza o perfil demográfico da litigância via pkg_litigancia.fn_litigancia_upd.
     */
    private void atualizarPerfilLitigancia(Long idLitigancia, String numeroProcesso, ParteWizardDTO parte) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idLitigancia", idLitigancia)
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

        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_litigancia.fn_litigancia_upd("
                        + ":idLitigancia, :idPolo, :numeroUnico, :idParte, :idSituacaoUsoDroga, :idEstadoCivil, "
                        + ":idEscolaridade, :idRenda, :idReligiao, :idPosicaoProle, :idRacaEtnia, "
                        + ":observacoesPosicaoProle)",
                parametros,
                String.class);
        validarMensagem(mensagem);
    }

    /**
     * Substitui as associações N:M da litigância (ocupações, deficiências, escuta
     * judicial e drogas) pelas informadas na tela — exclui todas as existentes e
     * reinsere as atuais, reaplicando a RN02.
     */
    private void atualizarAssociacoesLitigancia(Long idLitigancia, ParteWizardDTO parte) {
        litigenciaOcupacaoRepository
                .findByIdLitigancia(idLitigancia)
                .forEach(a -> executarExclusaoAssociacao("pkg_litigancia.fn_litigancia_ocupacao_del(:id)", a.getId()));
        litigenciaDeficienciaRepository
                .findByIdLitigancia(idLitigancia)
                .forEach(a ->
                        executarExclusaoAssociacao("pkg_litigancia.fn_litigancia_deficiencia_del(:id)", a.getId()));
        litigenciaDrogaRepository
                .findByIdLitigancia(idLitigancia)
                .forEach(a -> executarExclusaoAssociacao("pkg_litigancia.fn_litigancia_droga_del(:id)", a.getId()));

        processarAssociacoesLitigancia(idLitigancia, parte);
    }

    private void executarExclusaoAssociacao(String chamadaFuncao, Long id) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("id", id);
        String mensagem = jdbcTemplate.queryForObject("SELECT " + chamadaFuncao, parametros, String.class);
        validarMensagem(mensagem);
    }

    /**
     * Substitui os benefícios da litigância pelos informados na tela.
     */
    private void atualizarBeneficios(Long idLitigancia, List<BeneficioWizardDTO> beneficios) {
        beneficioRepository.deleteAll(beneficioRepository.findByIdLitigancia(idLitigancia));
        processarBeneficios(idLitigancia, beneficios);
    }

    /**
     * Substitui as configurações familiares da vítima pelas informadas na tela.
     */
    private void atualizarConfiguracoesFamiliares(Long idVitima, List<ConfiguracaoFamiliarWizardDTO> configuracoes) {
        configuracaoFamiliarRepository.deleteAll(configuracaoFamiliarRepository.findByIdVitima(idVitima));
        processarConfiguracoesFamiliares(idVitima, configuracoes);
    }

    /**
     * Cria, atualiza ou remove o vínculo entre a vítima e o acusado conforme
     * informado (ou não) na tela.
     */
    private void atualizarVinculo(Long idVitima, Long idAcusado, List<VinculoWizardDTO> vinculos) {
        var existente = vinculoRepository.findFirstByIdVitimaAndIdAcusado(idVitima, idAcusado);
        var novoOpt = vinculos == null
                ? java.util.Optional.<VinculoWizardDTO>empty()
                : vinculos.stream().findFirst();

        if (novoOpt.isEmpty()) {
            existente.ifPresent(vinculoRepository::delete);
            return;
        }

        VinculoWizardDTO novo = novoOpt.get();
        Vinculo vinculo = existente.orElseGet(Vinculo::new);
        vinculo.setIdVitima(idVitima);
        vinculo.setIdAcusado(idAcusado);
        vinculo.setIdTipoVinculo(novo.idTipoVinculo());
        vinculo.setObservacao(novo.observacao());
        vinculoRepository.save(vinculo);
    }

    /**
     * Substitui os comunicantes do fato ocorrido pelos informados na tela.
     */
    private void atualizarComunicantes(Long idFatoOcorrido, List<ComunicanteWizardDTO> comunicantes) {
        for (FatoOcorridoComunicante associacao :
                fatoOcorridoComunicanteRepository.findByIdFatoOcorrido(idFatoOcorrido)) {
            fatoOcorridoComunicanteRepository.delete(associacao);
            comunicanteRepository.deleteById(associacao.getIdComunicante());
        }
        if (comunicantes != null) {
            for (ComunicanteWizardDTO comunicanteDTO : comunicantes) {
                processarComunicante(idFatoOcorrido, comunicanteDTO);
            }
        }
    }

    /**
     * Substitui as consequências da violência da vítima e do acusado pelas
     * informadas na tela.
     */
    private void atualizarConsequencias(
            Long idLitiganciaVitima,
            Long idLitiganciaAcusado,
            String chaveVitima,
            String chaveAcusado,
            List<ConsequenciaViolenciaWizardDTO> consequencias) {
        consequenciaViolenciaRepository.deleteAll(
                consequenciaViolenciaRepository.findByIdLitigancia(idLitiganciaVitima));
        consequenciaViolenciaRepository.deleteAll(
                consequenciaViolenciaRepository.findByIdLitigancia(idLitiganciaAcusado));

        if (consequencias == null) {
            return;
        }
        Map<String, Long> chaveParaLitigancia = new HashMap<>();
        chaveParaLitigancia.put(chaveVitima, idLitiganciaVitima);
        chaveParaLitigancia.put(chaveAcusado, idLitiganciaAcusado);

        for (ConsequenciaViolenciaWizardDTO consequenciaDTO : consequencias) {
            Long idLitigancia = chaveParaLitigancia.get(consequenciaDTO.chaveParte());
            if (idLitigancia == null) {
                throw new AppException(
                        "Parte não encontrada para a consequência da violência: " + consequenciaDTO.chaveParte());
            }
            ConsequenciaViolencia consequencia = new ConsequenciaViolencia();
            consequencia.setIdLitigancia(idLitigancia);
            consequencia.setIdTipoConsequenciaViolencia(consequenciaDTO.idTipoConsequenciaViolencia());
            consequencia.setObservacao(consequenciaDTO.observacao());
            consequenciaViolenciaRepository.save(consequencia);
        }
    }

    private void validarMensagem(String mensagem) {
        if (mensagem == null || !mensagem.contains("GER-S")) {
            throw new AppException(mensagem);
        }
    }

    private Long idVitimaPorChave(Map<String, Long> chaveParaIdVitima, String chave) {
        Long id = chaveParaIdVitima.get(chave);
        if (id == null) {
            throw new AppException("Vítima não encontrada para a chave: " + chave);
        }
        return id;
    }

    private Long idAcusadoPorChave(Map<String, Long> chaveParaIdAcusado, String chave) {
        Long id = chaveParaIdAcusado.get(chave);
        if (id == null) {
            throw new AppException("Acusado não encontrado para a chave: " + chave);
        }
        return id;
    }

    /**
     * Cria (ou reaproveita, se já existente) um {@code tb_processo_crime} por crime
     * cometido informado na Tela 2.2 do wizard — cada crime corresponde a um assunto
     * do processo (PJe) tipificado com data de início e, opcionalmente, fim.
     *
     * @return mapa código do assunto → registro de processo de crime, usado para
     *     correlacionar o fato ocorrido (Tela 2.8) ao crime selecionado.
     */
    private Map<Long, ProcessoCrime> criarCrimesCometidos(CadastroCrimeCompletoRequest request) {
        Map<Long, ProcessoCrime> assuntoParaProcessoCrime = new HashMap<>();
        for (CrimeCometidoWizardDTO crimeCometido : request.crimesCometidos()) {
            Long codigoAssunto = crimeCometido.codigoAssunto().longValue();
            ProcessoCrime processoCrime = processoCrimeRepository
                    .findFirstByNumeroUnicoAndCodigoAssunto(request.numeroProcesso(), codigoAssunto)
                    .orElseGet(() -> {
                        ProcessoCrime novo = new ProcessoCrime();
                        novo.setNumeroUnico(request.numeroProcesso());
                        novo.setCodigoAssunto(codigoAssunto);
                        novo.setDescricaoAssunto(crimeCometido.descricaoAssunto());
                        novo.setDataInicioTipificacao(crimeCometido.dataInicioTipificacao());
                        novo.setDataFimTipificacao(crimeCometido.dataFimTipificacao());
                        return processoCrimeRepository.save(novo);
                    });
            assuntoParaProcessoCrime.put(codigoAssunto, processoCrime);
        }
        return assuntoParaProcessoCrime;
    }

    private ProcessoCrime processoCrimePorAssunto(
            Map<Long, ProcessoCrime> assuntoParaProcessoCrime, Integer codigoAssunto) {
        ProcessoCrime processoCrime = assuntoParaProcessoCrime.get(codigoAssunto.longValue());
        if (processoCrime == null) {
            throw new AppException(
                    "O crime informado no fato ocorrido não foi cadastrado na etapa de Crimes Cometidos: "
                            + codigoAssunto);
        }
        return processoCrime;
    }

    /**
     * Insere a litigância da parte via pkg_litigancia.fn_litigancia_ins e retorna o id gerado.
     */
    private Long inserirLitigancia(ParteWizardDTO parte, String numeroProcesso) {
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
     * Processa as associações N:M da litigância: ocupações, deficiências, escuta
     * judicial e drogas (com a regra RN02 aplicada em {@link #processarDrogas}).
     */
    private void processarAssociacoesLitigancia(Long idLitigancia, ParteWizardDTO parte) {
        if (parte.idsOcupacao() != null) {
            for (Long idOcupacao : parte.idsOcupacao()) {
                executarInsercaoAssociacao(
                        "pkg_litigancia.fn_litigancia_ocupacao_ins(:idAssociado, :idLitigancia)",
                        "p_int_litigancia_ocupacao_id",
                        idOcupacao,
                        idLitigancia);
            }
        }
        if (parte.idsDeficiencia() != null) {
            for (Long idDeficiencia : parte.idsDeficiencia()) {
                executarInsercaoAssociacao(
                        "pkg_litigancia.fn_litigancia_deficiencia_ins(:idAssociado, :idLitigancia)",
                        "p_int_litigancia_deficiencia_id",
                        idDeficiencia,
                        idLitigancia);
            }
        }
        processarDrogas(idLitigancia, parte.idSituacaoUsoDroga(), parte.idsDroga());
    }

    /**
     * RN02: se a situação de uso de droga indicar que a parte não é usuária ou que
     * a informação não foi especificada, o sistema não persiste em tb_litigancia_droga,
     * mesmo que valores tenham sido informados na tela.
     */
    private void processarDrogas(Long idLitigancia, Long idSituacaoUsoDroga, List<Long> idsDroga) {
        if (idsDroga == null || idsDroga.isEmpty()) {
            return;
        }
        if (idSituacaoUsoDroga == null) {
            throw new AppException(
                    "A situação de uso de droga é obrigatória quando há substâncias associadas à parte.");
        }
        if (indicaNaoUsuarioOuNaoEspecificado(idSituacaoUsoDroga)) {
            return;
        }
        for (Long idDroga : idsDroga) {
            executarInsercaoAssociacao(
                    "pkg_litigancia.fn_litigancia_droga_ins(:idAssociado, :idLitigancia)",
                    "p_int_litigancia_droga_id",
                    idDroga,
                    idLitigancia);
        }
    }

    private boolean indicaNaoUsuarioOuNaoEspecificado(Long idSituacaoUsoDroga) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("id", idSituacaoUsoDroga);
        String descricao = jdbcTemplate.queryForObject(
                "SELECT str_situacao_uso_droga FROM public.tb_situacao_uso_droga WHERE int_situacao_uso_droga_id = :id",
                parametros,
                String.class);
        if (descricao == null) {
            return false;
        }
        String normalizada = descricao.toUpperCase(java.util.Locale.ROOT);
        return normalizada.contains("NÃO USU")
                || normalizada.contains("NAO USU")
                || normalizada.contains("NÃO ESPECIF")
                || normalizada.contains("NAO ESPECIF");
    }

    /**
     * Executa uma função fn_litigancia_*_ins de associação N:M (ocupação, deficiência
     * ou droga), todas com a assinatura (idAssociado, idLitigancia) e retorno
     * (id da associação, mensagem) — apenas o nome da coluna OUT do id muda.
     */
    private void executarInsercaoAssociacao(
            String chamadaFuncao, String colunaId, Long idAssociado, Long idLitigancia) {
        MapSqlParameterSource parametros =
                new MapSqlParameterSource().addValue("idAssociado", idAssociado).addValue("idLitigancia", idLitigancia);
        ResultadoFuncao resultado = jdbcTemplate.queryForObject(
                "SELECT " + colunaId + " AS id, p_resultado AS mensagem FROM " + chamadaFuncao,
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));
        resultado.validar();
    }

    private void processarBeneficios(Long idLitigancia, List<BeneficioWizardDTO> beneficios) {
        if (beneficios == null) {
            return;
        }
        for (BeneficioWizardDTO beneficio : beneficios) {
            MapSqlParameterSource parametros = new MapSqlParameterSource()
                    .addValue("idLitigancia", idLitigancia)
                    .addValue("idTipoBeneficio", beneficio.idTipoBeneficio())
                    .addValue("dataInicio", paraTimestamp(beneficio.dataInicio()));

            ResultadoFuncao resultado = jdbcTemplate.queryForObject(
                    "SELECT p_int_beneficio_id AS id, p_resultado AS mensagem "
                            + "FROM pkg_litigancia.fn_litigancia_beneficio_ins(:idLitigancia, :idTipoBeneficio, :dataInicio)",
                    parametros,
                    (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));
            resultado.validar();
        }
    }

    private void processarConfiguracoesFamiliares(Long idVitima, List<ConfiguracaoFamiliarWizardDTO> configuracoes) {
        if (configuracoes == null) {
            return;
        }
        for (ConfiguracaoFamiliarWizardDTO configuracaoDTO : configuracoes) {
            ConfiguracaoFamiliar configuracao = new ConfiguracaoFamiliar();
            configuracao.setIdVitima(idVitima);
            configuracao.setIdTipoConfiguracaoFamiliar(configuracaoDTO.idTipoConfiguracaoFamiliar());
            configuracao.setDataDeclaracao(configuracaoDTO.dataDeclaracao());
            configuracao.setObservacao(configuracaoDTO.observacao());
            configuracaoFamiliarRepository.save(configuracao);
        }
    }

    private void processarComunicante(Long idFatoOcorrido, ComunicanteWizardDTO comunicanteDTO) {
        Comunicante comunicante = new Comunicante();
        comunicante.setNome(comunicanteDTO.nome());
        comunicante.setTelefone(comunicanteDTO.telefone());
        comunicante.setEmail(comunicanteDTO.email());
        comunicante.setCpfCnpj(comunicanteDTO.cpfCnpj());
        comunicante = comunicanteRepository.save(comunicante);

        FatoOcorridoComunicante associacao = new FatoOcorridoComunicante();
        associacao.setIdFatoOcorrido(idFatoOcorrido);
        associacao.setIdComunicante(comunicante.getId());
        associacao.setIdTipoComunicante(comunicanteDTO.idTipoComunicante());
        associacao.setDataDenuncia(comunicanteDTO.dataDenuncia());
        associacao.setObservacao(comunicanteDTO.observacao());
        associacao.setAnonimizado(Boolean.TRUE.equals(comunicanteDTO.anonimizado()) ? 1L : 0L);
        fatoOcorridoComunicanteRepository.save(associacao);
    }

    /**
     * Vincula uma MPU existente (RN008.04/05) ou cadastra uma nova MPU (RN008.01) e
     * a vincula ao fato ocorrido — ambos exigindo justificativa de inclusão.
     */
    private void processarMpu(Long idFatoOcorrido, Long idVitima, Long idAcusado, MpuVinculoWizardDTO mpuDTO) {
        Long idMpu = mpuDTO.idMpuExistente();

        if (idMpu == null) {
            if (mpuDTO.novaMpu() == null) {
                throw new AppException("Informe uma MPU existente ou os dados de uma nova MPU.");
            }
            CadastroMpuRequest novaMpuComPartes = new CadastroMpuRequest(
                    mpuDTO.novaMpu().legislacaoFundamento(),
                    mpuDTO.novaMpu().dataDecisao(),
                    mpuDTO.novaMpu().concedida(),
                    mpuDTO.novaMpu().dataIntimacaoAcusado(),
                    mpuDTO.novaMpu().dataIntimacaoVitima(),
                    mpuDTO.novaMpu().dataCienciaVitima(),
                    mpuDTO.novaMpu().dataCienciaAcusado(),
                    mpuDTO.novaMpu().pedidoDesistencia(),
                    mpuDTO.novaMpu().inqueritoInstaurado(),
                    mpuDTO.novaMpu().observacoes(),
                    idAcusado,
                    idVitima,
                    mpuDTO.novaMpu().numeroMpu(),
                    mpuDTO.novaMpu().numeroUnico());
            idMpu = mpuService.cadastrarMpu(novaMpuComPartes).idMpu();
        }

        mpuService.vincularMpu(
                idFatoOcorrido,
                new VinculoMpuRequest(idMpu, mpuDTO.idJustificativaInclusaoMpu(), mpuDTO.observacaoJustificativa()));
    }

    /**
     * Insere o fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_ins.
     */
    private ResultadoFuncao inserirFatoOcorrido(
            CadastroCrimeCompletoRequest request, Long idVitima, Long idAcusado, Long idProcessoCrime) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idAcusado", idAcusado)
                .addValue("idVitima", idVitima)
                .addValue("dataFato", paraTimestamp(request.fatoOcorrido().dataFato()))
                .addValue("idCep", request.fatoOcorrido().idCep())
                .addValue("medidaProtetiva", request.fatoOcorrido().medidaProtetiva())
                .addValue("idProcessoCrime", idProcessoCrime);

        return jdbcTemplate.queryForObject(
                "SELECT p_int_fato_ocorrido_id AS id, p_resultado AS mensagem "
                        + "FROM pkg_fato_ocorrido.fn_fato_ocorrido_ins("
                        + ":idAcusado, :idVitima, :dataFato, :idCep, :medidaProtetiva, :idProcessoCrime)",
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));
    }

    private Timestamp paraTimestamp(LocalDateTime dataHora) {
        return dataHora != null ? Timestamp.valueOf(dataHora) : null;
    }

    private ResultadoFuncao mapearResultado(long id, boolean idNulo, String mensagem) {
        return new ResultadoFuncao(idNulo ? null : id, mensagem);
    }
}
