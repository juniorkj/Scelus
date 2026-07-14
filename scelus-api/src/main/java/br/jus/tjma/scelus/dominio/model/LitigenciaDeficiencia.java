package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade de associação N:M entre litigância e deficiência (CSU002 — Telas 2.5/2.6).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_litigancia_deficiencia", schema = "public")
public class LitigenciaDeficiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_litigancia_deficiencia_id")
    private Long id;

    @Column(name = "int_deficiencia_id", nullable = false)
    private Long idDeficiencia;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDeficiencia() {
        return idDeficiencia;
    }

    public void setIdDeficiencia(Long idDeficiencia) {
        this.idDeficiencia = idDeficiencia;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }
}
