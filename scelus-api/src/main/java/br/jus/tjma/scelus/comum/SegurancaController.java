package br.jus.tjma.scelus.comum;

import br.jus.tjma.infraspring.seguranca.UsuarioContext;
import br.jus.tjma.infraspring.sentinela.authz.UsuarioContextContainer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de segurança para verificação de autenticação.
 *
 * O endpoint /seguranca/touch é chamado pela lib @tjma/angular (TjAuthService)
 * para verificar se o usuário está autenticado antes de renderizar a aplicação.
 *
 * O SeguranaServletFilter + LoginSentinelaServlet populam o UsuarioContextContainer.
 * Se a requisição chegar aqui, o token é válido → retorna 200.
 */
@RestController
public class SegurancaController {

    /**
     * Keepalive de autenticação.
     * Retorna 200 com os dados do usuário se o token for válido.
     */
    @GetMapping("/seguranca/touch")
    public ResponseEntity<UsuarioContext> touch() {
        UsuarioContext context = UsuarioContextContainer.get();
        return ResponseEntity.ok(context);
    }
}
