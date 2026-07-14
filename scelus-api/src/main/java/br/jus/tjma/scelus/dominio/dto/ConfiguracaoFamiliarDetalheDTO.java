package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record de detalhe de uma configuração familiar (tb_configuracao_familiar)
 * já cadastrada para a vítima, usado nas telas de edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record ConfiguracaoFamiliarDetalheDTO(
        Long idConfiguracaoFamiliar,
        Long idTipoConfiguracaoFamiliar,
        LocalDateTime dataDeclaracao,
        String observacao) {}
