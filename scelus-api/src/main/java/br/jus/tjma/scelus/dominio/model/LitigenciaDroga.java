package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade de associação N:M entre litigância e droga utilizada
 * (CSU002 — RN02: não persistir quando "não usuário"/"não especificado").
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_litigancia_droga", schema = "public")
public class LitigenciaDroga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_litigancia_droga_id")
    private Long id;

    @Column(name = "int_droga_id", nullable = false)
    private Long idDroga;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDroga() {
        return idDroga;
    }

    public void setIdDroga(Long idDroga) {
        this.idDroga = idDroga;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }
}
