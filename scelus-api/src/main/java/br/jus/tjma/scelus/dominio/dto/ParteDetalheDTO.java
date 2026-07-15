package br.jus.tjma.scelus.dominio.dto;

import java.util.List;

/**
 * javadoc Record de detalhe do perfil demográfico completo de uma vítima ou
 * acusado já cadastrado (tb_litigancia + associações), usado nas telas de
 * edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record ParteDetalheDTO(
        Long idLitigancia,
        Long idVitima,
        Long idAcusado,
        Long idParte,
        Long idPolo,
        Long idSituacaoUsoDroga,
        Long idEstadoCivil,
        Long idEscolaridade,
        Long idRenda,
        Long idReligiao,
        Long idPosicaoProle,
        Long idRacaEtnia,
        String observacoesPosicaoProle,
        List<Long> idsOcupacao,
        List<Long> idsDeficiencia,
        List<Long> idsDroga,
        Long idEscutaJudicial,
        Long idCep,
        String descricaoCep,
        List<BeneficioDetalheDTO> beneficios,
        List<ConfiguracaoFamiliarDetalheDTO> configuracoesFamiliares,
        Long possuiAntecedentes,
        Long reincidente,
        String observacaoAntecedentes) {}
