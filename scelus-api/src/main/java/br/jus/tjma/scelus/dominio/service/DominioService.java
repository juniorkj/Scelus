package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.scelus.comum.AppException;
import br.jus.tjma.scelus.dominio.dto.CepDTO;
import br.jus.tjma.scelus.dominio.dto.ItemDominioDTO;
import br.jus.tjma.scelus.dominio.dto.ViaCepResposta;
import br.jus.tjma.scelus.dominio.model.Cep;
import br.jus.tjma.scelus.dominio.repository.CepRepository;
import br.jus.tjma.scelus.dominio.repository.JustificativaInclusaoMpuRepository;
import br.jus.tjma.scelus.dominio.repository.PoloRepository;
import br.jus.tjma.scelus.dominio.repository.TipoVinculoRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * javadoc Serviço de consulta das tabelas de domínio (tipos de vínculo, polos,
 * justificativas de inclusão de MPU e CEPs) para popular os componentes do frontend.
 *
 * <p>A pesquisa de CEP completo (8 dígitos) não encontrado na base local consulta a
 * API pública ViaCEP e registra o endereço automaticamente em {@code tb_cep}.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.1
 * @since 11/07/2026
 */
@Service
@Transactional(readOnly = true)
public class DominioService {

    private static final Logger log = LoggerFactory.getLogger(DominioService.class);

    /**
     * Whitelist dos domínios genéricos expostos em GET /api/dominios/{chave}
     * (tabela, coluna id, coluna descrição) — usados nos filtros avançados do CSU001.
     */
    private static final java.util.Map<String, String[]> DOMINIOS_GENERICOS = java.util.Map.ofEntries(
            java.util.Map.entry("ocupacoes", new String[] {"tb_ocupacao", "int_ocupacao_id", "str_ocupacao"}),
            java.util.Map.entry(
                    "deficiencias", new String[] {"tb_deficiencia", "int_deficiencia_id", "str_deficiencia"}),
            java.util.Map.entry(
                    "estados-civis", new String[] {"tb_estado_civil", "int_estado_civil_id", "str_estado_civil"}),
            java.util.Map.entry("religioes", new String[] {"tb_religiao", "int_religiao_id", "str_religiao"}),
            java.util.Map.entry(
                    "escolaridades", new String[] {"tb_escolaridade", "int_escolaridade_id", "str_escolaridade"}),
            java.util.Map.entry("rendas", new String[] {"tb_renda", "int_renda_id", "str_renda"}),
            java.util.Map.entry("drogas", new String[] {"tb_droga", "int_droga_id", "str_droga"}),
            java.util.Map.entry("racas-etnias", new String[] {"tb_raca_etnia", "int_raca_etnia_id", "str_raca_etnia"}),
            java.util.Map.entry(
                    "tipos-beneficio",
                    new String[] {"tb_tipo_beneficio", "int_tipo_beneficio_id", "str_tipo_beneficio"}),
            java.util.Map.entry("consequencias-violencia", new String[] {
                "tb_tipo_consequencia_violencia",
                "int_tipo_consequencia_violencia_id",
                "str_tipo_consequencia_violencia"
            }));

    private final TipoVinculoRepository tipoVinculoRepository;
    private final PoloRepository poloRepository;
    private final JustificativaInclusaoMpuRepository justificativaRepository;
    private final CepRepository cepRepository;
    private final org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate jdbcTemplate;
    private final RestClient viaCepClient;

    public DominioService(
            TipoVinculoRepository tipoVinculoRepository,
            PoloRepository poloRepository,
            JustificativaInclusaoMpuRepository justificativaRepository,
            CepRepository cepRepository,
            org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate jdbcTemplate,
            @Value("${scelus.viacep.url:https://viacep.com.br/ws}") String viaCepUrl) {
        this.tipoVinculoRepository = tipoVinculoRepository;
        this.poloRepository = poloRepository;
        this.justificativaRepository = justificativaRepository;
        this.cepRepository = cepRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.viaCepClient = RestClient.builder().baseUrl(viaCepUrl).build();
    }

