package br.jus.tjma.scelus.dominio.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * javadoc Record de um crime cometido no wizard do CSU002 (Tela 2.2), vinculando
 * um assunto do processo (PJe) a uma tipificação com datas de início/fim opcionais
 * — sem fonte confiável hoje (nem TPU/CNJ nem PJe fornecem essa informação), o
 * usuário pode informá-las manualmente, mas não são mais obrigatórias.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.1
 * @since 14/07/2026
 */
public record CrimeCometidoWizardDTO(
        @NotNull(message = "O código do assunto é obrigatório.") Integer codigoAssunto,
        String descricaoAssunto,
        LocalDateTime dataInicioTipificacao,
        LocalDateTime dataFimTipificacao) {}
