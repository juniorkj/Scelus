package br.jus.tjma.scelus.dominio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * javadoc Record que representa a resposta da API pública ViaCEP (viacep.com.br).
 * O campo {@code erro} vem true quando o CEP consultado não existe.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResposta(
        String cep, String logradouro, String bairro, String localidade, String uf, Boolean erro) {}
