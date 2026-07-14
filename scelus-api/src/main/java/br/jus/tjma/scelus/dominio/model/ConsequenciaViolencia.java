package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade que representa uma consequência da violência sofrida pela parte
 * (associada à litigância) no cadastro de crimes do processo (CSU002 — Tela 2.9).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_consequencia_violencia", schema = "public")
public class ConsequenciaViolencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_consequencia_violencia_id")
    private Long id;

    @Column(name = "int_tipo_consequencia_violencia_id", nullable = false)
    private Long idTipoConsequenciaViolencia;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    @Column(name = "str_observacao")
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTipoConsequenciaViolencia() {
        return idTipoConsequenciaViolencia;
    }

    public void setIdTipoConsequenciaViolencia(Long idTipoConsequenciaViolencia) {
        this.idTipoConsequenciaViolencia = idTipoConsequenciaViolencia;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
