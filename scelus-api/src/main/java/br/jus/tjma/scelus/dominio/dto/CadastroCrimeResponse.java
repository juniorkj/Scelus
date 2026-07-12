package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de resposta do cadastro de crime, com os identificadores gerados.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record CadastroCrimeResponse(
        Long idFatoOcorrido, Long idProcessoCrime, Long idVitima, Long idAcusado, String mensagem) {}
