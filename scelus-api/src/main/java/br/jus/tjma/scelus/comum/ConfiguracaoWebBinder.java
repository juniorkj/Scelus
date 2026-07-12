package br.jus.tjma.scelus.comum;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * javadoc Configuração global do {@code WebDataBinder} da aplicação.
 *
 * <p>Desativa o field marker prefix padrão ("_") do Spring MVC: sem isso, os
 * parâmetros {@code _limit}/{@code _offset} enviados pelo frontend @tjma/angular
 * são interpretados como marcadores de campo vazio para {@code limit}/{@code offset}
 * (int primitivos), gerando HTTP 400 na consulta paginada.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@ControllerAdvice
public class ConfiguracaoWebBinder {

    @InitBinder
    public void configurarBinder(WebDataBinder binder) {
        binder.setFieldMarkerPrefix(null);
    }
}
