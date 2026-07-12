package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.dominio.dto.PartePjeDTO;
import br.jus.tjma.scelus.dominio.dto.ProcessoPjeDTO;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço de consulta de processos e partes na base do PJe,
 * priorizando as funções do banco (pkg_processo) que acessam o PJe via dblink.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Service
@Transactional(readOnly = true)
public class ProcessoPjeService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ProcessoPjeService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Consulta um processo no PJe pelo número único formatado (CNJ) e retorna
     * seus dados básicos com a lista de partes.
     *
     * @param numeroProcesso Número único formatado do processo.
     * @return DTO do processo com as partes associadas.
     */
    public ProcessoPjeDTO consultarPorNumero(String numeroProcesso) {
        String numeroFormatado = numeroProcesso;
        if (numeroProcesso != null) {
            String apenasDigitos = numeroProcesso.replaceAll("\\D", "");
            if (apenasDigitos.length() == 20) {
                numeroFormatado = apenasDigitos.substring(0, 7) + "-" + apenasDigitos.substring(7, 9)
                        + "." + apenasDigitos.substring(9, 13)
                        + "." + apenasDigitos.substring(13, 14)
                        + "." + apenasDigitos.substring(14, 16)
                        + "." + apenasDigitos.substring(16, 20);
            }
        }

        MapSqlParameterSource parametros = new MapSqlParameterSource("numeroProcesso", numeroFormatado);

        List<ProcessoPjeDTO> processos = jdbcTemplate.query(
                "SELECT int_processo_id, str_numero_unico_formatado, bol_sentenca "
                        + "FROM pkg_processo.fn_processo_pje_con(:numeroProcesso)",
                parametros,
                (rs, rowNum) -> new ProcessoPjeDTO(
                        rs.getInt("int_processo_id"),
                        rs.getString("str_numero_unico_formatado"),
                        rs.getString("bol_sentenca"),
                        null));

        if (processos.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Processo não localizado no PJe: " + numeroProcesso);
        }

        ProcessoPjeDTO processo = processos.get(0);
        List<PartePjeDTO> partes = consultarPartes(processo.idProcesso());

        return new ProcessoPjeDTO(processo.idProcesso(), processo.numeroUnico(), processo.possuiSentenca(), partes);
    }

    /**
     * Consulta as partes de um processo do PJe.
     *
     * @param idProcesso Identificador do processo no PJe.
     * @return Lista de partes com polo, nome, CPF/CNPJ e dados pessoais.
     */
    public List<PartePjeDTO> consultarPartes(Integer idProcesso) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idProcesso", idProcesso);

        return jdbcTemplate.query(
                "SELECT int_parte_id, int_processo_id, str_polo, str_nome, str_nome_social, "
                        + "str_cpf_cnpj, dta_nacimento, str_genero "
                        + "FROM pkg_processo.fn_processo_parte_pje_con(NULL, :idProcesso)",
                parametros,
                (rs, rowNum) -> new PartePjeDTO(
                        rs.getLong("int_parte_id"),
                        rs.getInt("int_processo_id"),
                        rs.getString("str_polo"),
                        rs.getString("str_nome"),
                        rs.getString("str_nome_social"),
                        rs.getString("str_cpf_cnpj"),
                        rs.getString("dta_nacimento"),
                        rs.getString("str_genero")));
    }
}
