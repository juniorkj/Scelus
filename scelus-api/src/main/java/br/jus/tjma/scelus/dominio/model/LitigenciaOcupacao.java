package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade de associação N:M entre litigância e ocupação (CSU002 — Telas 2.5/2.6).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_litigancia_ocupacao", schema = "public")
public class LitigenciaOcupacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_litigancia_ocupacao_id")
    private Long id;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    @Column(name = "int_ocupacao_id", nullable = false)
    private Long idOcupacao;

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

    public Long getIdOcupacao() {
        return idOcupacao;
    }

    public void setIdOcupacao(Long idOcupacao) {
        this.idOcupacao = idOcupacao;
    }
}
