package br.jus.tjma.scelus.comum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleNaoEncontrada(EntidadeNaoEncontradaException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/nao-encontrado"));
        problem.setTitle("Entidade não encontrada");
        problem.setProperty("message", ex.getMessage());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // Cobre ambos os pacotes: sentinela.AcessoNegadoException e seguranca.AcessoNegadoException
    @ExceptionHandler({
        br.jus.tjma.infraspring.sentinela.AcessoNegadoException.class,
        br.jus.tjma.infraspring.seguranca.AcessoNegadoException.class
    })
    public ProblemDetail handleAcessoNegado(Exception ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/acesso-negado"));
        problem.setTitle("Acesso Negado");
        problem.setProperty("message", "Você não tem permissão para realizar esta operação.");
        problem.setProperty("detalhe", ex.getMessage());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/regra-negocio"));
        problem.setTitle("Violação de regra de negócio");
        problem.setProperty("message", ex.getMessage());
        problem.setProperty("codigo", ex.getCodigo());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (a, b) -> a + "; " + b
                ));

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Dados de entrada inválidos. Corrija os campos indicados em 'campos'.");
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/validacao"));
        problem.setTitle("Erro de validação");
        problem.setProperty("message", problem.getDetail());
        problem.setProperty("campos", erros);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(br.jus.tjma.infraspring.TJMABaseException.class)
    public ProblemDetail handleTJMABaseException(br.jus.tjma.infraspring.TJMABaseException ex) {
        HttpStatus status = ("SES-E001".equals(ex.getErrorCode()) || "SES-E002".equals(ex.getErrorCode()))
                ? HttpStatus.UNAUTHORIZED : HttpStatus.CONFLICT;
        var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/banco"));
        problem.setTitle("Erro de Processamento no Banco");
        problem.setProperty("message", ex.getMessage());
        problem.setProperty("codigo", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex) {
        log.error("Erro interno não tratado", ex);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Contate o suporte informando o horário da ocorrência.");
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/interno"));
        problem.setTitle("Erro interno");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
