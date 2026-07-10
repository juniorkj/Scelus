package br.jus.tjma.scelus.comum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção de domínio para violações de regras de negócio.
 *
 * <p>Quando lançada, resulta em HTTP 422 (Unprocessable Entity).
 * Use para validações de negócio que vão além da validação de campos (Bean Validation).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AppException extends RuntimeException {

    private final String codigo;

    public AppException(String mensagem) {
        super(mensagem);
        this.codigo = "REGRA_NEGOCIO";
    }

    public AppException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public AppException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigo = "REGRA_NEGOCIO";
    }

    public String getCodigo() {
        return codigo;
    }
}
