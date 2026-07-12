package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de resposta da alteração de crime/fato ocorrido.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record AlteracaoCrimeResponse(Long idFatoOcorrido, String mensagem) {}
