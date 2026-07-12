package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record de detalhe de um crime/fato ocorrido para a tela de edição,
 * com os identificadores brutos necessários à alteração. O campo {@code id}
 * segue a convenção do TjCrudService (PUT /api/crimes/{id}).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record CrimeDetalheDTO(
        Long id,
        String numeroProcesso,
        Long codigoAssunto,
        LocalDateTime dataFato,
        String medidaProtetiva,
        String nomeVitima,
        String cpfVitima,
        String nomeAcusado,
        String cpfAcusado,
        String tipoVinculo,
        Long idVitima,
        Long idAcusado,
        Long idProcessoCrime,
        Long idCep,
        String cepFato,
        Long idVinculo,
        Long idTipoVinculo,
        String observacaoVinculo) {}
