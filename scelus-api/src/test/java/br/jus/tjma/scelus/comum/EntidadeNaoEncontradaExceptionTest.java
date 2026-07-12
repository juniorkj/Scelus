package br.jus.tjma.scelus.comum;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * javadoc Testes unitários para {@link EntidadeNaoEncontradaException}.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@DisplayName("EntidadeNaoEncontradaException — testes unitários")
class EntidadeNaoEncontradaExceptionTest {

    @Test
    @DisplayName("deve retornar mensagem padrão quando instanciada sem argumentos")
    void deveRetornarMensagemPadraoQuandoSemArgumentos() {
        // Arrange & Act
        var excecao = new EntidadeNaoEncontradaException();

        // Assert
        assertThat(excecao.getMessage()).isEqualTo("Entidade não encontrada");
    }

    @Test
    @DisplayName("deve retornar mensagem customizada quando fornecida")
    void deveRetornarMensagemCustomizadaQuandoFornecida() {
        // Arrange & Act
        var excecao = new EntidadeNaoEncontradaException("FatoOcorrido com id 99 não encontrado");

        // Assert
        assertThat(excecao.getMessage()).isEqualTo("FatoOcorrido com id 99 não encontrado");
    }

    @Test
    @DisplayName("deve ser subclasse de RuntimeException")
    void deveSerSubclasseDeRuntimeException() {
        var excecao = new EntidadeNaoEncontradaException();
        assertThat(excecao).isInstanceOf(RuntimeException.class);
    }
}
