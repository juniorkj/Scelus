package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record que representa uma MPU vinculada a um fato ocorrido, com a justificativa de inclusão.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record MpuVinculadaDTO(
        Long id,
        Long idMpu,
        String numeroMpu,
        String legislacaoFundamento,
        LocalDateTime dataDecisao,
        String concedida,
        String justificativa,
        String observacaoJustificativa,
        LocalDateTime dataCriacao) {}
