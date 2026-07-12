package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * javadoc Record de entrada para o cadastro de medida protetiva de urgência (CSU008),
 * gravada via pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record CadastroMpuRequest(
        @NotBlank(message = "A legislação/fundamento é obrigatória.") String legislacaoFundamento,
        @NotNull(message = "A data da decisão é obrigatória.") LocalDateTime dataDecisao,
        @NotBlank(message = "Informe se a medida foi concedida (S/N).")
                @Pattern(regexp = "[SN]", message = "Concedida deve ser 'S' ou 'N'.")
                String concedida,
        @NotNull(message = "A data de intimação do acusado é obrigatória.") LocalDateTime dataIntimacaoAcusado,
        @NotNull(message = "A data de intimação da vítima é obrigatória.") LocalDateTime dataIntimacaoVitima,
        LocalDateTime dataCienciaVitima,
        LocalDateTime dataCienciaAcusado,
        @NotBlank(message = "Informe se há pedido de desistência (S/N).")
                @Pattern(regexp = "[SN]", message = "Pedido de desistência deve ser 'S' ou 'N'.")
                String pedidoDesistencia,
        @NotBlank(message = "Informe se há inquérito instaurado (S/N).")
                @Pattern(regexp = "[SN]", message = "Inquérito instaurado deve ser 'S' ou 'N'.")
                String inqueritoInstaurado,
        String observacoes,
        Long idAcusado,
        Long idVitima,
        String numeroMpu,
        String numeroUnico) {}
