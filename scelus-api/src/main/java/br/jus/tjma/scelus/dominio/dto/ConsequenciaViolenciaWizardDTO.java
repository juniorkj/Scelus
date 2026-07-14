package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * javadoc Record de consequência da violência associada a uma parte no wizard do
 * CSU002 (Tela 2.9). {@code chaveParte} referencia a chave de uma vítima ou acusado
 * informado no mesmo payload (ver {@link ParteWizardDTO#chave()}).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record ConsequenciaViolenciaWizardDTO(
        @NotBlank(message = "A chave da parte é obrigatória.") String chaveParte,
        @NotNull(message = "O tipo de consequência da violência é obrigatório.") Long idTipoConsequenciaViolencia,
        @NotBlank(message = "As observações da consequência da violência são obrigatórias.") String observacao) {}
