package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record que representa o resultado da pesquisa de crimes.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
public record CrimeDTO(
        Long idFatoOcorrido,
        String numeroProcesso,
        Long codigoAssunto,
        LocalDateTime dataFato,
        String medidaProtetiva,
        String nomeVitima,
        String cpfVitima,
        String nomeAcusado,
        String cpfAcusado,
        String tipoVinculo,
        String deficienciaVitima) {}
