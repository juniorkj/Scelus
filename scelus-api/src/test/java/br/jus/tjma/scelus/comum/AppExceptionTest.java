package br.jus.tjma.scelus.comum;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * javadoc Testes unitários para a exceção de domínio {@link AppException}.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@DisplayName("AppException — testes unitários")
class AppExceptionTest {

    @Test
    @DisplayName("deve usar código padrão REGRA_NEGOCIO quando instanciada apenas com mensagem")
    void deveUsarCodigoPadraoQuandoInstanciadaComMensagem() {
        // Arrange & Act
        var excecao = new AppException("Operação inválida");

        // Assert
        assertThat(excecao.getMessage()).isEqualTo("Operação inválida");
        assertThat(excecao.getCodigo()).isEqualTo("REGRA_NEGOCIO");
    }

    @Test
    @DisplayName("deve preservar código personalizado quando fornecido")
    void devePreservarCodigoPersonalizadoQuandoFornecido() {
        // Arrange & Act
        var excecao = new AppException("CSU001-E01", "Processo não localizado no PJe");

        // Assert
        assertThat(excecao.getMessage()).isEqualTo("Processo não localizado no PJe");
        assertThat(excecao.getCodigo()).isEqualTo("CSU001-E01");
    }

    @Test
    @DisplayName("deve encadear a causa original quando fornecida")
    void deveEncadearCausaOriginalQuandoFornecida() {
        // Arrange
        var causaOriginal = new IllegalStateException("Falha de conexão");

        // Act
        var excecao = new AppException("Erro ao comunicar com PJe", causaOriginal);

        // Assert
        assertThat(excecao.getMessage()).isEqualTo("Erro ao comunicar com PJe");
        assertThat(excecao.getCodigo()).isEqualTo("REGRA_NEGOCIO");
        assertThat(excecao.getCause()).isSameAs(causaOriginal);
    }

    @Test
    @DisplayName("deve ser subclasse de RuntimeException")
    void deveSerSubclasseDeRuntimeException() {
        var excecao = new AppException("teste");
        assertThat(excecao).isInstanceOf(RuntimeException.class);
    }
}
