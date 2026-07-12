package br.jus.tjma.scelus.dominio.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * javadoc Testes unitários para o DTO de filtro {@link FiltroConsultaCrimes}.
 * Cobre os requisitos de filtragem do CSU001 — Consultar Crimes do Processo.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@DisplayName("FiltroConsultaCrimes — testes unitários (CSU001)")
class FiltroConsultaCrimesTest {

    @Test
    @DisplayName("deve inicializar com todos os campos nulos")
    void deveInicializarComTodosOsCamposNulos() {
        var filtro = new FiltroConsultaCrimes();

        assertThat(filtro.getNumeroProcesso()).isNull();
        assertThat(filtro.getCodigoAssunto()).isNull();
        assertThat(filtro.getNomeVitima()).isNull();
        assertThat(filtro.getCpfVitima()).isNull();
        assertThat(filtro.getNomeAcusado()).isNull();
        assertThat(filtro.getCpfAcusado()).isNull();
        assertThat(filtro.getTipoVinculo()).isNull();
        assertThat(filtro.getDataFato()).isNull();
        assertThat(filtro.getMedidaProtetiva()).isNull();
    }

    @Test
    @DisplayName("deve persistir numeroProcesso corretamente via setter")
    void devePersistirNumeroProcesso() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setNumeroProcesso("0000123-45.2024.8.10.0001");

        assertThat(filtro.getNumeroProcesso()).isEqualTo("0000123-45.2024.8.10.0001");
    }

    @Test
    @DisplayName("deve persistir codigoAssunto como Long")
    void devePersistirCodigoAssunto() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setCodigoAssunto(10625L);

        assertThat(filtro.getCodigoAssunto()).isEqualTo(10625L);
    }

    @Test
    @DisplayName("deve persistir nomeVitima corretamente")
    void devePersistirNomeVitima() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setNomeVitima("Maria da Silva");

        assertThat(filtro.getNomeVitima()).isEqualTo("Maria da Silva");
    }

    @Test
    @DisplayName("deve persistir cpfVitima corretamente")
    void devePersistirCpfVitima() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setCpfVitima("123.456.789-00");

        assertThat(filtro.getCpfVitima()).isEqualTo("123.456.789-00");
    }

    @Test
    @DisplayName("deve persistir nomeAcusado corretamente")
    void devePersistirNomeAcusado() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setNomeAcusado("João Pereira");

        assertThat(filtro.getNomeAcusado()).isEqualTo("João Pereira");
    }

    @Test
    @DisplayName("deve persistir cpfAcusado corretamente")
    void devePersistirCpfAcusado() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setCpfAcusado("987.654.321-00");

        assertThat(filtro.getCpfAcusado()).isEqualTo("987.654.321-00");
    }

    @Test
    @DisplayName("deve persistir tipoVinculo como Long")
    void devePersistirTipoVinculo() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setTipoVinculo(3L);

        assertThat(filtro.getTipoVinculo()).isEqualTo(3L);
    }

    @Test
    @DisplayName("deve persistir dataFato como LocalDate")
    void devePersistirDataFatoComoLocalDate() {
        var data = LocalDate.of(2024, 3, 15);
        var filtro = new FiltroConsultaCrimes();
        filtro.setDataFato(data);

        assertThat(filtro.getDataFato()).isEqualTo(LocalDate.of(2024, 3, 15));
    }

    @Test
    @DisplayName("deve persistir medidaProtetiva corretamente")
    void devePersistirMedidaProtetiva() {
        var filtro = new FiltroConsultaCrimes();
        filtro.setMedidaProtetiva("S");

        assertThat(filtro.getMedidaProtetiva()).isEqualTo("S");
    }
}
