package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * javadoc Record de entrada do wizard completo de cadastro de crimes do processo
 * (CSU002), com persistência diferida ao final (RN01) — inclui vítimas, acusados,
 * vínculos, fato ocorrido, comunicantes, MPUs e consequências da violência.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record CadastroCrimeCompletoRequest(
        @NotBlank(message = "O número do processo é obrigatório.") String numeroProcesso,
        @NotEmpty(message = "Ao menos um crime cometido é obrigatório.") @Valid
                List<CrimeCometidoWizardDTO> crimesCometidos,
        @NotEmpty(message = "Ao menos uma vítima é obrigatória.") @Valid List<ParteWizardDTO> vitimas,
        @NotEmpty(message = "Ao menos um acusado é obrigatório.") @Valid List<ParteWizardDTO> acusados,
        @Valid List<VinculoWizardDTO> vinculos,
        @NotNull(message = "Os dados do fato ocorrido são obrigatórios.") @Valid FatoOcorridoWizardDTO fatoOcorrido,
        @Valid List<MpuVinculoWizardDTO> mpus,
        @Valid List<ConsequenciaViolenciaWizardDTO> consequenciasViolencia) {}
