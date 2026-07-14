package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que associa um comunicante (e seu tipo) ao fato ocorrido
 * (CSU002 — Tela 2.8).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_fato_ocorrido_comunicante", schema = "public")
public class FatoOcorridoComunicante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_fato_ocorrido_comunicante_id")
    private Long id;

    @Column(name = "dta_denuncia")
    private LocalDateTime dataDenuncia;

    @Column(name = "str_observacao")
    private String observacao;

    @Column(name = "bol_anonimizado")
    private Long anonimizado;

    @Column(name = "int_comunicante_id", nullable = false)
    private Long idComunicante;

    @Column(name = "int_tipo_comunicante_id", nullable = false)
    private Long idTipoComunicante;

    @Column(name = "int_fato_ocorrido_id", nullable = false)
    private Long idFatoOcorrido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataDenuncia() {
        return dataDenuncia;
    }

    public void setDataDenuncia(LocalDateTime dataDenuncia) {
        this.dataDenuncia = dataDenuncia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getAnonimizado() {
        return anonimizado;
    }

    public void setAnonimizado(Long anonimizado) {
        this.anonimizado = anonimizado;
    }

    public Long getIdComunicante() {
        return idComunicante;
    }

    public void setIdComunicante(Long idComunicante) {
        this.idComunicante = idComunicante;
    }

    public Long getIdTipoComunicante() {
        return idTipoComunicante;
    }

    public void setIdTipoComunicante(Long idTipoComunicante) {
        this.idTipoComunicante = idTipoComunicante;
    }

    public Long getIdFatoOcorrido() {
        return idFatoOcorrido;
    }

    public void setIdFatoOcorrido(Long idFatoOcorrido) {
        this.idFatoOcorrido = idFatoOcorrido;
    }
}
