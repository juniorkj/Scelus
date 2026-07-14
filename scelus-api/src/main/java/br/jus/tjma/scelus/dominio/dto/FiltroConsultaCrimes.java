package br.jus.tjma.scelus.dominio.dto;

import br.jus.tjma.scelus.comum.FiltroPesquisaWeb;
import java.time.LocalDate;
import java.util.List;

/**
 * javadoc DTO que encapsula os filtros para a consulta de crimes do processo (CSU001):
 * filtros simples (Tela 1.1), avançados (Tela 1.2) e avançados compostos (Tela 1.3).
 *
 * <p>Regras: RN001.01 — filtros combinados com AND; RN001.02 — valores de um mesmo
 * filtro composto (listas ids*) combinados com OR (IN).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 2.0
 * @since 10/07/2026
 */
public class FiltroConsultaCrimes extends FiltroPesquisaWeb {

    // ── Filtros simples (Tela 1.1) ──
    private String numeroProcesso;
    private Long codigoAssunto;
    private String nomeVitima;
    private String cpfVitima;
    private String nomeAcusado;
    private String cpfAcusado;
    private Long tipoVinculo;
    private Long idConsequenciaViolencia;
    private LocalDate dataFato;
    private String medidaProtetiva;

    // ── Filtros avançados — Vítima (Tela 1.2) ──
    private Long idOcupacaoVitima;
    private Long idDeficienciaVitima;
    private Long idEstadoCivilVitima;
    private Long idReligiaoVitima;
    private Long idEscolaridadeVitima;
    private Long idRendaVitima;

    // ── Filtros avançados — Acusado (Tela 1.2) ──
    private Long idOcupacaoAcusado;
    private Long idDeficienciaAcusado;
    private Long idEstadoCivilAcusado;
    private Long idReligiaoAcusado;
    private Long idEscolaridadeAcusado;
    private Long idRendaAcusado;

    // ── Filtros avançados — MPU (Tela 1.2) ──
    private LocalDate dataDecisaoMpu;
    private String legislacaoMpu;
    private String concedidaMpu;
    private LocalDate dataIntimacaoAcusadoMpu;
    private LocalDate dataIntimacaoVitimaMpu;
    private LocalDate dataCienciaAcusadoMpu;
    private LocalDate dataCienciaVitimaMpu;
    private String pedidoDesistenciaMpu;

    // ── Filtros compostos — Vítima (Tela 1.3, OR dentro da lista) ──
    private List<Long> idsDrogaVitima;
    private List<Long> idsOcupacaoVitima;
    private List<Long> idsDeficienciaVitima;
    private List<Long> idsRacaEtniaVitima;
    private List<Long> idsBeneficioVitima;
    private List<Long> idsConsequenciaViolenciaVitima;

