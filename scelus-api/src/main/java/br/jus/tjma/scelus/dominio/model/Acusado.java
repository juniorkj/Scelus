package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade que representa um acusado cadastrado no sistema.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Entity
@Table(name = "tb_acusado", schema = "public")
public class Acusado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_acusado_id")
    private Long id;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    @Column(name = "bol_possui_antecedentes")
    private Long possuiAntecedentes;

    @Column(name = "bol_reincidente")
    private Long reincidente;

    @Column(name = "str_observacao_antecedentes")
    private String observacaoAntecedentes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }

    public Long getPossuiAntecedentes() {
        return possuiAntecedentes;
    }

    public void setPossuiAntecedentes(Long possuiAntecedentes) {
        this.possuiAntecedentes = possuiAntecedentes;
    }

    public Long getReincidente() {
        return reincidente;
    }

    public void setReincidente(Long reincidente) {
        this.reincidente = reincidente;
    }

    public String getObservacaoAntecedentes() {
        return observacaoAntecedentes;
    }

    public void setObservacaoAntecedentes(String observacaoAntecedentes) {
        this.observacaoAntecedentes = observacaoAntecedentes;
    }
}
