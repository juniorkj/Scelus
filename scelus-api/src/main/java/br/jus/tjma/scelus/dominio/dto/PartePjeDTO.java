package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record que representa uma parte processual retornada pelo PJe (via pkg_processo.fn_processo_parte_pje_con).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record PartePjeDTO(
        Long idParte,
        Integer idProcesso,
        String polo,
        String nome,
        String nomeSocial,
        String cpfCnpj,
        String dataNascimento,
        String genero) {}
