package br.jus.tjma.scelus.dominio.model;

import jakarta.persistence.*;

/**
 * javadoc Entidade que representa a pessoa que noticiou o fato ocorrido (CSU002 — Tela 2.8).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Entity
@Table(name = "tb_comunicante", schema = "public")
public class Comunicante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_comunicante_id")
    private Long id;

    @Column(name = "str_nome", nullable = false)
    private String nome;

    @Column(name = "str_telefone")
    private String telefone;

    @Column(name = "str_email")
    private String email;

    @Column(name = "str_cpf_cnpj")
    private String cpfCnpj;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
