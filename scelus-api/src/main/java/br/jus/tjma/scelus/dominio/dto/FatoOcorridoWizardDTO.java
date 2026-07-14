package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

/**
 * javadoc Record do fato ocorrido no wizard do CSU002 (Tela 2.8), relacionando o
 * crime a uma vítima e um acusado (por chave) e o(s) comunicante(s) do fato.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record FatoOcorridoWizardDTO(
        @NotNull(message = "O crime associado ao fato é obrigatório.") Integer codigoAssunto,
        @NotBlank(message = "A chave da vítima é obrigatória.") String chaveVitima,
        @NotBlank(message = "A chave do acusado é obrigatória.") String chaveAcusado,
        @NotNull(message = "A data do fato é obrigatória.") LocalDateTime dataFato,
        @NotNull(message = "O CEP do local do fato é obrigatório.") Long idCep,
        @NotBlank(message = "Informe se o fato possui medida protetiva (S/N).")
                @Pattern(regexp = "[SN]", message = "Medida protetiva deve ser 'S' ou 'N'.")
                String medidaProtetiva,
        @Valid List<ComunicanteWizardDTO> comunicantes) {}
