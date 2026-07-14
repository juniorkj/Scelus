package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * javadoc Record de configuração familiar declarada pela vítima no wizard do CSU002 (Tela 2.4).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record ConfiguracaoFamiliarWizardDTO(
        @NotNull(message = "O tipo de configuração familiar é obrigatório.") Long idTipoConfiguracaoFamiliar,
        LocalDateTime dataDeclaracao,
        String observacao) {}
