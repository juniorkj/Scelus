package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * javadoc Record de entrada para a alteração de um crime/fato ocorrido,
 * gravada via pkg_fato_ocorrido.fn_fato_ocorrido_upd.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record AlteracaoCrimeRequest(
        @NotNull(message = "A data do fato é obrigatória.") LocalDateTime dataFato,
        @NotNull(message = "O CEP do local do fato é obrigatório.") Long idCep,
        @NotBlank(message = "Informe se o fato possui medida protetiva (S/N).")
                @Pattern(regexp = "[SN]", message = "Medida protetiva deve ser 'S' ou 'N'.")
                String medidaProtetiva,
        Long idTipoVinculo,
        String observacaoVinculo) {}
