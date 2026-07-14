package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * javadoc Record de vínculo entre vítima e acusado no wizard do CSU002 (Tela 2.7).
 * {@code chaveVitima}/{@code chaveAcusado} referenciam as chaves informadas em
 * {@link ParteWizardDTO#chave()} no mesmo payload.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record VinculoWizardDTO(
        @NotBlank(message = "A chave da vítima é obrigatória.") String chaveVitima,
        @NotBlank(message = "A chave do acusado é obrigatória.") String chaveAcusado,
        @NotNull(message = "O tipo de vínculo é obrigatório.") Long idTipoVinculo,
        String observacao) {}
