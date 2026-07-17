package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * javadoc Record com o perfil demográfico completo de uma vítima ou acusado no
 * wizard do CSU002 (Telas 2.5/2.6), incluindo associações múltiplas (ocupações,
 * deficiências, drogas) e — para vítimas — benefícios e configuração familiar.
 * Escuta judicial é tratada em CSU separado.
 *
 * <p>{@code chave} é um identificador atribuído pelo frontend (ex.: "V1", "A1")
 * para permitir que outros blocos do payload (vínculo, fato ocorrido,
 * consequências da violência) referenciem esta parte antes de sua litigância
 * ser criada no banco.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record ParteWizardDTO(
        @NotBlank(message = "A chave da parte é obrigatória.") String chave,
        @NotNull(message = "O identificador da parte (PJe) é obrigatório.") Long idParte,
        @NotNull(message = "O polo processual é obrigatório.") Long idPolo,

        // Perfil demográfico (tb_litigancia)
        Long idSituacaoUsoDroga,
        Long idEstadoCivil,
        Long idEscolaridade,
        Long idRenda,
        Long idReligiao,
        Long idPosicaoProle,
        Long idRacaEtnia,
        String observacoesPosicaoProle,

        // Associações múltiplas (N:M sobre a litigância)
        List<Long> idsOcupacao,
        List<Long> idsDeficiencia,
        List<Long> idsDroga,

        // Exclusivos da vítima
        Long idCep,
        @Valid List<BeneficioWizardDTO> beneficios,
        @Valid List<ConfiguracaoFamiliarWizardDTO> configuracoesFamiliares,

        // Exclusivos do acusado
        Long possuiAntecedentes,
        Long reincidente,
        String observacaoAntecedentes) {}
