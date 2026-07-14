package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record que representa um assunto vinculado ao processo, retornado pelo PJe
 * (via pkg_processo.fn_processo_assunto_pje_con).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 13/07/2026
 */
public record AssuntoPjeDTO(Integer codigo, String descricao) {}
