package br.jus.tjma.scelus.comum;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                        (a, b) -> a + "; " + b));

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Dados de entrada inválidos. Corrija os campos indicados em 'campos'.");
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
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.CONFLICT;
        var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/banco"));
        problem.setTitle("Erro de Processamento no Banco");
        problem.setProperty("message", ex.getMessage());
        problem.setProperty("codigo", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ProblemDetail handleRecursoNaoEncontrado(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "Recurso não encontrado: " + ex.getResourcePath());
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/nao-encontrado"));
        problem.setTitle("Recurso não encontrado");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex) {
        log.error("Erro interno não tratado", ex);

        String dblinkMsg = obterMensagemDblink(ex);
        if (dblinkMsg != null) {
            String dblinkName = "pje";
            if (dblinkMsg.contains("'pje'") || dblinkMsg.contains("pje")) {
                dblinkName = "pje";
            } else if (dblinkMsg.contains("'sentinela'") || dblinkMsg.contains("sentinela")) {
                dblinkName = "sentinela";
            }

            var problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha na comunicação com o banco de dados externo via dblink '" + dblinkName
                            + "'. Certifique-se de que o serviço remoto está disponível.");
            problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/dblink"));
            problem.setTitle("Falha no DB Link");
            problem.setProperty("dblink", dblinkName);
            problem.setProperty("detalhe", dblinkMsg);
            problem.setProperty("timestamp", Instant.now());
            return problem;
        }

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Contate o suporte informando o horário da ocorrência.");
        problem.setType(URI.create("https://sistemas.tjma.jus.br/scelus-api/erros/interno"));
        problem.setTitle("Erro interno");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    private String obterMensagemDblink(Throwable t) {
        if (t == null) {
            return null;
        }
        // Prioriza as causas mais profundas (root cause)
        String msgCausa = obterMensagemDblink(t.getCause());
        if (msgCausa != null) {
            return msgCausa;
        }
        String msg = t.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("dblink")
                    || lower.contains("connection")
                    || lower.contains("connect")
                    || lower.contains("login")
                    || lower.contains("failed")
                    || lower.contains("poshpje")
                    || lower.contains("pje")) {
                return msg;
            }
        }
        return null;
    }
}