    // ── Filtros compostos — Acusado (Tela 1.3, OR dentro da lista) ──
    private List<Long> idsDrogaAcusado;
    private List<Long> idsOcupacaoAcusado;
    private List<Long> idsDeficienciaAcusado;
    private List<Long> idsRacaEtniaAcusado;
    private List<Long> idsBeneficioAcusado;
    private List<Long> idsConsequenciaViolenciaAcusado;

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String v) {
        this.numeroProcesso = v;
    }

    public Long getCodigoAssunto() {
        return codigoAssunto;
    }

    public void setCodigoAssunto(Long v) {
        this.codigoAssunto = v;
    }

    public String getNomeVitima() {
        return nomeVitima;
    }

    public void setNomeVitima(String v) {
        this.nomeVitima = v;
    }

    public String getCpfVitima() {
        return cpfVitima;
    }

    public void setCpfVitima(String v) {
        this.cpfVitima = v;
    }

    public String getNomeAcusado() {
        return nomeAcusado;
    }

    public void setNomeAcusado(String v) {
        this.nomeAcusado = v;
    }

    public String getCpfAcusado() {
        return cpfAcusado;
    }

    public void setCpfAcusado(String v) {
        this.cpfAcusado = v;
    }

    public Long getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(Long v) {
        this.tipoVinculo = v;
    }

    public Long getIdConsequenciaViolencia() {
        return idConsequenciaViolencia;
    }

    public void setIdConsequenciaViolencia(Long v) {
        this.idConsequenciaViolencia = v;
    }

    public LocalDate getDataFato() {
        return dataFato;
    }

    public void setDataFato(LocalDate v) {
        this.dataFato = v;
    }

    public String getMedidaProtetiva() {
        return medidaProtetiva;
    }

    public void setMedidaProtetiva(String v) {
        this.medidaProtetiva = v;
    }

    public Long getIdOcupacaoVitima() {
        return idOcupacaoVitima;
    }

    public void setIdOcupacaoVitima(Long v) {
        this.idOcupacaoVitima = v;
    }

    public Long getIdDeficienciaVitima() {
        return idDeficienciaVitima;
    }

    public void setIdDeficienciaVitima(Long v) {
        this.idDeficienciaVitima = v;
    }

    public Long getIdEstadoCivilVitima() {
        return idEstadoCivilVitima;
    }

    public void setIdEstadoCivilVitima(Long v) {
        this.idEstadoCivilVitima = v;
    }

    public Long getIdReligiaoVitima() {
        return idReligiaoVitima;
    }

    public void setIdReligiaoVitima(Long v) {
        this.idReligiaoVitima = v;
    }

    public Long getIdEscolaridadeVitima() {
        return idEscolaridadeVitima;
    }

    public void setIdEscolaridadeVitima(Long v) {
        this.idEscolaridadeVitima = v;
    }

    public Long getIdRendaVitima() {
        return idRendaVitima;
    }

    public void setIdRendaVitima(Long v) {
        this.idRendaVitima = v;
    }

    public Long getIdOcupacaoAcusado() {
        return idOcupacaoAcusado;
    }

    public void setIdOcupacaoAcusado(Long v) {
        this.idOcupacaoAcusado = v;
    }

    public Long getIdDeficienciaAcusado() {
        return idDeficienciaAcusado;
    }

    public void setIdDeficienciaAcusado(Long v) {
        this.idDeficienciaAcusado = v;
    }

    public Long getIdEstadoCivilAcusado() {
        return idEstadoCivilAcusado;
    }

    public void setIdEstadoCivilAcusado(Long v) {
        this.idEstadoCivilAcusado = v;
    }

    public Long getIdReligiaoAcusado() {
        return idReligiaoAcusado;
    }

    public void setIdReligiaoAcusado(Long v) {
        this.idReligiaoAcusado = v;
    }

    public Long getIdEscolaridadeAcusado() {
        return idEscolaridadeAcusado;
    }

    public void setIdEscolaridadeAcusado(Long v) {
        this.idEscolaridadeAcusado = v;
    }

    public Long getIdRendaAcusado() {
        return idRendaAcusado;
    }

    public void setIdRendaAcusado(Long v) {
        this.idRendaAcusado = v;
    }

    public LocalDate getDataDecisaoMpu() {
        return dataDecisaoMpu;
    }

    public void setDataDecisaoMpu(LocalDate v) {
        this.dataDecisaoMpu = v;
    }

    public String getLegislacaoMpu() {
        return legislacaoMpu;
    }

    public void setLegislacaoMpu(String v) {
        this.legislacaoMpu = v;
    }

    public String getConcedidaMpu() {
        return concedidaMpu;
    }

    public void setConcedidaMpu(String v) {
        this.concedidaMpu = v;
    }

    public LocalDate getDataIntimacaoAcusadoMpu() {
        return dataIntimacaoAcusadoMpu;
    }

    public void setDataIntimacaoAcusadoMpu(LocalDate v) {
        this.dataIntimacaoAcusadoMpu = v;
    }

    public LocalDate getDataIntimacaoVitimaMpu() {
        return dataIntimacaoVitimaMpu;
    }

    public void setDataIntimacaoVitimaMpu(LocalDate v) {
        this.dataIntimacaoVitimaMpu = v;
    }

    public LocalDate getDataCienciaAcusadoMpu() {
        return dataCienciaAcusadoMpu;
    }

    public void setDataCienciaAcusadoMpu(LocalDate v) {
        this.dataCienciaAcusadoMpu = v;
    }

    public LocalDate getDataCienciaVitimaMpu() {
        return dataCienciaVitimaMpu;
    }

    public void setDataCienciaVitimaMpu(LocalDate v) {
        this.dataCienciaVitimaMpu = v;
    }

    public String getPedidoDesistenciaMpu() {
        return pedidoDesistenciaMpu;
    }

    public void setPedidoDesistenciaMpu(String v) {
        this.pedidoDesistenciaMpu = v;
    }

    public List<Long> getIdsDrogaVitima() {
        return idsDrogaVitima;
    }

    public void setIdsDrogaVitima(List<Long> v) {
        this.idsDrogaVitima = v;
    }

    public List<Long> getIdsOcupacaoVitima() {
        return idsOcupacaoVitima;
    }

    public void setIdsOcupacaoVitima(List<Long> v) {
        this.idsOcupacaoVitima = v;
    }

    public List<Long> getIdsDeficienciaVitima() {
        return idsDeficienciaVitima;
    }

    public void setIdsDeficienciaVitima(List<Long> v) {
        this.idsDeficienciaVitima = v;
    }

    public List<Long> getIdsRacaEtniaVitima() {
        return idsRacaEtniaVitima;
    }

    public void setIdsRacaEtniaVitima(List<Long> v) {
        this.idsRacaEtniaVitima = v;
    }

    public List<Long> getIdsBeneficioVitima() {
        return idsBeneficioVitima;
    }

    public void setIdsBeneficioVitima(List<Long> v) {
        this.idsBeneficioVitima = v;
    }

    public List<Long> getIdsConsequenciaViolenciaVitima() {
        return idsConsequenciaViolenciaVitima;
    }

    public void setIdsConsequenciaViolenciaVitima(List<Long> v) {
        this.idsConsequenciaViolenciaVitima = v;
    }

    public List<Long> getIdsDrogaAcusado() {
        return idsDrogaAcusado;
    }

    public void setIdsDrogaAcusado(List<Long> v) {
        this.idsDrogaAcusado = v;
    }

    public List<Long> getIdsOcupacaoAcusado() {
        return idsOcupacaoAcusado;
    }

    public void setIdsOcupacaoAcusado(List<Long> v) {
        this.idsOcupacaoAcusado = v;
    }

    public List<Long> getIdsDeficienciaAcusado() {
        return idsDeficienciaAcusado;
    }

    public void setIdsDeficienciaAcusado(List<Long> v) {
        this.idsDeficienciaAcusado = v;
    }

    public List<Long> getIdsRacaEtniaAcusado() {
        return idsRacaEtniaAcusado;
    }

    public void setIdsRacaEtniaAcusado(List<Long> v) {
        this.idsRacaEtniaAcusado = v;
    }

    public List<Long> getIdsBeneficioAcusado() {
        return idsBeneficioAcusado;
    }

    public void setIdsBeneficioAcusado(List<Long> v) {
        this.idsBeneficioAcusado = v;
    }

    public List<Long> getIdsConsequenciaViolenciaAcusado() {
        return idsConsequenciaViolenciaAcusado;
    }

    public void setIdsConsequenciaViolenciaAcusado(List<Long> v) {
        this.idsConsequenciaViolenciaAcusado = v;
    }
}
