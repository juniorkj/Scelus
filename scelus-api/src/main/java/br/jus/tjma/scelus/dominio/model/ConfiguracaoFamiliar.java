package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa a configuração familiar declarada pela vítima
 * no cadastro de crimes do processo (CSU002 — Tela 2.4).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_configuracao_familiar", schema = "public")
public class ConfiguracaoFamiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_configuracao_familiar_id")
    private Long id;

    @Column(name = "dta_declaracao")
    private LocalDateTime dataDeclaracao;

    @Column(name = "str_observacao")
    private String observacao;

    @Column(name = "int_tipo_configuracao_familiar_id", nullable = false)
    private Long idTipoConfiguracaoFamiliar;

    @Column(name = "int_vitima_id", nullable = false)
    private Long idVitima;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataDeclaracao() {
        return dataDeclaracao;
    }

    public void setDataDeclaracao(LocalDateTime dataDeclaracao) {
        this.dataDeclaracao = dataDeclaracao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getIdTipoConfiguracaoFamiliar() {
        return idTipoConfiguracaoFamiliar;
    }

    public void setIdTipoConfiguracaoFamiliar(Long idTipoConfiguracaoFamiliar) {
        this.idTipoConfiguracaoFamiliar = idTipoConfiguracaoFamiliar;
    }

    public Long getIdVitima() {
        return idVitima;
    }

    public void setIdVitima(Long idVitima) {
        this.idVitima = idVitima;
    }
}
