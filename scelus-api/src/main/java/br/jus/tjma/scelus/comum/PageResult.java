package br.jus.tjma.scelus.comum;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper de resposta paginada compatível com {@code @tjma/angular} TjTable.
 *
 * <p>A lib espera exatamente os campos {@code result} (array) e {@code totalCount}
 * (inteiro) — usados pelo TjTable para renderizar linhas e paginar.
 *
 * <pre>{@code
 * {
 *   "result": [...],
 *   "totalCount": 100,
 *   "pagina": 0,
 *   "tamanho": 20,
 *   "totalPaginas": 5,
 *   "ultima": false
 * }
 * }</pre>
 */
public record PageResult<T>(
        List<T> result,
        long totalCount,
        int pagina,
        int tamanho,
        int totalPaginas,
        boolean ultima
) {
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
