package br.jus.tjma.scelus.comum;

import br.jus.tjma.infraspring.sentinela.authz.UsuarioContextContainer;
import java.util.Collections;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Wrapper injetável sobre o {@link br.jus.tjma.infraspring.seguranca.UsuarioContext} da infra-spring.
 *
 * <p>Adicione aqui os métodos de conveniência específicos dos grupos Sentinela do Scelus
 * (ex: {@code isAdmin()}, {@code isOperador()}) conforme os grupos forem cadastrados.
 */
@Primary
@Component
@RequestScope
public class ScelusUsuarioContext {

    private br.jus.tjma.infraspring.seguranca.UsuarioContext ctx() {
        return UsuarioContextContainer.get();
    }

    /** Matrícula/login do usuário autenticado (ex: "129965" ou CPF). */
    public String getLogin() {
        var c = ctx();
        return c != null ? c.getLogin() : null;
    }

    /** Matrícula funcional do usuário. */
    public Long getMatricula() {
        var c = ctx();
        return c != null ? c.getMatricula() : null;
    }

    /** Nome completo do usuário. */
    public String getNome() {
        var c = ctx();
        return c != null ? c.getNome() : null;
    }

    /** Email institucional. */
    public String getEmail() {
        var c = ctx();
        return c != null ? c.getEmail() : null;
    }

    /** Grupos Sentinela aos quais o usuário pertence. */
    public Set<String> getGrupos() {
        var c = ctx();
        return c != null ? c.getGrupos() : Collections.emptySet();
    }

    public boolean pertenceAoGrupo(String nomeGrupo) {
        return getGrupos().contains(nomeGrupo);
    }

    public boolean isAdmin() {
        return pertenceAoGrupo("Administradores");
    }
}
