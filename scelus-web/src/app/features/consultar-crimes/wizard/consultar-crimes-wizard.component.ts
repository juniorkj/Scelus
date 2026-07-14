import { Component, inject, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
  TjSearchField,
} from '@tjma/angular-21';
import { ConsultarCrimesService } from '../consultar-crimes.service';
import { ProcessoPjeService } from '../processo-pje.service';
import { DominioService } from '../../../core/services/dominio.service';
import { MpuSeletorComponent } from '../mpu-seletor/mpu-seletor.component';
import { MpusService } from '../../mpus/mpus.service';
import {
  CadastroCrimeCompletoRequest,
  ComunicanteWizard,
  ConsequenciaViolenciaWizard,
  CrimeCometidoWizard,
  ItemDominio,
  MpuDTO,
  MpuVinculada,
  MpuVinculoWizard,
  ParteWizard,
  ProcessoPje,
  VinculoMpuRequest,
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
    TjSearchField,
    MatStepperNext,
    MatStepperPrevious,
    MatStepperModule,
  ],

  templateUrl: './consultar-crimes-wizard.component.html',
  styleUrl: './consultar-crimes-wizard.component.scss',
})
export class ConsultarCrimesWizardComponent {
  @ViewChild('stepper') stepper!: TjStepper;
  @ViewChild('numeroProcessoInput') numeroProcessoInput!: TjInput;

  /** Registra o componente do modal seletor de MPU assim que o campo é renderizado. */
  @ViewChild(TjSearchField)
  set mpuField(field: TjSearchField | undefined) {
    field?.setDialogComponent(MpuSeletorComponent);
  }

  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly service = inject(ConsultarCrimesService);
  private readonly processoPjeService = inject(ProcessoPjeService);
  private readonly dominioService = inject(DominioService);
  private readonly mpusService = inject(MpusService);

  /** Identificador do fato ocorrido em edição/visualização (ausente em cadastro novo). */
  idFatoOcorrido?: number;
  modoSomenteLeitura = false;
  carregandoDetalhe = false;

  get navigation(): TjPaginaNavegacao {
    return {
      grupo: { href: '/crimes', title: 'Consultar Crimes' },
      atual: { href: this.router.url, title: this.tituloPagina },
    };
  }

  get tituloPagina(): string {
    if (this.modoSomenteLeitura) return 'Visualizar Crime do Processo';
    if (this.idFatoOcorrido) return 'Editar Crime do Processo';
    return 'Cadastrar Crime do Processo';
  }

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

  // ── MPU vinculada ao fato ocorrido (CSU008 — somente em edição/visualização) ──
  mpusVinculadas: MpuVinculada[] = [];
  mpuSelecionada?: { id: number; numeroMpu?: string };
  vinculoMpu: {
    idJustificativaInclusaoMpu?: number;
    observacaoJustificativa?: string;
  } = {};
  vinculandoMpu = false;

  // ── CSU008 — Telas 8.1/8.2/8.3, estado em memória (cadastro novo, RN008.07) ──
  mpusList: MpuVinculoWizard[] = [];
  mpuIdentificadas: MpuDTO[] = [];
  buscandoMpuIdentificadas = false;
  mpuIdentificadasBuscadas = false;
  mpuSelecionadaParaJustificativa?: MpuDTO;
  justificativaSelecionada: {
    idJustificativaInclusaoMpu?: number;
    observacaoJustificativa?: string;
  } = {};
  novaMpu: {
    numeroMpu?: string;
    legislacaoFundamento?: string;
    dataDecisao?: string;
    concedida?: string;
    dataIntimacaoAcusado?: string;
    dataIntimacaoVitima?: string;
    dataCienciaVitima?: string;
    dataCienciaAcusado?: string;
    pedidoDesistencia?: string;
    inqueritoInstaurado?: string;
    observacoes?: string;
    idJustificativaInclusaoMpu?: number;
    observacaoJustificativa?: string;
  } = {};

