package br.jus.tjma.scelus.comum;

import br.jus.tjma.infraspring.dados.FiltroPesquisa;

/**
 * javadoc Especialização de {@link FiltroPesquisa} para binding de query parameters
 * no padrão do frontend @tjma/angular ({@code _limit}, {@code _offset}, {@code _download}).
 *
 * <p>O Spring não faz o binding de {@code _limit} para a propriedade {@code limit};
 * os setters com underscore abaixo criam as propriedades {@code _limit}/{@code _offset}
 * esperadas na URL. Requer também a desativação do field marker prefix ("_") do
 * {@code WebDataBinder} — ver {@code ConfiguracaoWebBinder}.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
public abstract class FiltroPesquisaWeb extends FiltroPesquisa {

    protected FiltroPesquisaWeb() {
        // Paginação padrão caso o frontend não envie os parâmetros.
        setLimit(10);
        setOffset(0);
    }

    public void set_limit(int limit) {
        setLimit(limit);
    }

    public void set_offset(int offset) {
        setOffset(offset);
    }

    public void set_download(boolean download) {
        setDownload(download);
    }

    public void set_downloadFormat(String downloadFormat) {
        setDownloadFormat(downloadFormat);
    }
}
