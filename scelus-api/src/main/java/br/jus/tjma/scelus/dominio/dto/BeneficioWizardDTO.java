package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * javadoc Record de benefício assistencial associado a uma parte no wizard do CSU002 (Tela 2.3).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record BeneficioWizardDTO(
        @NotNull(message = "O tipo de benefício é obrigatório.") Long idTipoBeneficio, LocalDateTime dataInicio) {}
