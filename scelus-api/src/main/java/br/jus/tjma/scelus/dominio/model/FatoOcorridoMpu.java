package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa o vínculo entre um fato ocorrido e uma medida protetiva de urgência (MPU).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Entity
@Table(name = "tb_fato_ocorrido_mpu", schema = "public")
public class FatoOcorridoMpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_fato_ocorrido_mpu_id")
    private Long id;

    @Column(name = "int_mpu_id", nullable = false)
    private Long idMpu;

    @Column(name = "int_fato_ocorrido_id", nullable = false)
    private Long idFatoOcorrido;

    @Column(name = "int_justificativa_inclusao_mpu_id", nullable = false)
    private Long idJustificativaInclusaoMpu;

    @Column(name = "str_observacao_justificativa")
    private String observacaoJustificativa;

    @Column(name = "dta_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMpu() {
        return idMpu;
    }

    public void setIdMpu(Long idMpu) {
        this.idMpu = idMpu;
    }

    public Long getIdFatoOcorrido() {
        return idFatoOcorrido;
    }

    public void setIdFatoOcorrido(Long idFatoOcorrido) {
        this.idFatoOcorrido = idFatoOcorrido;
    }

    public Long getIdJustificativaInclusaoMpu() {
        return idJustificativaInclusaoMpu;
    }

    public void setIdJustificativaInclusaoMpu(Long idJustificativaInclusaoMpu) {
        this.idJustificativaInclusaoMpu = idJustificativaInclusaoMpu;
    }

    public String getObservacaoJustificativa() {
        return observacaoJustificativa;
    }

    public void setObservacaoJustificativa(String observacaoJustificativa) {
        this.observacaoJustificativa = observacaoJustificativa;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
