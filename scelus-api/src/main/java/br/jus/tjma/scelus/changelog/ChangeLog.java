package br.jus.tjma.scelus.changelog;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * javadoc Entidade que representa um item do histórico de alterações (changelog) do sistema,
 * no mesmo padrão adotado no frottas-new (adaptado para PostgreSQL com identity).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Entity
@Table(name = "tb_change_log", schema = "public")
public class ChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_change_log_id")
    private Long id;

    @Column(name = "str_release", nullable = false)
    private String release;

    @Column(name = "str_versao", nullable = false)
    private String versao;

    @Column(name = "str_descricao_item", nullable = false)
    private String descricaoItem;

    public ChangeLog() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public void setDescricaoItem(String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }
}
