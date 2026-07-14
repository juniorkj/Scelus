package br.jus.tjma.scelus.dominio.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.jus.tjma.scelus.dominio.dto.CadastroCrimeRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroCrimeResponse;
import br.jus.tjma.scelus.dominio.dto.ParteCadastroDTO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Testes de integração para CadastroCrimeService.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 13/07/2026
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
@DisplayName("CadastroCrimeService — Teste de Integração (CSU002)")
class CadastroCrimeServiceIntegrationTest {

    @Autowired
    private CadastroCrimeService cadastroCrimeService;

    @Test
    @DisplayName("Deve cadastrar um crime associado ao processo 0836084-52.2016.8.10.0001 com sucesso")
    void deveCadastrarCrimeComSucesso() {
        // Dados fictícios compatíveis com domínios do banco
        ParteCadastroDTO vitima = new ParteCadastroDTO(
                533622L, // idParte PJe
                1L, // idPolo (Ativo)
                null, // idSituacaoUsoDroga
                1L, // idEstadoCivil (Solteiro)
                1L, // idEscolaridade
                1L, // idRenda
                1L, // idReligiao
                null, // idPosicaoProle
                1L, // idRacaEtnia (Branca)
                null, // observacoesPosicaoProle
                1L, // idCep (Vítima - obrigatório)
                null, // possuiAntecedentes (Exclusivo acusado)
                null, // reincidente (Exclusivo acusado)
                null, // observacaoAntecedentes (Exclusivo acusado)
                null // idOcupacao
                );

        ParteCadastroDTO acusado = new ParteCadastroDTO(
                8961264L, // idParte PJe
                2L, // idPolo (Passivo)
                null, // idSituacaoUsoDroga
                1L, // idEstadoCivil
                1L, // idEscolaridade
                1L, // idRenda
                1L, // idReligiao
                null, // idPosicaoProle
                1L, // idRacaEtnia
                null, // observacoesPosicaoProle
                null, // idCep (Opcional para acusado)
                0L, // possuiAntecedentes (Não)
                0L, // reincidente (Não)
                "Nenhum antecedente", // observacaoAntecedentes
                null // idOcupacao
                );

        CadastroCrimeRequest request = new CadastroCrimeRequest(
                "0836084-52.2016.8.10.0001", // Numero Processo
                14234L, // Codigo Assunto
                LocalDateTime.now().minusDays(2), // Data Fato
                1L, // CEP Fato (Local)
                "S", // Medida Protetiva
                vitima,
                acusado,
                4L, // idTipoVinculo (Ex-companheiro)
                "Vínculo de teste de integração");

        // Executa o cadastro do crime
        CadastroCrimeResponse response = cadastroCrimeService.cadastrar(request);

        // Validações dos retornos
        assertThat(response).isNotNull();
        assertThat(response.idFatoOcorrido()).isNotNull().isGreaterThan(0L);
        assertThat(response.idProcessoCrime()).isNotNull().isGreaterThan(0L);
        assertThat(response.idVitima()).isNotNull().isGreaterThan(0L);
        assertThat(response.idAcusado()).isNotNull().isGreaterThan(0L);
        assertThat(response.mensagem()).contains("GER-S001"); // Sucesso

        System.out.println(">>> Sucesso! ID Fato Ocorrido: " + response.idFatoOcorrido());
        System.out.println(">>> Mensagem do Banco: " + response.mensagem());
    }
}
