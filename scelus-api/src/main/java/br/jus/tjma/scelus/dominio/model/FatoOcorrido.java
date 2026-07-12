package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa um fato ocorrido associado a um processo.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Entity
@Table(name = "tb_fato_ocorrido", schema = "public")
public class FatoOcorrido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_fato_ocorrido_id")
    private Long id;

    @Column(name = "int_acusado_id", nullable = false)
    private Long idAcusado;

    @Column(name = "int_vitima_id", nullable = false)
    private Long idVitima;

    @Column(name = "dta_data_fato", nullable = false)
    private LocalDateTime dataFato;

    @Column(name = "int_cep_id", nullable = false)
    private Long idCep;

    @Column(name = "bol_medida_protetiva", nullable = false)
    private String medidaProtetiva;

    @Column(name = "int_processo_crime_id")
    private Long idProcessoCrime;

    @Column(name = "dta_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAcusado() {
        return idAcusado;
    }

    public void setIdAcusado(Long idAcusado) {
        this.idAcusado = idAcusado;
    }

    public Long getIdVitima() {
        return idVitima;
    }

    public void setIdVitima(Long idVitima) {
        this.idVitima = idVitima;
    }

    public LocalDateTime getDataFato() {
        return dataFato;
    }

    public void setDataFato(LocalDateTime dataFato) {
        this.dataFato = dataFato;
    }

    public Long getIdCep() {
        return idCep;
    }

    public void setIdCep(Long idCep) {
        this.idCep = idCep;
    }

    public String getMedidaProtetiva() {
        return medidaProtetiva;
    }

    public void setMedidaProtetiva(String medidaProtetiva) {
        this.medidaProtetiva = medidaProtetiva;
    }

    public Long getIdProcessoCrime() {
        return idProcessoCrime;
    }

    public void setIdProcessoCrime(Long idProcessoCrime) {
        this.idProcessoCrime = idProcessoCrime;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
