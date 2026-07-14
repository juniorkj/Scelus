package br.jus.tjma.scelus.dominio.dto;

import java.util.List;

/**
 * javadoc Record de detalhe completo de um crime/fato ocorrido cadastrado pelo
 * wizard do CSU002, reunindo crimes cometidos, vítima, acusado, vínculo, fato
 * ocorrido (com comunicantes) e consequências da violência — usado para
 * carregar as telas de edição e visualização em formato de passos (stepper).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record CrimeCompletoDetalheDTO(
        Long idFatoOcorrido,
        String numeroProcesso,
        List<CrimeCometidoDetalheDTO> crimesCometidos,
        ParteDetalheDTO vitima,
        ParteDetalheDTO acusado,
        VinculoDetalheDTO vinculo,
        FatoOcorridoDetalheDTO fatoOcorrido,
        List<ConsequenciaViolenciaDetalheDTO> consequenciasViolencia) {}
