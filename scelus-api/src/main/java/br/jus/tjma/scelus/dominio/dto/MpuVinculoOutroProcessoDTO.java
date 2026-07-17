package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record que representa uma MPU já vinculada a uma vítima ou acusado em
 * outro processo/fato ocorrido, distinto do que está em edição — usado para
 * alertar no passo "Fato Ocorrido" do CSU002 sobre vínculos preexistentes
 * (RN008.02, exibição de vínculos entre partes e MPUs).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 17/07/2026
 */
public record MpuVinculoOutroProcessoDTO(
        Long idMpu, String numeroMpu, String numeroUnicoProcesso, String concedida, String papel) {}
