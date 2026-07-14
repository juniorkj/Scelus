package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa um benefício assistencial percebido pela parte
 * (associado à litigância) no cadastro de crimes do processo (CSU002 — Tela 2.3).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_beneficio", schema = "public")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_beneficio_id")
    private Long id;

    @Column(name = "int_tipo_beneficio_id", nullable = false)
    private Long idTipoBeneficio;

    @Column(name = "dta_data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "int_litigancia_id", nullable = false)
    private Long idLitigancia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTipoBeneficio() {
        return idTipoBeneficio;
    }

    public void setIdTipoBeneficio(Long idTipoBeneficio) {
        this.idTipoBeneficio = idTipoBeneficio;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Long getIdLitigancia() {
        return idLitigancia;
    }

    public void setIdLitigancia(Long idLitigancia) {
        this.idLitigancia = idLitigancia;
    }
}
