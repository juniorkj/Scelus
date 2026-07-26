package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * javadoc Entidade que representa uma medida protetiva de urgência (MPU).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Entity
@Table(name = "tb_medida_protetiva_urgencia", schema = "public")
public class MedidaProtetivaUrgencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_mpu_id")
    private Long id;

    @Column(name = "str_legislacao_fundamento", nullable = false)
    private String legislacaoFundamento;

    @Column(name = "dta_decisao", nullable = false)
    private LocalDateTime dataDecisao;

    @Column(name = "bol_concedida", nullable = false)
    private String concedida;

    @Column(name = "dta_intimacao_acusado", nullable = false)
    private LocalDateTime dataIntimacaoAcusado;

    @Column(name = "dta_intimacao_vitima", nullable = false)
    private LocalDateTime dataIntimacaoVitima;

    @Column(name = "dta_ciencia_vitima")
    private LocalDateTime dataCienciaVitima;

    @Column(name = "dta_ciencia_acusado")
    private LocalDateTime dataCienciaAcusado;

    @Column(name = "bol_pedido_desistencia", nullable = false)
    private String pedidoDesistencia;

    @Column(name = "bol_inquerito_instaurado", nullable = false)
    private String inqueritoInstaurado;

    @Column(name = "str_observacoes")
    private String observacoes;

    @Column(name = "int_acusado_id")
    private Long idAcusado;

    @Column(name = "int_vitima_id")
    private Long idVitima;

    @Column(name = "str_numero_mpu")
    private String numeroMpu;

    @Column(name = "str_numero_unico")
    private String numeroUnico;

    @Column(name = "dta_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "dta_fim_vigencia")
    private LocalDateTime dataFimVigencia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLegislacaoFundamento() {
        return legislacaoFundamento;
    }

    public void setLegislacaoFundamento(String legislacaoFundamento) {
        this.legislacaoFundamento = legislacaoFundamento;
    }

    public LocalDateTime getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDateTime dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public String getConcedida() {
        return concedida;
    }

    public void setConcedida(String concedida) {
        this.concedida = concedida;
    }

    public LocalDateTime getDataIntimacaoAcusado() {
        return dataIntimacaoAcusado;
    }

    public void setDataIntimacaoAcusado(LocalDateTime dataIntimacaoAcusado) {
        this.dataIntimacaoAcusado = dataIntimacaoAcusado;
    }

    public LocalDateTime getDataIntimacaoVitima() {
        return dataIntimacaoVitima;
    }

    public void setDataIntimacaoVitima(LocalDateTime dataIntimacaoVitima) {
        this.dataIntimacaoVitima = dataIntimacaoVitima;
    }

    public LocalDateTime getDataCienciaVitima() {
        return dataCienciaVitima;
    }

    public void setDataCienciaVitima(LocalDateTime dataCienciaVitima) {
        this.dataCienciaVitima = dataCienciaVitima;
    }

    public LocalDateTime getDataCienciaAcusado() {
        return dataCienciaAcusado;
    }

    public void setDataCienciaAcusado(LocalDateTime dataCienciaAcusado) {
        this.dataCienciaAcusado = dataCienciaAcusado;
    }

    public String getPedidoDesistencia() {
        return pedidoDesistencia;
    }

    public void setPedidoDesistencia(String pedidoDesistencia) {
        this.pedidoDesistencia = pedidoDesistencia;
    }

    public String getInqueritoInstaurado() {
        return inqueritoInstaurado;
    }

    public void setInqueritoInstaurado(String inqueritoInstaurado) {
        this.inqueritoInstaurado = inqueritoInstaurado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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

    public String getNumeroMpu() {
        return numeroMpu;
    }

    public void setNumeroMpu(String numeroMpu) {
        this.numeroMpu = numeroMpu;
    }

    public String getNumeroUnico() {
        return numeroUnico;
    }

    public void setNumeroUnico(String numeroUnico) {
        this.numeroUnico = numeroUnico;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(LocalDateTime dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
    }
}
