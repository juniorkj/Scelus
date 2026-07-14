import { Component, inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  MatStepperNext,
  MatStepperPrevious,
  MatStepperModule,
} from '@angular/material/stepper';
import {
  TjPage,
  TjCard,
  TjInput,
  TjRadio,
  TjSelect,
  TjTextarea,
  TjIconModule,
  TjDatePicker,
  TjRadioOption,
  TjButtonModule,
  TjStepperModule,
  TjPaginaNavegacao,
  TjStepper,
} from '@tjma/angular-21';
import { ConsultarCrimesService } from '../consultar-crimes.service';
import { ProcessoPjeService } from '../processo-pje.service';
import { DominioService } from '../../../core/services/dominio.service';
import {
  CadastroCrimeCompletoRequest,
  ComunicanteWizard,
  ConsequenciaViolenciaWizard,
  CrimeCometidoWizard,
  ItemDominio,
  ParteWizard,
  ProcessoPje,
} from '../consultar-crimes.model';

type Opcao = { label: string; value: number };

/**
 * javadoc Wizard de cadastro de Crimes do Processo (CSU002), com persistência
 * diferida ao "Finalizar" (RN01). Segue a estrutura de passos descrita no plano
 * de testes do CSU002 (TjStepper), com pequeno reordenamento: Vítima e Acusado
 * são cadastrados antes de Benefícios/Configuração Familiar, pois esses blocos
 * referenciam a parte já classificada (a spec original associa por "Parte" antes
 * da classificação, o que gera dependência circular na implementação).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Component({
  selector: 'app-consultar-crimes-wizard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TjPage,
    TjCard,
    TjInput,
    TjRadio,
    TjSelect,
    TjTextarea,
    TjIconModule,
    TjDatePicker,
    TjButtonModule,
    TjStepperModule,
    MatStepperNext,
    MatStepperPrevious,
    MatStepperModule,
  ],

  templateUrl: './consultar-crimes-wizard.component.html',
  styleUrl: './consultar-crimes-wizard.component.scss',
})
export class ConsultarCrimesWizardComponent {
  @ViewChild('stepper') stepper!: TjStepper;

  private readonly router = inject(Router);
  private readonly service = inject(ConsultarCrimesService);
  private readonly processoPjeService = inject(ProcessoPjeService);
  private readonly dominioService = inject(DominioService);

  readonly navigation: TjPaginaNavegacao = {
    grupo: { href: '/crimes', title: 'Consultar Crimes' },
    atual: { href: '/crimes/new', title: 'Cadastrar Crime do Processo' },
  };

  readonly simNaoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

  /** Antecedentes/reincidência (tb_acusado) são persistidos como Long (0/1), não "S"/"N". */
  readonly simNaoNumericoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 1 },
    { label: 'Não', value: 0 },
  ];

  // ── Passo 1: Processo ──
  numeroProcesso = '';
  processoPje?: ProcessoPje;
  buscandoPje = false;
  erroPje?: string;
  parteOptions: Opcao[] = [];
  assuntoOptions: Opcao[] = [];

  // ── Passo 2: Crimes Cometidos (Tela 2.2) ──
  crimesCometidosList: CrimeCometidoWizard[] = [];
  crimeOptions: Opcao[] = [];
  novoCrimeCometido: {
    codigoAssunto?: number;
    dataInicioTipificacao?: string;
    dataFimTipificacao?: string;
  } = {};

  // ── Passo 3/4: Vítima e Acusado ──
  vitima: ParteWizard = { chave: 'V1', idParte: 0, idPolo: 0 };
  acusado: ParteWizard = { chave: 'A1', idParte: 0, idPolo: 0 };

  novaOcupacaoVitima?: number;
  novaDeficienciaVitima?: number;
  novaDrogaVitima?: number;
  novaOcupacaoAcusado?: number;
  novaDeficienciaAcusado?: number;
  novaDrogaAcusado?: number;

  cepBuscaVitima = '';
  buscandoCepVitima = false;
  cepOptionsVitima: Opcao[] = [];

  // ── Passo 4: Benefícios & Configuração Familiar ──
  novoBeneficioVitima: { idTipoBeneficio?: number; dataInicio?: string } = {};
  novoBeneficioAcusado: { idTipoBeneficio?: number; dataInicio?: string } = {};
  novaConfiguracaoFamiliar: {
    idTipoConfiguracaoFamiliar?: number;
    dataDeclaracao?: string;
    observacao?: string;
  } = {};

  // ── Passo 6: Vínculo ──
  vinculo: { idTipoVinculo?: number; observacao?: string } = {};

  // ── Passo 7: Fato Ocorrido ──
  fatoCodigoAssunto?: number;
  dataFato?: string;
  medidaProtetiva?: string;
  fatoIdCep?: number;
  cepBuscaFato = '';
  buscandoCepFato = false;
  cepOptionsFato: Opcao[] = [];

  novoComunicante: Partial<ComunicanteWizard> = {};
  comunicantes: ComunicanteWizard[] = [];

  // ── Passo 8: Consequências da Violência + Resumo ──
  novaConsequencia: {
    chaveParte?: string;
    idTipoConsequenciaViolencia?: number;
    observacao?: string;
  } = {};
  consequencias: ConsequenciaViolenciaWizard[] = [];
  opcoesPartes: Opcao[] = [];

  salvando = false;
  erroFinalizar?: string;

  /** Índice máximo já visitado — define quais passos ficam verdes */
  maxPassoAtingido = -1;

  // ── Domínios ──
  dominios: Record<string, Opcao[]> = {
    polos: [],
    tiposVinculo: [],
    estadosCivis: [],
    escolaridades: [],
    rendas: [],
    religioes: [],
    racasEtnias: [],
    situacoesUsoDroga: [],
    ocupacoes: [],
    deficiencias: [],
    drogas: [],
    escutasJudiciais: [],
    tiposBeneficio: [],
    tiposConfiguracaoFamiliar: [],
    tiposComunicante: [],
    consequenciasViolencia: [],
  };

  constructor() {
    this.carregarDominios();
  }

  private carregarDominios(): void {
    const fontes: Array<[string, string]> = [
      ['tiposVinculo', 'tipos-vinculo'],
      ['estadosCivis', 'estados-civis'],
      ['escolaridades', 'escolaridades'],
      ['rendas', 'rendas'],
      ['religioes', 'religioes'],
      ['racasEtnias', 'racas-etnias'],
      ['situacoesUsoDroga', 'situacoes-uso-droga'],
      ['ocupacoes', 'ocupacoes'],
      ['deficiencias', 'deficiencias'],
      ['drogas', 'drogas'],
      ['escutasJudiciais', 'escutas-judiciais'],
      ['tiposBeneficio', 'tipos-beneficio'],
      ['tiposConfiguracaoFamiliar', 'tipos-configuracao-familiar'],
      ['tiposComunicante', 'tipos-comunicante'],
      ['consequenciasViolencia', 'consequencias-violencia'],
    ];
    for (const [chave, endpoint] of fontes) {
      this.dominioService.listarDominio(endpoint).subscribe({
        next: (itens: ItemDominio[]) => {
          this.dominios[chave] = itens.map(i => ({
            label: i.descricao,
            value: i.id,
          }));
        },
      });
    }
    this.dominioService.listarPolos().subscribe({
      next: (itens: ItemDominio[]) => {
        this.dominios['polos'] = itens.map(i => ({
          label: i.descricao,
          value: i.id,
        }));
      },
    });
  }

  /** Chamado ao navegar entre passos — mantém o maior índice já atingido */
  onStepChange(novoIndice: number): void {
    if (novoIndice > this.maxPassoAtingido) {
      this.maxPassoAtingido = novoIndice;
    }
  }

  /** Retorna true se o passo com o índice dado já foi visitado (e portanto está "concluído") */
  passoCompleto(indice: number): boolean {
    return indice < this.maxPassoAtingido;
  }

  // ── Validações de Erro por Passo (Obrigatórios) ──

  get erroPassoProcesso(): boolean {
    // É erro se não foi buscado processo
    return !this.processoPje;
  }

  get erroPassoCrimesCometidos(): boolean {
    // É erro se nenhum crime cometido foi cadastrado
    return this.crimesCometidosList.length === 0;
  }

  get erroPassoVitima(): boolean {
    // É erro se a vítima não foi selecionada ou se o CEP de residência dela não está preenchido
    return !this.vitima.idParte || !this.vitima.idCep;
  }

  get erroPassoAcusado(): boolean {
    // É erro se o acusado não foi selecionado
    return !this.acusado.idParte;
  }

  get erroPassoFato(): boolean {
    // É erro se o crime, a data do fato, medida protetiva ou CEP do fato não foram informados
    return (
      !this.fatoCodigoAssunto ||
      !this.dataFato ||
      !this.medidaProtetiva ||
      !this.fatoIdCep
    );
  }

  // ── Passo 1 (Tela 2.1): nomes das partes por polo e status de sentença ──
  get poloAtivoNomes(): string {
    const nomes = (this.processoPje?.partes || [])
      .filter(p => p.polo === 'A')
      .map(p => p.nome);
    return nomes.length ? nomes.join(', ') : 'Nenhuma parte no polo ativo.';
  }

  get poloPassivoNomes(): string {
    const nomes = (this.processoPje?.partes || [])
      .filter(p => p.polo === 'P')
      .map(p => p.nome);
    return nomes.length ? nomes.join(', ') : 'Nenhuma parte no polo passivo.';
  }

  buscarProcessoPje(): void {
    const numero = (this.numeroProcesso || '').trim();
    if (!numero) return;

    this.buscandoPje = true;
    this.erroPje = undefined;
    this.processoPje = undefined;
    this.parteOptions = [];
    this.assuntoOptions = [];

    this.processoPjeService.consultarPorNumero(numero).subscribe({
      next: processo => {
        this.processoPje = processo;
        this.parteOptions = (processo.partes || []).map(parte => ({
          label: `${parte.nome}${parte.cpfCnpj ? ' — ' + parte.cpfCnpj : ''} (${parte.polo})`,
          value: parte.idParte,
        }));
        this.assuntoOptions = (processo.assuntos || []).map(a => ({
          label: `${a.codigo} — ${a.descricao}`,
          value: a.codigo,
        }));
        this.novoCrimeCometido = {
          codigoAssunto:
            processo.assuntos?.length === 1
              ? processo.assuntos[0].codigo
              : undefined,
        };
        this.crimesCometidosList = [];
        this.crimeOptions = [];

        // Reseta o estado de progresso do Stepper ao consultar um novo processo
        this.maxPassoAtingido = -1;
        if (this.stepper) {
          this.stepper.reset();
        }

        this.buscandoPje = false;
      },
      error: erro => {
        this.buscandoPje = false;
        this.erroPje =
          erro?.error?.message ||
          'Não foi possível localizar o processo no PJe.';
      },
    });
  }

  // ── Passo 2 (Tela 2.2): Crimes Cometidos ──
  adicionarCrimeCometido(): void {
    const codigoAssunto = this.novoCrimeCometido.codigoAssunto;
    const dataInicio = this.novoCrimeCometido.dataInicioTipificacao;
    if (!codigoAssunto || !dataInicio) return;
    if (
      this.crimesCometidosList.some(
        c => c.codigoAssunto === Number(codigoAssunto)
      )
    ) {
      return;
    }

    this.crimesCometidosList = [
      ...this.crimesCometidosList,
      {
        codigoAssunto: Number(codigoAssunto),
        dataInicioTipificacao: this.normalizarDataHora(dataInicio),
        dataFimTipificacao: this.novoCrimeCometido.dataFimTipificacao
          ? this.normalizarDataHora(this.novoCrimeCometido.dataFimTipificacao)
          : undefined,
      },
    ];
    this.novoCrimeCometido = {};
    this.atualizarOpcoesCrimes();
  }

  removerCrimeCometido(index: number): void {
    this.crimesCometidosList = this.crimesCometidosList.filter(
      (_, i) => i !== index
    );
    this.atualizarOpcoesCrimes();
  }

  private atualizarOpcoesCrimes(): void {
    this.crimeOptions = this.crimesCometidosList.map(c => ({
      label:
        this.assuntoOptions.find(o => o.value === c.codigoAssunto)?.label ||
        String(c.codigoAssunto),
      value: c.codigoAssunto,
    }));
  }

  // ── Passo 3/4: seleção de parte + polo automático ──
  onParteSelecionada(alvo: 'vitima' | 'acusado', idParte: number): void {
    const parte = this.processoPje?.partes?.find(
      p => p.idParte === Number(idParte)
    );
    if (!parte?.polo) return;

    const descricao = parte.polo === 'A' ? 'ATIVO' : 'PASSIVO';
    const opcao = this.dominios['polos'].find(o =>
      o.label.toUpperCase().startsWith(descricao)
    );
    const idPolo = opcao?.value ?? 0;

    if (alvo === 'vitima') {
      this.vitima.idParte = Number(idParte);
      this.vitima.idPolo = idPolo;
    } else {
      this.acusado.idParte = Number(idParte);
      this.acusado.idPolo = idPolo;
    }
    this.atualizarOpcoesPartes();
  }

  private atualizarOpcoesPartes(): void {
    const opcoes: Opcao[] = [];
    if (this.vitima.idParte) opcoes.push({ label: 'Vítima', value: 1 });
    if (this.acusado.idParte) opcoes.push({ label: 'Acusado', value: 2 });
    this.opcoesPartes = opcoes;
  }

  adicionarOcupacao(alvo: 'vitima' | 'acusado'): void {
    const id =
      alvo === 'vitima' ? this.novaOcupacaoVitima : this.novaOcupacaoAcusado;
    if (!id) return;
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsOcupacao = [...(parte.idsOcupacao || []), Number(id)];
    if (alvo === 'vitima') this.novaOcupacaoVitima = undefined;
    else this.novaOcupacaoAcusado = undefined;
  }

  removerOcupacao(alvo: 'vitima' | 'acusado', id: number): void {
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsOcupacao = (parte.idsOcupacao || []).filter(v => v !== id);
  }

  adicionarDeficiencia(alvo: 'vitima' | 'acusado'): void {
    const id =
      alvo === 'vitima'
        ? this.novaDeficienciaVitima
        : this.novaDeficienciaAcusado;
    if (!id) return;
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsDeficiencia = [...(parte.idsDeficiencia || []), Number(id)];
    if (alvo === 'vitima') this.novaDeficienciaVitima = undefined;
    else this.novaDeficienciaAcusado = undefined;
  }

  removerDeficiencia(alvo: 'vitima' | 'acusado', id: number): void {
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsDeficiencia = (parte.idsDeficiencia || []).filter(v => v !== id);
  }

  adicionarDroga(alvo: 'vitima' | 'acusado'): void {
    const id = alvo === 'vitima' ? this.novaDrogaVitima : this.novaDrogaAcusado;
    if (!id) return;
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsDroga = [...(parte.idsDroga || []), Number(id)];
    if (alvo === 'vitima') this.novaDrogaVitima = undefined;
    else this.novaDrogaAcusado = undefined;
  }

  removerDroga(alvo: 'vitima' | 'acusado', id: number): void {
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.idsDroga = (parte.idsDroga || []).filter(v => v !== id);
  }

  descricaoDominio(dominio: string, valor: number): string {
    return (
      this.dominios[dominio]?.find(o => o.value === Number(valor))?.label ||
      String(valor)
    );
  }

  descricaoAssunto(codigoAssunto: number): string {
    return (
      this.assuntoOptions.find(o => o.value === Number(codigoAssunto))?.label ||
      String(codigoAssunto)
    );
  }

  buscarCepVitima(): void {
    const prefixo = (this.cepBuscaVitima || '').replace(/\D/g, '');
    if (!prefixo) return;
    this.buscandoCepVitima = true;
    this.dominioService.pesquisarCeps(prefixo).subscribe({
      next: ceps => {
        this.cepOptionsVitima = ceps.map(c => ({
          label: `${c.cep} — ${c.logradouro || ''} ${c.municipio || ''}/${c.uf || ''}`,
          value: c.id,
        }));
        if (ceps.length === 1) {
          this.vitima.idCep = this.vitima.idCep ?? ceps[0].id;
        }
        this.buscandoCepVitima = false;
      },
      error: () => (this.buscandoCepVitima = false),
    });
  }

  // ── Passo 4: Benefícios & Configuração Familiar ──
  adicionarBeneficio(alvo: 'vitima' | 'acusado'): void {
    const novo =
      alvo === 'vitima' ? this.novoBeneficioVitima : this.novoBeneficioAcusado;
    if (!novo.idTipoBeneficio) return;
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.beneficios = [
      ...(parte.beneficios || []),
      { ...novo, idTipoBeneficio: Number(novo.idTipoBeneficio) },
    ];
    if (alvo === 'vitima') this.novoBeneficioVitima = {};
    else this.novoBeneficioAcusado = {};
  }

  removerBeneficio(alvo: 'vitima' | 'acusado', index: number): void {
    const parte = alvo === 'vitima' ? this.vitima : this.acusado;
    parte.beneficios = (parte.beneficios || []).filter((_, i) => i !== index);
  }

  adicionarConfiguracaoFamiliar(): void {
    if (!this.novaConfiguracaoFamiliar.idTipoConfiguracaoFamiliar) return;
    this.vitima.configuracoesFamiliares = [
      ...(this.vitima.configuracoesFamiliares || []),
      {
        ...this.novaConfiguracaoFamiliar,
        idTipoConfiguracaoFamiliar: Number(
          this.novaConfiguracaoFamiliar.idTipoConfiguracaoFamiliar
        ),
      },
    ];
    this.novaConfiguracaoFamiliar = {};
  }

  removerConfiguracaoFamiliar(index: number): void {
    this.vitima.configuracoesFamiliares = (
      this.vitima.configuracoesFamiliares || []
    ).filter((_, i) => i !== index);
  }

  // ── Passo 6: Fato Ocorrido ──
  buscarCepFato(): void {
    const prefixo = (this.cepBuscaFato || '').replace(/\D/g, '');
    if (!prefixo) return;
    this.buscandoCepFato = true;
    this.dominioService.pesquisarCeps(prefixo).subscribe({
      next: ceps => {
        this.cepOptionsFato = ceps.map(c => ({
          label: `${c.cep} — ${c.logradouro || ''} ${c.municipio || ''}/${c.uf || ''}`,
          value: c.id,
        }));
        this.buscandoCepFato = false;
      },
      error: () => (this.buscandoCepFato = false),
    });
  }

  adicionarComunicante(): void {
    if (!this.novoComunicante.nome || !this.novoComunicante.idTipoComunicante)
      return;
    this.comunicantes = [
      ...this.comunicantes,
      {
        ...(this.novoComunicante as ComunicanteWizard),
        idTipoComunicante: Number(this.novoComunicante.idTipoComunicante),
      },
    ];
    this.novoComunicante = {};
  }

  removerComunicante(index: number): void {
    this.comunicantes = this.comunicantes.filter((_, i) => i !== index);
  }

  // ── Passo 7: Consequências da Violência ──
  adicionarConsequencia(): void {
    if (
      !this.novaConsequencia.idTipoConsequenciaViolencia ||
      !this.novaConsequencia.observacao
    )
      return;
    const chaveParte =
      this.novaConsequencia.chaveParte === 'Acusado'
        ? this.acusado.chave
        : this.vitima.chave;
    this.consequencias = [
      ...this.consequencias,
      {
        chaveParte,
        idTipoConsequenciaViolencia: Number(
          this.novaConsequencia.idTipoConsequenciaViolencia
        ),
        observacao: this.novaConsequencia.observacao,
      },
    ];
    this.novaConsequencia = {};
  }

  removerConsequencia(index: number): void {
    this.consequencias = this.consequencias.filter((_, i) => i !== index);
  }

  // ── Finalizar (RN01 — persistência única) ──
  finalizar(): void {
    this.salvando = true;
    this.erroFinalizar = undefined;

    if (!this.processoPje) {
      this.erroFinalizar =
        'Busque e selecione um processo do PJe antes de finalizar.';
      this.salvando = false;
      return;
    }
    if (this.crimesCometidosList.length === 0) {
      this.erroFinalizar = 'Cadastre ao menos um crime cometido.';
      this.salvando = false;
      return;
    }
    if (!this.vitima.idParte || !this.acusado.idParte) {
      this.erroFinalizar = 'Selecione a vítima e o acusado antes de finalizar.';
      this.salvando = false;
      return;
    }
    if (!this.vitima.idCep) {
      this.erroFinalizar = 'O CEP de residência da vítima é obrigatório.';
      this.salvando = false;
      return;
    }
    if (
      !this.fatoCodigoAssunto ||
      !this.dataFato ||
      !this.medidaProtetiva ||
      !this.cepOptionsFato.length
    ) {
      this.erroFinalizar =
        'Preencha os dados do fato ocorrido (crime, data, medida protetiva e CEP).';
      this.salvando = false;
      return;
    }

    const payload: CadastroCrimeCompletoRequest = {
      numeroProcesso: this.processoPje.numeroUnico,
      crimesCometidos: this.crimesCometidosList,
      vitimas: [this.vitima],
      acusados: [this.acusado],
      vinculos: this.vinculo.idTipoVinculo
        ? [
            {
              chaveVitima: this.vitima.chave,
              chaveAcusado: this.acusado.chave,
              idTipoVinculo: Number(this.vinculo.idTipoVinculo),
              observacao: this.vinculo.observacao,
            },
          ]
        : [],
      fatoOcorrido: {
        codigoAssunto: Number(this.fatoCodigoAssunto),
        chaveVitima: this.vitima.chave,
        chaveAcusado: this.acusado.chave,
        dataFato: this.normalizarDataHora(this.dataFato),
        idCep: Number(this.fatoIdCep),
        medidaProtetiva: this.medidaProtetiva,
        comunicantes: this.comunicantes,
      },
      consequenciasViolencia: this.consequencias,
    };

    this.service.cadastrarCompleto(payload).subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/crimes']);
      },
      error: erro => {
        this.salvando = false;
        this.erroFinalizar =
          erro?.error?.detail ||
          erro?.error?.message ||
          'Não foi possível finalizar o cadastro.';
      },
    });
  }

  private normalizarDataHora(valor: unknown): string {
    if (valor instanceof Date) {
      const ano = valor.getFullYear();
      const mes = String(valor.getMonth() + 1).padStart(2, '0');
      const dia = String(valor.getDate()).padStart(2, '0');
      return `${ano}-${mes}-${dia}T00:00:00`;
    }
    const texto = String(valor || '');
    return texto.includes('T') ? texto : `${texto}T00:00:00`;
  }
}
