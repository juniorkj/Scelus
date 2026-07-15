package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.dominio.dto.CrimeDTO;
import br.jus.tjma.scelus.dominio.dto.CrimeDetalheDTO;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaCrimes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço responsável pelas regras de negócio e consultas de crimes.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Service
@Transactional(readOnly = true)
public class CrimeService {

    private static final String SQL_COLUNAS =
            """
        SELECT f.int_fato_ocorrido_id AS idFatoOcorrido, \
               p.str_numero_unico AS numeroProcesso, \
               p.int_codigo_assunto AS codigoAssunto, \
               p.str_descricao_assunto AS descricaoAssunto, \
               f.dta_data_fato AS dataFato, \
               f.bol_medida_protetiva AS medidaProtetiva, \
               v_pje.str_nome AS nomeVitima, \
               v_pje.str_cpf_cnpj AS cpfVitima, \
               a_pje.str_nome AS nomeAcusado, \
               a_pje.str_cpf_cnpj AS cpfAcusado, \
               tv.str_tipo_vinculo AS tipoVinculo, \
               (SELECT string_agg(d.str_deficiencia, ', ') \
                  FROM public.tb_litigancia_deficiencia ld \
                  JOIN public.tb_deficiencia d ON d.int_deficiencia_id = ld.int_deficiencia_id \
                 WHERE ld.int_litigancia_id = l_vt.int_litigancia_id) AS deficienciaVitima, \
               CASE WHEN EXISTS (SELECT 1 FROM public.tb_fato_ocorrido_mpu fom \
                                  WHERE fom.int_fato_ocorrido_id = f.int_fato_ocorrido_id) \
                    THEN 'S' ELSE 'N' END AS possuiMpu, \
               (SELECT string_agg(tcv.str_tipo_consequencia_violencia, ', ') \
                  FROM public.tb_consequencia_violencia cv \
                  JOIN public.tb_tipo_consequencia_violencia tcv \
                    ON tcv.int_tipo_consequencia_violencia_id = cv.int_tipo_consequencia_violencia_id \
                 WHERE cv.int_litigancia_id IN (l_vt.int_litigancia_id, l_ac.int_litigancia_id)) \
                    AS consequenciaViolencia\s""";

    private static final String SQL_COLUNAS_DETALHE = SQL_COLUNAS
            + """
        , f.int_vitima_id AS idVitima, \
          f.int_acusado_id AS idAcusado, \
          f.int_processo_crime_id AS idProcessoCrime, \
          f.int_cep_id AS idCep, \
          c.str_cep AS cepFato, \
          vi.int_vinculo_id AS idVinculo, \
          vi.int_tipo_vinculo_id AS idTipoVinculo, \
          vi.str_observacao AS observacaoVinculo\s""";

