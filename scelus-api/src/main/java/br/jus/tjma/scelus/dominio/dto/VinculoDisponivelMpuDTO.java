package br.jus.tjma.scelus.dominio.dto;

/**
 * javadoc Record de uma litigância (vítima ou acusado) já cadastrada no Scelus
 * para um determinado processo, disponível para vincular a uma MPU cadastrada
 * pela tela avulsa (CSU008) — evita criar MPUs "órfãs", sem vítima/acusado, que
 * ficam invisíveis à busca por par (RN008.02).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 15/07/2026
 */
public record VinculoDisponivelMpuDTO(Long idParte, String polo, Long idVitima, Long idAcusado) {}
