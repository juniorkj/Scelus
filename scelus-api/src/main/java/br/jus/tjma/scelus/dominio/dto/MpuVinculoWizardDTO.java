package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;

/**
 * javadoc Record de vínculo de MPU no wizard do CSU002/CSU008 (Tela 2.8 → CSU008).
 *
 * <p>Se {@code idMpuExistente} for informado, vincula uma MPU já cadastrada
 * (RN008.04, com justificativa obrigatória — RN008.05). Caso contrário, os dados
 * de {@code novaMpu} são usados para cadastrar uma nova MPU antes do vínculo
 * (RN008.01), também exigindo justificativa.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record MpuVinculoWizardDTO(
        Long idMpuExistente,
        CadastroMpuRequest novaMpu,
        @NotNull(message = "A justificativa de inclusão da MPU é obrigatória.") Long idJustificativaInclusaoMpu,
        String observacaoJustificativa) {}
