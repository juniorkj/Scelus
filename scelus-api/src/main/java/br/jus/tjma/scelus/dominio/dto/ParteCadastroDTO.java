package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;

/**
 * javadoc Record com os dados de uma parte (vítima ou acusado) para o cadastro de crime.
 * A litigância é gravada via pkg_litigancia.fn_litigancia_ins; os campos demográficos são opcionais.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record ParteCadastroDTO(
        @NotNull(message = "O identificador da parte (PJe) é obrigatório.") Long idParte,
        @NotNull(message = "O polo processual é obrigatório.") Long idPolo,
        Long idSituacaoUsoDroga,
        Long idEstadoCivil,
        Long idEscolaridade,
        Long idRenda,
        Long idReligiao,
        Long idPosicaoProle,
        Long idRacaEtnia,
        String observacoesPosicaoProle,

        /** CEP de residência — obrigatório apenas para a vítima. */
        Long idCep,

        /** Campos exclusivos do acusado (1 = sim, 0 = não). */
        Long possuiAntecedentes,
        Long reincidente,
        String observacaoAntecedentes,
        Long idOcupacao) {}
