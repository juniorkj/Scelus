package br.jus.tjma.scelus.dominio.dto;

import java.util.List;

/**
 * javadoc Record que representa um processo retornado pelo PJe (via pkg_processo.fn_processo_pje_con)
 * com suas respectivas partes.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record ProcessoPjeDTO(Integer idProcesso, String numeroUnico, String possuiSentenca, List<PartePjeDTO> partes) {}
