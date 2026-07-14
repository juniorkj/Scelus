package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de detalhe de uma consequência da violência (tb_consequencia_violencia)
 * já cadastrada, usado nas telas de edição/visualização do CSU002.
 *
 * <p>{@code parte} indica se a consequência está associada à litigância da
 * vítima ou do acusado ("vitima"/"acusado"), já que ambas compartilham o
 * mesmo {@code int_litigancia_id}.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record ConsequenciaViolenciaDetalheDTO(
        Long idConsequenciaViolencia, String parte, Long idTipoConsequenciaViolencia, String observacao) {}
