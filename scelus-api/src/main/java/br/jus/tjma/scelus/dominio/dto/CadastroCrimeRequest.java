package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * javadoc Record de entrada para o cadastro de crime do processo (CSU002),
 * importando processo e partes do PJe.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record CadastroCrimeRequest(
        @NotBlank(message = "O número do processo é obrigatório.") String numeroProcesso,
        @NotNull(message = "O código do assunto é obrigatório.") Long codigoAssunto,
        @NotNull(message = "A data do fato é obrigatória.") LocalDateTime dataFato,
        @NotNull(message = "O CEP do local do fato é obrigatório.") Long idCep,
        @NotBlank(message = "Informe se o fato possui medida protetiva (S/N).")
                @Pattern(regexp = "[SN]", message = "Medida protetiva deve ser 'S' ou 'N'.")
                String medidaProtetiva,
        @NotNull(message = "Os dados da vítima são obrigatórios.") @Valid ParteCadastroDTO vitima,
        @NotNull(message = "Os dados do acusado são obrigatórios.") @Valid ParteCadastroDTO acusado,
        Long idTipoVinculo,
        String observacaoVinculo) {}
