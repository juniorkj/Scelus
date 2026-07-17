package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa um processo de crime cadastrado no sistema.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Entity
@Table(name = "tb_processo_crime", schema = "public")
public class ProcessoCrime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_processo_crime_id")
    private Long id;

    @Column(name = "dta_inicio_tipificacao")
    private LocalDateTime dataInicioTipificacao;

    @Column(name = "dta_fim_tipificacao")
    private LocalDateTime dataFimTipificacao;

    @Column(name = "int_codigo_assunto", nullable = false)
    private Long codigoAssunto;

    @Column(name = "str_descricao_assunto")
    private String descricaoAssunto;

    @Column(name = "str_numero_unico", nullable = false)
    private String numeroUnico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataInicioTipificacao() {
        return dataInicioTipificacao;
    }

    public void setDataInicioTipificacao(LocalDateTime dataInicioTipificacao) {
        this.dataInicioTipificacao = dataInicioTipificacao;
    }

    public LocalDateTime getDataFimTipificacao() {
        return dataFimTipificacao;
    }

    public void setDataFimTipificacao(LocalDateTime dataFimTipificacao) {
        this.dataFimTipificacao = dataFimTipificacao;
    }

    public Long getCodigoAssunto() {
        return codigoAssunto;
    }

    public void setCodigoAssunto(Long codigoAssunto) {
        this.codigoAssunto = codigoAssunto;
    }

    public String getNumeroUnico() {
        return numeroUnico;
    }

    public void setNumeroUnico(String numeroUnico) {
        this.numeroUnico = numeroUnico;
    }

    public String getDescricaoAssunto() {
        return descricaoAssunto;
    }

    public void setDescricaoAssunto(String descricaoAssunto) {
        this.descricaoAssunto = descricaoAssunto;
    }
}