    private static final String SQL_FROM =
            """
        FROM public.tb_fato_ocorrido f \
        LEFT JOIN public.tb_processo_crime p ON f.int_processo_crime_id = p.int_processo_crime_id \
        LEFT JOIN public.tb_cep c ON f.int_cep_id = c.int_cep_id \
        LEFT JOIN public.tb_vitima vt ON f.int_vitima_id = vt.int_vitima_id \
        LEFT JOIN public.tb_litigancia l_vt ON vt.int_litigancia_id = l_vt.int_litigancia_id \
        LEFT JOIN public.tb_acusado ac ON f.int_acusado_id = ac.int_acusado_id \
        LEFT JOIN public.tb_litigancia l_ac ON ac.int_litigancia_id = l_ac.int_litigancia_id \
        LEFT JOIN public.tb_vinculo vi ON vi.int_vitima_id = vt.int_vitima_id AND vi.int_acusado_id = ac.int_acusado_id \
        LEFT JOIN public.tb_tipo_vinculo tv ON vi.int_tipo_vinculo_id = tv.int_tipo_vinculo_id \
        LEFT JOIN LATERAL pkg_processo.fn_processo_parte_pje_con(l_vt.int_parte_id, p.int_processo_crime_id::integer) v_pje ON true \
        LEFT JOIN LATERAL pkg_processo.fn_processo_parte_pje_con(l_ac.int_parte_id, p.int_processo_crime_id::integer) a_pje ON true \
        WHERE 1=1\s""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CrimeService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Busca o detalhe de um crime/fato ocorrido pelo identificador, com os
     * identificadores brutos necessários à tela de edição.
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @return DTO de detalhe do crime.
     */
    public CrimeDetalheDTO buscarPorId(Long idFatoOcorrido) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idFatoOcorrido", idFatoOcorrido);
        List<CrimeDetalheDTO> resultado = jdbcTemplate.query(
                SQL_COLUNAS_DETALHE + SQL_FROM + "AND f.int_fato_ocorrido_id = :idFatoOcorrido",
                parametros,
                this::mapearDetalhe);

        if (resultado.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Fato ocorrido não encontrado: " + idFatoOcorrido);
        }
        return resultado.get(0);
    }

    /**
     * Realiza a busca paginada de crimes com base nos filtros informados.
     * Prioriza a chamada de funções do banco para obter dados do PJe via dblink.
     *
     * @param filtro DTO contendo os parâmetros de filtro e paginação.
     * @return ResultList contendo a lista de crimes e o total de registros.
     */
    public ResultList<CrimeDTO> consultarCrimes(FiltroConsultaCrimes filtro) {
        StringBuilder sql = new StringBuilder(SQL_COLUNAS + SQL_FROM);

        MapSqlParameterSource parametros = new MapSqlParameterSource();

        if (filtro.getNumeroProcesso() != null && !filtro.getNumeroProcesso().isBlank()) {
            // O campo mascarado no frontend envia só dígitos; str_numero_unico
            // é gravado formatado — compara ignorando a formatação dos dois lados.
            sql.append("AND regexp_replace(p.str_numero_unico, '[^0-9]', '', 'g') "
                    + "= regexp_replace(:numeroProcesso, '[^0-9]', '', 'g') ");
            parametros.addValue("numeroProcesso", filtro.getNumeroProcesso());
        }

        if (filtro.getCodigoAssunto() != null) {
            sql.append("AND p.int_codigo_assunto = :codigoAssunto ");
            parametros.addValue("codigoAssunto", filtro.getCodigoAssunto());
        }

        if (filtro.getDescricaoAssunto() != null
                && !filtro.getDescricaoAssunto().isBlank()) {
            sql.append("AND UPPER(p.str_descricao_assunto) LIKE UPPER(:descricaoAssunto) ");
            parametros.addValue("descricaoAssunto", "%" + filtro.getDescricaoAssunto() + "%");
        }

        if (filtro.getNomeVitima() != null && !filtro.getNomeVitima().isBlank()) {
            sql.append("AND UPPER(v_pje.str_nome) LIKE UPPER(:nomeVitima) ");
            parametros.addValue("nomeVitima", "%" + filtro.getNomeVitima() + "%");
        }

        if (filtro.getCpfVitima() != null && !filtro.getCpfVitima().isBlank()) {
            sql.append("AND v_pje.str_cpf_cnpj = :cpfVitima ");
            parametros.addValue("cpfVitima", filtro.getCpfVitima());
        }

        if (filtro.getNomeAcusado() != null && !filtro.getNomeAcusado().isBlank()) {
            sql.append("AND UPPER(a_pje.str_nome) LIKE UPPER(:nomeAcusado) ");
            parametros.addValue("nomeAcusado", "%" + filtro.getNomeAcusado() + "%");
        }

        if (filtro.getCpfAcusado() != null && !filtro.getCpfAcusado().isBlank()) {
            sql.append("AND a_pje.str_cpf_cnpj = :cpfAcusado ");
            parametros.addValue("cpfAcusado", filtro.getCpfAcusado());
        }

        if (filtro.getTipoVinculo() != null) {
            sql.append("AND vi.int_tipo_vinculo_id = :tipoVinculo ");
            parametros.addValue("tipoVinculo", filtro.getTipoVinculo());
        }

        if (filtro.getDataFato() != null) {
            sql.append("AND f.dta_data_fato::date = :dataFato ");
            parametros.addValue("dataFato", java.sql.Date.valueOf(filtro.getDataFato()));
        }

        if (filtro.getMedidaProtetiva() != null && !filtro.getMedidaProtetiva().isBlank()) {
            sql.append("AND f.bol_medida_protetiva = :medidaProtetiva ");
            parametros.addValue("medidaProtetiva", filtro.getMedidaProtetiva());
        }

        if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
            sql.append("AND (UPPER(v_pje.str_nome) LIKE UPPER(:nome) OR UPPER(a_pje.str_nome) LIKE UPPER(:nome)) ");
            parametros.addValue("nome", "%" + filtro.getNome() + "%");
        }

        aplicarFiltrosAvancados(sql, parametros, filtro);
        aplicarFiltrosCompostos(sql, parametros, filtro);

        // Consulta de contagem total para paginação
        String sqlContagem = "SELECT COUNT(*) FROM (" + sql + ") AS subquery";
        Long total = jdbcTemplate.queryForObject(sqlContagem, parametros, Long.class);
        if (total == null) {
            total = 0L;
        }

        // Ordenação estável — sem isso, a paginação pode repetir/perder registros
        // entre páginas em consultas concorrentes (PostgreSQL não garante ordem
        // implícita sem ORDER BY).
        sql.append("ORDER BY f.int_fato_ocorrido_id DESC ");

        // Aplicação de paginação
        sql.append("LIMIT :limit OFFSET :offset");
        parametros.addValue("limit", filtro.getLimit());
        parametros.addValue("offset", filtro.getOffset());

        List<CrimeDTO> resultado = jdbcTemplate.query(sql.toString(), parametros, this::mapearCrime);

        return new ResultList<>(total, resultado);
    }

    /**
     * Filtros avançados da Tela 1.2 (CSU001/SF01): atributos demográficos das partes
     * (via litigância) e atributos da MPU vinculada ao fato (RN001.01 — AND).
     */
    private void aplicarFiltrosAvancados(
            StringBuilder sql, MapSqlParameterSource parametros, FiltroConsultaCrimes filtro) {

        aplicarFiltrosLitigancia(sql, parametros, filtro, "l_vt", "Vitima");
        aplicarFiltrosLitigancia(sql, parametros, filtro, "l_ac", "Acusado");

        if (filtro.getIdConsequenciaViolencia() != null) {
            sql.append("AND EXISTS (SELECT 1 FROM public.tb_consequencia_violencia cv ")
                    .append("WHERE cv.int_tipo_consequencia_violencia_id = :idConsequenciaViolencia ")
                    .append("AND cv.int_litigancia_id IN (l_vt.int_litigancia_id, l_ac.int_litigancia_id)) ");
            parametros.addValue("idConsequenciaViolencia", filtro.getIdConsequenciaViolencia());
        }

        // Bloco MPU: todas as condições devem valer para a MESMA medida vinculada ao fato
        java.util.List<String> condicoesMpu = new java.util.ArrayList<>();
        if (filtro.getDataDecisaoMpu() != null) {
            condicoesMpu.add("mpu.dta_decisao::date = :dataDecisaoMpu");
            parametros.addValue("dataDecisaoMpu", java.sql.Date.valueOf(filtro.getDataDecisaoMpu()));
        }
        if (filtro.getLegislacaoMpu() != null && !filtro.getLegislacaoMpu().isBlank()) {
            condicoesMpu.add("UPPER(mpu.str_legislacao_fundamento) LIKE UPPER(:legislacaoMpu)");
            parametros.addValue("legislacaoMpu", "%" + filtro.getLegislacaoMpu() + "%");
        }
        if (filtro.getConcedidaMpu() != null && !filtro.getConcedidaMpu().isBlank()) {
            condicoesMpu.add("mpu.bol_concedida = :concedidaMpu");
            parametros.addValue("concedidaMpu", filtro.getConcedidaMpu());
        }
        if (filtro.getDataIntimacaoAcusadoMpu() != null) {
            condicoesMpu.add("mpu.dta_intimacao_acusado::date = :dataIntimacaoAcusadoMpu");
            parametros.addValue("dataIntimacaoAcusadoMpu", java.sql.Date.valueOf(filtro.getDataIntimacaoAcusadoMpu()));
        }
        if (filtro.getDataIntimacaoVitimaMpu() != null) {
            condicoesMpu.add("mpu.dta_intimacao_vitima::date = :dataIntimacaoVitimaMpu");
            parametros.addValue("dataIntimacaoVitimaMpu", java.sql.Date.valueOf(filtro.getDataIntimacaoVitimaMpu()));
        }
        if (filtro.getDataCienciaAcusadoMpu() != null) {
            condicoesMpu.add("mpu.dta_ciencia_acusado::date = :dataCienciaAcusadoMpu");
            parametros.addValue("dataCienciaAcusadoMpu", java.sql.Date.valueOf(filtro.getDataCienciaAcusadoMpu()));
        }
        if (filtro.getDataCienciaVitimaMpu() != null) {
            condicoesMpu.add("mpu.dta_ciencia_vitima::date = :dataCienciaVitimaMpu");
            parametros.addValue("dataCienciaVitimaMpu", java.sql.Date.valueOf(filtro.getDataCienciaVitimaMpu()));
        }
        if (filtro.getPedidoDesistenciaMpu() != null
                && !filtro.getPedidoDesistenciaMpu().isBlank()) {
            condicoesMpu.add("mpu.bol_pedido_desistencia = :pedidoDesistenciaMpu");
            parametros.addValue("pedidoDesistenciaMpu", filtro.getPedidoDesistenciaMpu());
        }
        if (!condicoesMpu.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM public.tb_fato_ocorrido_mpu fom ")
                    .append("JOIN public.tb_medida_protetiva_urgencia mpu ON mpu.int_mpu_id = fom.int_mpu_id ")
                    .append("WHERE fom.int_fato_ocorrido_id = f.int_fato_ocorrido_id AND ")
                    .append(String.join(" AND ", condicoesMpu))
                    .append(") ");
        }
    }

    /**
     * Filtros demográficos de uma parte (vítima ou acusado) sobre a litigância.
     */
    private void aplicarFiltrosLitigancia(
            StringBuilder sql,
            MapSqlParameterSource parametros,
            FiltroConsultaCrimes filtro,
            String alias,
            String sufixo) {
        boolean vitima = "Vitima".equals(sufixo);

        Long idOcupacao = vitima ? filtro.getIdOcupacaoVitima() : filtro.getIdOcupacaoAcusado();
        if (idOcupacao != null) {
            sql.append("AND EXISTS (SELECT 1 FROM public.tb_litigancia_ocupacao lo_")
                    .append(sufixo)
                    .append(" WHERE lo_")
                    .append(sufixo)
                    .append(".int_litigancia_id = ")
                    .append(alias)
                    .append(".int_litigancia_id AND lo_")
                    .append(sufixo)
                    .append(".int_ocupacao_id = :idOcupacao")
                    .append(sufixo)
                    .append(") ");
            parametros.addValue("idOcupacao" + sufixo, idOcupacao);
        }

        Long idDeficiencia = vitima ? filtro.getIdDeficienciaVitima() : filtro.getIdDeficienciaAcusado();
        if (idDeficiencia != null) {
            sql.append("AND EXISTS (SELECT 1 FROM public.tb_litigancia_deficiencia ldf_")
                    .append(sufixo)
                    .append(" WHERE ldf_")
                    .append(sufixo)
                    .append(".int_litigancia_id = ")
                    .append(alias)
                    .append(".int_litigancia_id AND ldf_")
                    .append(sufixo)
                    .append(".int_deficiencia_id = :idDeficiencia")
                    .append(sufixo)
                    .append(") ");
            parametros.addValue("idDeficiencia" + sufixo, idDeficiencia);
        }

        Long idEstadoCivil = vitima ? filtro.getIdEstadoCivilVitima() : filtro.getIdEstadoCivilAcusado();
        if (idEstadoCivil != null) {
            sql.append("AND ")
                    .append(alias)
                    .append(".int_estado_civil_id = :idEstadoCivil")
                    .append(sufixo)
                    .append(" ");
            parametros.addValue("idEstadoCivil" + sufixo, idEstadoCivil);
        }

        Long idReligiao = vitima ? filtro.getIdReligiaoVitima() : filtro.getIdReligiaoAcusado();
        if (idReligiao != null) {
            sql.append("AND ")
                    .append(alias)
                    .append(".int_religiao_id = :idReligiao")
                    .append(sufixo)
                    .append(" ");
            parametros.addValue("idReligiao" + sufixo, idReligiao);
        }

        Long idEscolaridade = vitima ? filtro.getIdEscolaridadeVitima() : filtro.getIdEscolaridadeAcusado();
        if (idEscolaridade != null) {
            sql.append("AND ")
                    .append(alias)
                    .append(".int_escolaridade_id = :idEscolaridade")
                    .append(sufixo)
                    .append(" ");
            parametros.addValue("idEscolaridade" + sufixo, idEscolaridade);
        }

        Long idRenda = vitima ? filtro.getIdRendaVitima() : filtro.getIdRendaAcusado();
        if (idRenda != null) {
            sql.append("AND ")
                    .append(alias)
                    .append(".int_renda_id = :idRenda")
                    .append(sufixo)
                    .append(" ");
            parametros.addValue("idRenda" + sufixo, idRenda);
        }
    }

    /**
     * Filtros avançados compostos da Tela 1.3 (CSU001/SF02): listas de valores por
     * parte, combinados com OR dentro da mesma lista (RN001.02 — IN) e AND entre listas.
     */
    private void aplicarFiltrosCompostos(
            StringBuilder sql, MapSqlParameterSource parametros, FiltroConsultaCrimes filtro) {

        aplicarListaExists(
                sql,
                parametros,
                "l_vt",
                "tb_litigancia_droga",
                "int_droga_id",
                "idsDrogaVitima",
                filtro.getIdsDrogaVitima());
        aplicarListaExists(
                sql,
                parametros,
                "l_ac",
                "tb_litigancia_droga",
                "int_droga_id",
                "idsDrogaAcusado",
                filtro.getIdsDrogaAcusado());
        aplicarListaExists(
                sql,
                parametros,
                "l_vt",
                "tb_litigancia_ocupacao",
                "int_ocupacao_id",
                "idsOcupacaoVitima",
                filtro.getIdsOcupacaoVitima());
        aplicarListaExists(
                sql,
                parametros,
                "l_ac",
                "tb_litigancia_ocupacao",
                "int_ocupacao_id",
                "idsOcupacaoAcusado",
                filtro.getIdsOcupacaoAcusado());
        aplicarListaExists(
                sql,
                parametros,
                "l_vt",
                "tb_litigancia_deficiencia",
                "int_deficiencia_id",
                "idsDeficienciaVitima",
                filtro.getIdsDeficienciaVitima());
        aplicarListaExists(
                sql,
                parametros,
                "l_ac",
                "tb_litigancia_deficiencia",
                "int_deficiencia_id",
                "idsDeficienciaAcusado",
                filtro.getIdsDeficienciaAcusado());
        aplicarListaExists(
                sql,
                parametros,
                "l_vt",
                "tb_beneficio",
                "int_tipo_beneficio_id",
                "idsBeneficioVitima",
                filtro.getIdsBeneficioVitima());
        aplicarListaExists(
                sql,
                parametros,
                "l_ac",
                "tb_beneficio",
                "int_tipo_beneficio_id",
                "idsBeneficioAcusado",
                filtro.getIdsBeneficioAcusado());
        aplicarListaExists(
                sql,
                parametros,
                "l_vt",
                "tb_consequencia_violencia",
                "int_tipo_consequencia_violencia_id",
                "idsConsequenciaViolenciaVitima",
                filtro.getIdsConsequenciaViolenciaVitima());
        aplicarListaExists(
                sql,
                parametros,
                "l_ac",
                "tb_consequencia_violencia",
                "int_tipo_consequencia_violencia_id",
                "idsConsequenciaViolenciaAcusado",
                filtro.getIdsConsequenciaViolenciaAcusado());

        if (filtro.getIdsRacaEtniaVitima() != null
                && !filtro.getIdsRacaEtniaVitima().isEmpty()) {
            sql.append("AND l_vt.int_raca_etnia_id IN (:idsRacaEtniaVitima) ");
            parametros.addValue("idsRacaEtniaVitima", filtro.getIdsRacaEtniaVitima());
        }
        if (filtro.getIdsRacaEtniaAcusado() != null
                && !filtro.getIdsRacaEtniaAcusado().isEmpty()) {
            sql.append("AND l_ac.int_raca_etnia_id IN (:idsRacaEtniaAcusado) ");
            parametros.addValue("idsRacaEtniaAcusado", filtro.getIdsRacaEtniaAcusado());
        }
    }

    /**
     * Condição EXISTS sobre tabela filha da litigância com lista de valores (OR via IN).
     */
    private void aplicarListaExists(
            StringBuilder sql,
            MapSqlParameterSource parametros,
            String aliasLitigancia,
            String tabela,
            String coluna,
            String nomeParametro,
            java.util.List<Long> valores) {
        if (valores == null || valores.isEmpty()) {
            return;
        }
        String aliasFilha = "x_" + nomeParametro;
        sql.append("AND EXISTS (SELECT 1 FROM public.")
                .append(tabela)
                .append(" ")
                .append(aliasFilha)
                .append(" WHERE ")
                .append(aliasFilha)
                .append(".int_litigancia_id = ")
                .append(aliasLitigancia)
                .append(".int_litigancia_id AND ")
                .append(aliasFilha)
                .append(".")
                .append(coluna)
                .append(" IN (:")
                .append(nomeParametro)
                .append(")) ");
        parametros.addValue(nomeParametro, valores);
    }

    private CrimeDTO mapearCrime(ResultSet rs, int rowNum) throws SQLException {
        Timestamp ts = rs.getTimestamp("dataFato");
        return new CrimeDTO(
                rs.getLong("idFatoOcorrido"),
                rs.getString("numeroProcesso"),
                rs.getLong("codigoAssunto"),
                rs.getString("descricaoAssunto"),
                ts != null ? ts.toLocalDateTime() : null,
                rs.getString("medidaProtetiva"),
                rs.getString("nomeVitima"),
                rs.getString("cpfVitima"),
                rs.getString("nomeAcusado"),
                rs.getString("cpfAcusado"),
                rs.getString("tipoVinculo"),
                rs.getString("deficienciaVitima"),
                rs.getString("possuiMpu"),
                rs.getString("consequenciaViolencia"));
    }

    private CrimeDetalheDTO mapearDetalhe(ResultSet rs, int rowNum) throws SQLException {
        Timestamp ts = rs.getTimestamp("dataFato");
        return new CrimeDetalheDTO(
                rs.getLong("idFatoOcorrido"),
                rs.getString("numeroProcesso"),
                rs.getLong("codigoAssunto"),
                ts != null ? ts.toLocalDateTime() : null,
                rs.getString("medidaProtetiva"),
                rs.getString("nomeVitima"),
                rs.getString("cpfVitima"),
                rs.getString("nomeAcusado"),
                rs.getString("cpfAcusado"),
                rs.getString("tipoVinculo"),
                lerLong(rs, "idVitima"),
                lerLong(rs, "idAcusado"),
                lerLong(rs, "idProcessoCrime"),
                lerLong(rs, "idCep"),
                rs.getString("cepFato"),
                lerLong(rs, "idVinculo"),
                lerLong(rs, "idTipoVinculo"),
                rs.getString("observacaoVinculo"));
    }

    private Long lerLong(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }
}
