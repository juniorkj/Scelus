package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de resposta do cadastro completo de crime do processo (CSU002 wizard).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
public record CadastroCrimeCompletoResponse(Long idFatoOcorrido, Long idProcessoCrime, String mensagem) {}