    /**
     * Lista um domínio genérico da whitelist (ocupações, deficiências, drogas, etc.).
     *
     * @param chave Chave do domínio na URL (ex.: "ocupacoes", "racas-etnias").
     * @return Itens id + descrição ordenados pela descrição.
     */
    public List<ItemDominioDTO> listarDominio(String chave) {
        String[] config = DOMINIOS_GENERICOS.get(chave);
        if (config == null) {
            throw new br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException("Domínio não encontrado: " + chave);
        }
        String sql = "SELECT " + config[1] + " AS id, " + config[2] + " AS descricao FROM public." + config[0]
                + " ORDER BY 2";
        return jdbcTemplate.query(
                sql,
                new org.springframework.jdbc.core.namedparam.MapSqlParameterSource(),
                (rs, rowNum) -> new ItemDominioDTO(rs.getLong("id"), rs.getString("descricao")));
    }

    /**
     * Lista os tipos de vínculo entre vítima e acusado.
     */
    public List<ItemDominioDTO> listarTiposVinculo() {
        return tipoVinculoRepository.findAll(Sort.by("descricao")).stream()
                .map(t -> new ItemDominioDTO(t.getId(), t.getDescricao()))
                .toList();
    }

    /**
     * Lista os polos processuais.
     */
    public List<ItemDominioDTO> listarPolos() {
        return poloRepository.findAll(Sort.by("id")).stream()
                .map(p -> new ItemDominioDTO(p.getId(), p.getDescricao()))
                .toList();
    }

    /**
     * Lista as justificativas de inclusão de MPU.
     */
    public List<ItemDominioDTO> listarJustificativasInclusaoMpu() {
        return justificativaRepository.findAll(Sort.by("id")).stream()
                .map(j -> new ItemDominioDTO(j.getId(), j.getDescricao()))
                .toList();
    }

    /**
     * Pesquisa CEPs pelo valor informado.
     *
     * <p>Com 8 dígitos, busca o CEP exato na base local; se não existir, consulta o
     * ViaCEP, registra em {@code tb_cep} e retorna. Com menos dígitos, faz a
     * pesquisa por prefixo apenas na base local.
     */
    @Transactional
    public List<CepDTO> pesquisarCeps(String cep) {
        String digitos = cep == null ? "" : cep.replaceAll("\\D", "");

        if (digitos.length() == 8) {
            String formatado = digitos.substring(0, 5) + "-" + digitos.substring(5);
            Cep local = cepRepository
                    .findFirstByCep(formatado)
                    .orElseGet(() -> consultarERegistrarViaCep(digitos, formatado));
            return local == null ? List.of() : List.of(paraDTO(local));
        }

        return cepRepository.findTop20ByCepStartingWithOrderByCep(digitos).stream()
                .map(this::paraDTO)
                .toList();
    }

    /**
     * Consulta o CEP no ViaCEP e registra o endereço em tb_cep (não há função de banco para CEP).
     *
     * @return o CEP registrado, ou {@code null} quando o ViaCEP indica CEP inexistente.
     */
    private Cep consultarERegistrarViaCep(String digitos, String formatado) {
        ViaCepResposta resposta;
        try {
            resposta =
                    viaCepClient.get().uri("/{cep}/json/", digitos).retrieve().body(ViaCepResposta.class);
        } catch (RestClientException e) {
            log.warn("Falha ao consultar o ViaCEP para {}: {}", formatado, e.getMessage());
            throw new AppException(
                    "VIACEP_INDISPONIVEL",
                    "Serviço de consulta de CEP (ViaCEP) indisponível no momento. Tente novamente.");
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            return null;
        }

        Cep novo = new Cep();
        novo.setCep(Optional.ofNullable(resposta.cep()).orElse(formatado));
        novo.setLogradouro(resposta.logradouro());
        novo.setBairro(resposta.bairro());
        novo.setMunicipio(resposta.localidade());
        novo.setUf(resposta.uf());
        Cep salvo = cepRepository.save(novo);
        log.info("CEP {} registrado automaticamente via ViaCEP (id={}).", salvo.getCep(), salvo.getId());
        return salvo;
    }

    private CepDTO paraDTO(Cep cep) {
        return new CepDTO(
                cep.getId(), cep.getCep(), cep.getLogradouro(), cep.getBairro(), cep.getMunicipio(), cep.getUf());
    }
}
