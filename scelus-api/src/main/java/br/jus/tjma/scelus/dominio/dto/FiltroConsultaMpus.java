package br.jus.tjma.scelus.dominio.dto;

import br.jus.tjma.scelus.comum.FiltroPesquisaWeb;

/**
 * javadoc DTO que encapsula os filtros para a consulta de medidas protetivas de urgência (MPU).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public class FiltroConsultaMpus extends FiltroPesquisaWeb {

    private String numeroMpu;
    private String numeroUnico;
    private String concedida;

    public String getNumeroMpu() {
        return numeroMpu;
    }

    public void setNumeroMpu(String numeroMpu) {
        this.numeroMpu = numeroMpu;
    }

    public String getNumeroUnico() {
        return numeroUnico;
    }

    public void setNumeroUnico(String numeroUnico) {
        this.numeroUnico = numeroUnico;
    }

    public String getConcedida() {
        return concedida;
    }

    public void setConcedida(String concedida) {
        this.concedida = concedida;
    }
}
