package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record de detalhe de um crime cometido (tb_processo_crime) já cadastrado,
 * usado nas telas de edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record CrimeCometidoDetalheDTO(
        Long idProcessoCrime,
        Long codigoAssunto,
        String descricaoAssunto,
        LocalDateTime dataInicioTipificacao,
        LocalDateTime dataFimTipificacao) {}
