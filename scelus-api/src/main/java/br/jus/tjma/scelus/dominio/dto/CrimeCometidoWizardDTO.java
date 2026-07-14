package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * javadoc Record de um crime cometido no wizard do CSU002 (Tela 2.2), vinculando
 * um assunto do processo (PJe) a uma tipificação com data de início e, opcionalmente,
 * data de fim.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record CrimeCometidoWizardDTO(
        @NotNull(message = "O código do assunto é obrigatório.") Integer codigoAssunto,
        String descricaoAssunto,
        @NotNull(message = "A data de início da tipificação é obrigatória.") LocalDateTime dataInicioTipificacao,
        LocalDateTime dataFimTipificacao) {}
