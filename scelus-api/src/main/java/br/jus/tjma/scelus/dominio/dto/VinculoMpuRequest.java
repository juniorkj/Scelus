package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;

/**
 * javadoc Record de entrada para vincular uma MPU a um fato ocorrido (CSU008),
 * com justificativa de inclusão obrigatória.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record VinculoMpuRequest(
        @NotNull(message = "A MPU a ser vinculada é obrigatória.") Long idMpu,
        @NotNull(message = "A justificativa de inclusão da MPU é obrigatória.") Long idJustificativaInclusaoMpu,
        String observacaoJustificativa) {}
