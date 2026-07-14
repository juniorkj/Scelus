package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade de associação N:M entre litigância e escuta judicial
 * (CSU002 — Telas 2.5/2.6, uma escuta por vez).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_litigancia_escuta_judicial", schema = "public")
public class LitigenciaEscutaJudicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_litigancia_escuta_judicial_id")
    private Long id;

    @Column(name = "int_escuta_judicial_id", nullable = false)
    private Long idEscutaJudicial;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEscutaJudicial() {
        return idEscutaJudicial;
    }

    public void setIdEscutaJudicial(Long idEscutaJudicial) {
        this.idEscutaJudicial = idEscutaJudicial;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }
}
