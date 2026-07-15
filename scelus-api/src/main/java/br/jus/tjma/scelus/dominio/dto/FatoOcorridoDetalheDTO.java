package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * javadoc Record de detalhe do fato ocorrido (tb_fato_ocorrido) já cadastrado,
 * usado nas telas de edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record FatoOcorridoDetalheDTO(
        Long codigoAssunto,
        LocalDateTime dataFato,
        Long idCep,
        String descricaoCep,
        String medidaProtetiva,
        List<ComunicanteDetalheDTO> comunicantes) {}
