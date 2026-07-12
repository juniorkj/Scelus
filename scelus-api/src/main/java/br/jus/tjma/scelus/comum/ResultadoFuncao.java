package br.jus.tjma.scelus.comum;

/**
 * javadoc Record que encapsula o retorno padrão das funções do banco PostgreSQL
 * (id gerado + mensagem formatada por pkg_sistema_util.fn_mensagem, ex.: "GER-S001. Inclusão realizada com sucesso.").
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public record ResultadoFuncao(Long id, String mensagem) {

    /**
     * Indica sucesso quando a mensagem segue o padrão de códigos de sucesso (ex.: GER-S001).
     */
    public boolean sucesso() {
        return mensagem != null && mensagem.matches("(?s)^[A-Z]{3}-S\\d+\\..*");
    }

    /**
     * Lança {@link AppException} com a mensagem retornada pelo banco caso a execução não tenha sido bem-sucedida.
     */
    public void validar() {
        if (!sucesso()) {
            String detalhe =
                    (mensagem == null || mensagem.isBlank()) ? "Falha na execução da função de banco." : mensagem;
            throw new AppException("FUNCAO_BANCO", detalhe);
        }
    }
}
