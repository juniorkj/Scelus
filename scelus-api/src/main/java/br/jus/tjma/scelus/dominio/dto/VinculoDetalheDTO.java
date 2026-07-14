package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de detalhe do vínculo entre vítima e acusado (tb_vinculo) já
 * cadastrado, usado nas telas de edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record VinculoDetalheDTO(Long idVinculo, Long idTipoVinculo, String observacao) {}