  salvando = false;
  erroFinalizar?: string;

  /** Índice máximo já visitado — define quais passos ficam verdes */
  maxPassoAtingido = 0;

  /** Índice do passo atualmente selecionado — o passo atual nunca fica vermelho. */
  passoAtual = 0;

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

  /** Justificativas de inclusão de MPU (CSU008), carregadas à parte de `dominios`. */
  justificativaOptions: Opcao[] = [];

  constructor() {
    this.carregarDominios();
    this.dominioService.listarJustificativasInclusaoMpu().subscribe({
      next: itens => {
        this.justificativaOptions = itens.map(i => ({
          label: i.descricao,
          value: i.id,
        }));
      },
    });

    // Mesma convenção do TjCrudBaseComponent: `?_readonly=true` na própria
    // rota `/crimes/:id` alterna entre visualizar e editar (sem rota própria).
    this.modoSomenteLeitura =
      this.activatedRoute.snapshot.queryParamMap.get('_readonly') === 'true';
    const idParam = this.activatedRoute.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'new') {
      this.carregarDetalhe(Number(idParam));
    }
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

  /** Índice do passo "Fato Ocorrido" (0-based): Processo, Crimes, Vítima, Acusado, Benefícios, Vínculo, Fato. */
  private readonly PASSO_FATO_OCORRIDO = 6;

  /** Chamado ao navegar entre passos — mantém o maior índice já atingido */
  onStepChange(novoIndice: number): void {
    this.passoAtual = novoIndice;
    if (novoIndice > this.maxPassoAtingido) {
      this.maxPassoAtingido = novoIndice;
    }
    // RN008.02: ao chegar no passo do Fato Ocorrido em um cadastro novo, busca
    // automaticamente as MPUs já cadastradas para o par vítima-acusado.
    if (
      novoIndice === this.PASSO_FATO_OCORRIDO &&
      !this.idFatoOcorrido &&
      !this.mpuIdentificadasBuscadas
    ) {
      this.buscarMpusIdentificadas();
    }
  }

  /** Retorna true se o passo com o índice dado já foi visitado (e portanto está "concluído") */
  passoCompleto(indice: number): boolean {
    return indice < this.maxPassoAtingido;
  }

  // ── Validações de Erro por Passo (Obrigatórios) ──

  /**
   * Um passo só fica vermelho depois de já ter sido visitado (o usuário passou
   * por ele) e ainda estar incompleto — nunca no primeiro carregamento da página
   * nem no passo em que o usuário está no momento (senão todo passo com campo
   * obrigatório nasce vermelho antes mesmo de o usuário digitar algo).
   */
  private passoVisitadoComErro(indice: number, condicaoErro: boolean): boolean {
    return (
      condicaoErro &&
      indice !== this.passoAtual &&
      indice <= this.maxPassoAtingido
    );
  }

  get erroPassoProcesso(): boolean {
    return this.passoVisitadoComErro(0, !this.processoPje);
  }

  get erroPassoCrimesCometidos(): boolean {
    return this.passoVisitadoComErro(1, this.crimesCometidosList.length === 0);
  }

  get erroPassoVitima(): boolean {
    return this.passoVisitadoComErro(
      2,
      !this.vitima.idParte || !this.vitima.idCep
    );
  }

  get erroPassoAcusado(): boolean {
    return this.passoVisitadoComErro(3, !this.acusado.idParte);
  }

