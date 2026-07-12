package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade que representa o vínculo entre uma vítima e um acusado.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Entity
@Table(name = "tb_vinculo", schema = "public")
public class Vinculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_vinculo_id")
    private Long id;

    @Column(name = "int_tipo_vinculo_id", nullable = false)
    private Long idTipoVinculo;

    @Column(name = "int_vitima_id", nullable = false)
    private Long idVitima;

    @Column(name = "int_acusado_id", nullable = false)
    private Long idAcusado;

    @Column(name = "str_observacao")
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTipoVinculo() {
        return idTipoVinculo;
    }

    public void setIdTipoVinculo(Long idTipoVinculo) {
        this.idTipoVinculo = idTipoVinculo;
    }

    public Long getIdVitima() {
        return idVitima;
    }

    public void setIdVitima(Long idVitima) {
        this.idVitima = idVitima;
    }

    public Long getIdAcusado() {
        return idAcusado;
    }

    public void setIdAcusado(Long idAcusado) {
        this.idAcusado = idAcusado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
