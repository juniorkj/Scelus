package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade de domínio que representa o tipo de consequência da violência
 * (CSU002 — Tela 2.9).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_tipo_consequencia_violencia", schema = "public")
public class TipoConsequenciaViolencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_tipo_consequencia_violencia_id")
    private Long id;

    @Column(name = "str_tipo_consequencia_violencia")
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
