package br.jus.tjma.scelus.dominio.dto;

import java.time.LocalDateTime;

/**
 * javadoc Record de detalhe de um comunicante do fato ocorrido (tb_comunicante +
 * tb_fato_ocorrido_comunicante) já cadastrado, usado nas telas de
 * edição/visualização do CSU002.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 14/07/2026
 */
public record ComunicanteDetalheDTO(
        Long idComunicante,
        Long idFatoOcorridoComunicante,
        String nome,
        String telefone,
        String email,
        String cpfCnpj,
        Long idTipoComunicante,
        LocalDateTime dataDenuncia,
        String observacao,
        Boolean anonimizado) {}
