package br.jus.tjma.scelus.comum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.jus.tjma.infraspring.seguranca.UsuarioContext;
import br.jus.tjma.infraspring.sentinela.authz.UsuarioContextContainer;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * javadoc Testes unitários para {@link ScelusUsuarioContext}.
 * Verificam o comportamento do wrapper do contexto Sentinela em cenários
 * de contexto nulo (sem autenticação) e contexto populado.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScelusUsuarioContext — testes unitários")
class ScelusUsuarioContextTest {

    @Mock
    private UsuarioContext usuarioContextMock;

    @AfterEach
    void limparContexto() {
        // Remove contexto após cada teste para evitar vazamento entre testes
        try {
            UsuarioContextContainer.clear();
        } catch (Exception ignored) {
            // ignorar se clear não estiver disponível
        }
    }

    @Nested
    @DisplayName("quando o contexto Sentinela está populado (usuário autenticado)")
    class QuandoContextoPopulado {

        @Test
        @DisplayName("pertenceAoGrupo deve retornar true para grupo existente")
        void pertenceAoGrupoDeveRetornarTrueParaGrupoExistente() {
            when(usuarioContextMock.getGrupos()).thenReturn(Set.of("Administradores", "Operadores"));
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.pertenceAoGrupo("Administradores")).isTrue();
        }

        @Test
        @DisplayName("pertenceAoGrupo deve retornar false para grupo inexistente")
        void pertenceAoGrupoDeveRetornarFalseParaGrupoInexistente() {
            when(usuarioContextMock.getGrupos()).thenReturn(Set.of("Operadores"));
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.pertenceAoGrupo("Administradores")).isFalse();
        }

        @Test
        @DisplayName("isAdmin deve retornar true somente para o grupo Administradores")
        void isAdminDeveRetornarTrueSomenteParaGrupoAdministradores() {
            when(usuarioContextMock.getGrupos()).thenReturn(Set.of("Administradores"));
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("isAdmin deve retornar false quando usuário não é administrador")
        void isAdminDeveRetornarFalseQuandoNaoAdministrador() {
            when(usuarioContextMock.getGrupos()).thenReturn(Set.of("Operadores", "Consulta"));
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("getLogin deve delegar ao contexto autenticado")
        void getLoginDeveDelegarAoContextoAutenticado() {
            when(usuarioContextMock.getLogin()).thenReturn("129965");
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.getLogin()).isEqualTo("129965");
        }

        @Test
        @DisplayName("getMatricula deve delegar ao contexto autenticado")
        void getMatriculaDeveDelegarAoContextoAutenticado() {
            when(usuarioContextMock.getMatricula()).thenReturn(129965L);
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.getMatricula()).isEqualTo(129965L);
        }

        @Test
        @DisplayName("getNome deve delegar ao contexto autenticado")
        void getNomeDeveDelegarAoContextoAutenticado() {
            when(usuarioContextMock.getNome()).thenReturn("João da Silva");
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.getNome()).isEqualTo("João da Silva");
        }

        @Test
        @DisplayName("getGrupos deve retornar os grupos do contexto autenticado")
        void getGruposDeveRetornarGruposDoContexto() {
            var grupos = Set.of("Administradores", "Operadores");
            when(usuarioContextMock.getGrupos()).thenReturn(grupos);
            UsuarioContextContainer.set(usuarioContextMock);
            var contexto = new ScelusUsuarioContext();

            assertThat(contexto.getGrupos()).containsExactlyInAnyOrder("Administradores", "Operadores");
        }
    }
}