  get erroPassoFato(): boolean {
    return this.passoVisitadoComErro(
      this.PASSO_FATO_OCORRIDO,
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
    if (!numero) {
      if (this.numeroProcessoInput) {
        this.numeroProcessoInput.onTouch();
      }
      return;
    }

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
        this.maxPassoAtingido = 0;
        this.passoAtual = 0;
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

  /** Carrega o detalhe completo de um crime/fato ocorrido (edição/visualização). */
  private carregarDetalhe(idFatoOcorrido: number): void {
    this.carregandoDetalhe = true;
    this.service.buscarCompleto(idFatoOcorrido).subscribe({
      next: detalhe => {
        this.idFatoOcorrido = detalhe.idFatoOcorrido;
        this.numeroProcesso = detalhe.numeroProcesso;

        this.buscandoPje = true;
        this.processoPjeService
          .consultarPorNumero(detalhe.numeroProcesso)
          .subscribe({
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
              this.buscandoPje = false;

              this.crimesCometidosList = detalhe.crimesCometidos.map(c => ({
                codigoAssunto: c.codigoAssunto,
                descricaoAssunto: c.descricaoAssunto,
                dataInicioTipificacao: c.dataInicioTipificacao,
                dataFimTipificacao: c.dataFimTipificacao,
              }));
              this.atualizarOpcoesCrimes();

              this.vitima = { chave: 'V1', ...detalhe.vitima };
              this.acusado = { chave: 'A1', ...detalhe.acusado };
              this.atualizarOpcoesPartes();

              if (detalhe.vitima.idCep) {
                this.cepOptionsVitima = [
                  {
                    label: `CEP ${detalhe.vitima.idCep}`,
                    value: detalhe.vitima.idCep,
                  },
                ];
              }

              if (detalhe.vinculo) {
                this.vinculo = {
                  idTipoVinculo: detalhe.vinculo.idTipoVinculo,
                  observacao: detalhe.vinculo.observacao,
                };
              }

              this.fatoCodigoAssunto = detalhe.fatoOcorrido.codigoAssunto;
              this.dataFato = detalhe.fatoOcorrido.dataFato;
              this.medidaProtetiva = detalhe.fatoOcorrido.medidaProtetiva;
              this.fatoIdCep = detalhe.fatoOcorrido.idCep;
              this.cepOptionsFato = [
                {
                  label: `CEP ${detalhe.fatoOcorrido.idCep}`,
                  value: detalhe.fatoOcorrido.idCep,
                },
              ];
              this.comunicantes = detalhe.fatoOcorrido.comunicantes.map(c => ({
                nome: c.nome,
                telefone: c.telefone,
                email: c.email,
                cpfCnpj: c.cpfCnpj,
                idTipoComunicante: c.idTipoComunicante,
                dataDenuncia: c.dataDenuncia,
                observacao: c.observacao,
                anonimizado: c.anonimizado,
              }));

              this.consequencias = detalhe.consequenciasViolencia.map(c => ({
                chaveParte:
                  c.parte === 'vitima' ? this.vitima.chave : this.acusado.chave,
                idTipoConsequenciaViolencia: c.idTipoConsequenciaViolencia,
                observacao: c.observacao || '',
              }));

              // Marca todos os passos como já visitados, liberando navegação livre.
              this.maxPassoAtingido = 8;

              this.carregarMpusVinculadas();
              this.carregandoDetalhe = false;
            },
            error: () => {
              this.buscandoPje = false;
              this.carregandoDetalhe = false;
              this.erroPje =
                'Não foi possível recarregar os dados do processo no PJe.';
            },
          });
      },
      error: erro => {
        this.carregandoDetalhe = false;
        this.erroFinalizar =
          erro?.error?.detail ||
          erro?.error?.message ||
          'Não foi possível carregar o crime cadastrado.';
      },
    });
  }

  // ── MPU vinculada ao fato ocorrido (CSU008) ──
  carregarMpusVinculadas(): void {
    if (!this.idFatoOcorrido) return;
    this.service.listarMpusDoFato(this.idFatoOcorrido).subscribe({
      next: mpus => (this.mpusVinculadas = mpus),
    });
  }

  vincularMpu(): void {
    const idMpu = this.mpuSelecionada?.id;
    const idJustificativa = this.vinculoMpu.idJustificativaInclusaoMpu;
    if (!idMpu || !idJustificativa || !this.idFatoOcorrido) return;

    const request: VinculoMpuRequest = {
      idMpu: Number(idMpu),
      idJustificativaInclusaoMpu: Number(idJustificativa),
      observacaoJustificativa: this.vinculoMpu.observacaoJustificativa,
    };

    this.vinculandoMpu = true;
    this.service.vincularMpu(this.idFatoOcorrido, request).subscribe({
      next: () => {
        this.vinculandoMpu = false;
        this.mpuSelecionada = undefined;
        this.vinculoMpu = {};
        this.carregarMpusVinculadas();
      },
      error: () => {
        this.vinculandoMpu = false;
      },
    });
  }

  removerVinculoMpu(vinculo: MpuVinculada): void {
    if (!this.idFatoOcorrido) return;
    if (
      !confirm(
        `Remover o vínculo da MPU ${vinculo.numeroMpu || vinculo.idMpu}?`
      )
    ) {
      return;
    }
    this.service.removerVinculoMpu(this.idFatoOcorrido, vinculo.id).subscribe({
      next: () => this.carregarMpusVinculadas(),
    });
  }

  get podeVincularMpu(): boolean {
    if (
      !this.mpuSelecionada?.id ||
      !this.vinculoMpu.idJustificativaInclusaoMpu ||
      this.vinculandoMpu
    ) {
      return false;
    }
    if (
      this.ehJustificativaOutros(this.vinculoMpu.idJustificativaInclusaoMpu) &&
      !this.vinculoMpu.observacaoJustificativa
    ) {
      return false;
    }
    return true;
  }

  // ── CSU008 (Telas 8.1/8.2/8.3) — cadastro novo: estado em memória (RN008.07) ──

  /** RN008.02: busca global de MPUs para o par vítima-acusado, pelas partes do PJe. */
  buscarMpusIdentificadas(): void {
    if (!this.vitima.idParte || !this.acusado.idParte) return;
    this.buscandoMpuIdentificadas = true;
    this.mpusService
      .buscarPorPar(this.vitima.idParte, this.acusado.idParte)
      .subscribe({
        next: mpus => {
          this.mpuIdentificadas = mpus;
          this.mpuIdentificadasBuscadas = true;
          this.buscandoMpuIdentificadas = false;
        },
        error: () => {
          this.mpuIdentificadasBuscadas = true;
          this.buscandoMpuIdentificadas = false;
        },
      });
  }

  /** RN008.04: seleção explícita de uma MPU identificada, abrindo a Tela 8.3 (justificativa). */
  selecionarMpuIdentificada(mpu: MpuDTO): void {
    this.mpuSelecionadaParaJustificativa = mpu;
    this.justificativaSelecionada = {};
  }

  cancelarJustificativaMpu(): void {
    this.mpuSelecionadaParaJustificativa = undefined;
    this.justificativaSelecionada = {};
  }

  /** RN008.05: quando a justificativa selecionada for "Outros", a observação é obrigatória. */
  ehJustificativaOutros(
    idJustificativaInclusaoMpu: number | undefined
  ): boolean {
    const opcao = this.justificativaOptions.find(
      o => o.value === Number(idJustificativaInclusaoMpu)
    );
    return !!opcao && opcao.label.trim().toLowerCase() === 'outros';
  }

  get podeConfirmarJustificativaMpu(): boolean {
    if (!this.justificativaSelecionada.idJustificativaInclusaoMpu) return false;
    if (
      this.ehJustificativaOutros(
        this.justificativaSelecionada.idJustificativaInclusaoMpu
      ) &&
      !this.justificativaSelecionada.observacaoJustificativa
    ) {
      return false;
    }
    return true;
  }

  /** RN008.06: registra a associação temporária (MPU identificada + justificativa) no quadro Resumo. */
  confirmarJustificativaMpuIdentificada(): void {
    if (
      !this.mpuSelecionadaParaJustificativa ||
      !this.podeConfirmarJustificativaMpu
    ) {
      return;
    }
    this.mpusList = [
      ...this.mpusList,
      {
        idMpuExistente: this.mpuSelecionadaParaJustificativa.id,
        idJustificativaInclusaoMpu: Number(
          this.justificativaSelecionada.idJustificativaInclusaoMpu
        ),
        observacaoJustificativa:
          this.justificativaSelecionada.observacaoJustificativa,
      },
    ];
    this.mpuSelecionadaParaJustificativa = undefined;
    this.justificativaSelecionada = {};
  }

  get podeAdicionarNovaMpu(): boolean {
    const n = this.novaMpu;
    if (
      !n.legislacaoFundamento ||
      !n.dataDecisao ||
      !n.concedida ||
      !n.dataIntimacaoAcusado ||
      !n.dataIntimacaoVitima ||
      !n.pedidoDesistencia ||
      !n.inqueritoInstaurado ||
      !n.idJustificativaInclusaoMpu
    ) {
      return false;
    }
    if (
      this.ehJustificativaOutros(n.idJustificativaInclusaoMpu) &&
      !n.observacaoJustificativa
    ) {
      return false;
    }
    return true;
  }

  /** RN008.01: registra temporariamente uma MPU nova, com processo/vítima/acusado do fato em pauta. */
  adicionarNovaMpu(): void {
    if (!this.podeAdicionarNovaMpu) return;
    const n = this.novaMpu;

    this.mpusList = [
      ...this.mpusList,
      {
        novaMpu: {
          legislacaoFundamento: n.legislacaoFundamento!,
          dataDecisao: this.normalizarDataHora(n.dataDecisao),
          concedida: n.concedida!,
          dataIntimacaoAcusado: this.normalizarDataHora(n.dataIntimacaoAcusado),
          dataIntimacaoVitima: this.normalizarDataHora(n.dataIntimacaoVitima),
          dataCienciaVitima: n.dataCienciaVitima
            ? this.normalizarDataHora(n.dataCienciaVitima)
            : undefined,
          dataCienciaAcusado: n.dataCienciaAcusado
            ? this.normalizarDataHora(n.dataCienciaAcusado)
            : undefined,
          pedidoDesistencia: n.pedidoDesistencia!,
          inqueritoInstaurado: n.inqueritoInstaurado!,
          observacoes: n.observacoes,
          numeroMpu: n.numeroMpu,
          numeroUnico: this.processoPje?.numeroUnico,
        },
        idJustificativaInclusaoMpu: Number(n.idJustificativaInclusaoMpu),
        observacaoJustificativa: n.observacaoJustificativa,
      },
    ];
    this.novaMpu = {};
  }

  removerMpuStaged(index: number): void {
    this.mpusList = this.mpusList.filter((_, i) => i !== index);
  }

  descricaoMpuStaged(mpu: MpuVinculoWizard): string {
    if (mpu.novaMpu) {
      return `Nova MPU — ${mpu.novaMpu.numeroMpu || mpu.novaMpu.legislacaoFundamento}`;
    }
    const identificada = this.mpuIdentificadas.find(
      m => m.id === mpu.idMpuExistente
    );
    return `MPU ${identificada?.numeroMpu || mpu.idMpuExistente}`;
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

    const assunto = this.processoPje?.assuntos?.find(
      a => a.codigo === Number(codigoAssunto)
    );

    this.crimesCometidosList = [
      ...this.crimesCometidosList,
      {
        codigoAssunto: Number(codigoAssunto),
        descricaoAssunto: assunto?.descricao,
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

  descricaoJustificativaMpu(idJustificativaInclusaoMpu: number): string {
    return (
      this.justificativaOptions.find(
        o => o.value === Number(idJustificativaInclusaoMpu)
      )?.label || String(idJustificativaInclusaoMpu)
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
      mpus: this.mpusList,
      consequenciasViolencia: this.consequencias,
    };

    const requisicao = this.idFatoOcorrido
      ? this.service.atualizarCompleto(this.idFatoOcorrido, payload)
      : this.service.cadastrarCompleto(payload);

    requisicao.subscribe({
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
