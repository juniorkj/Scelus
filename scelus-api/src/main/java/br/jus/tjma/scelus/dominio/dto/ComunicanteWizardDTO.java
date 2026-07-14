package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * javadoc Record do comunicante que noticiou o fato ocorrido no wizard do CSU002 (Tela 2.8).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record ComunicanteWizardDTO(
        @NotBlank(message = "O nome do comunicante é obrigatório.") String nome,
        String telefone,
        String email,
        String cpfCnpj,
        @NotNull(message = "O tipo de comunicante é obrigatório.") Long idTipoComunicante,
        LocalDateTime dataDenuncia,
        String observacao,
        Boolean anonimizado) {}
