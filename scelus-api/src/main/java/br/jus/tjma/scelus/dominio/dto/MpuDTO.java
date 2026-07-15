package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record que representa uma medida protetiva de urgência (MPU) na listagem/seleção/edição.
 * O campo {@code id} segue a convenção do componente seletor da @tjma/angular.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.1
 * @since 11/07/2026
 */
public record MpuDTO(
        Long id,
        String numeroMpu,
        String numeroUnico,
        String legislacaoFundamento,
        LocalDateTime dataDecisao,
        String concedida,
        LocalDateTime dataIntimacaoAcusado,
        LocalDateTime dataIntimacaoVitima,
        LocalDateTime dataCienciaVitima,
        LocalDateTime dataCienciaAcusado,
        String pedidoDesistencia,
        String inqueritoInstaurado,
        String observacoes,
        Long idVitima,
        Long idAcusado) {}
