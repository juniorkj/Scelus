package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record que representa um CEP/endereço para pesquisa e seleção.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record CepDTO(Long id, String cep, String logradouro, String bairro, String municipio, String uf) {}
