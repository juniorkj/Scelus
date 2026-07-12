import { Component, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  TjPage,
  TjCard,
  TjInput,
  TjRadio,
  TjSelect,
  TjTextarea,
  TjIconModule,
  TjCardFooter,
  TjActionPage,
  TjDatePicker,
  TjSearchField,
  TjRadioOption,
  TjButtonModule,
  TjCrudBaseComponent,
  TjPaginaNavegacao,
} from '@tjma/angular-21';
import { finalize } from 'rxjs';
import { ConsultarCrimesService } from '../consultar-crimes.service';
import { ProcessoPjeService } from '../processo-pje.service';
import { DominioService } from '../../../core/services/dominio.service';
import { MpuSeletorComponent } from '../mpu-seletor/mpu-seletor.component';
import {
  CadastroCrimeRequest,
  ItemDominio,
  MpuVinculada,
  ProcessoPje,
} from '../consultar-crimes.model';

/**
 * javadoc Tela de cadastro de Crimes do Processo (CSU002) com importação do PJe,
 * e gestão do vínculo de MPUs ao fato ocorrido (CSU008) no modo edição.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Component({
  standalone: true,
  selector: 'app-consultar-crimes-manter',
  templateUrl: './consultar-crimes-manter.component.html',
  styleUrl: './consultar-crimes-manter.component.scss',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TjPage,
    TjCard,
    TjInput,
    TjRadio,
    TjSelect,
    TjTextarea,
    TjIconModule,
    TjCardFooter,
    TjActionPage,
    TjDatePicker,
    TjSearchField,
    TjButtonModule,
  ],
})
export class ConsultarCrimesManterComponent extends TjCrudBaseComponent<ConsultarCrimesService> {
  private readonly processoPjeService = inject(ProcessoPjeService);
  private readonly dominioService = inject(DominioService);

  readonly simNaoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

  processoPje?: ProcessoPje;
  buscandoPje = false;
  erroPje?: string;

  parteOptions: { label: string; value: number }[] = [];
  poloOptions: { label: string; value: number }[] = [];
  tipoVinculoOptions: { label: string; value: number }[] = [];
  justificativaOptions: { label: string; value: number }[] = [];
  cepOptions: { label: string; value: number }[] = [];
  racaEtniaOptions: { label: string; value: number }[] = [];
  estadoCivilOptions: { label: string; value: number }[] = [];
  religiaoOptions: { label: string; value: number }[] = [];
  escolaridadeOptions: { label: string; value: number }[] = [];
  rendaOptions: { label: string; value: number }[] = [];
  situacaoUsoDrogaOptions: { label: string; value: number }[] = [];
  ocupacaoOptions: { label: string; value: number }[] = [];

  cepBusca = '';
  buscandoCep = false;

  mpusVinculadas: MpuVinculada[] = [];
  mpuSelecionada?: { id: number; numeroMpu?: string };
  vinculoMpu: {
    idJustificativaInclusaoMpu?: number;
    observacaoJustificativa?: string;
  } = {};
  vinculandoMpu = false;

  /** Registra o componente do modal seletor de MPU assim que o campo é renderizado. */
  @ViewChild(TjSearchField)
  set mpuField(field: TjSearchField | undefined) {
    field?.setDialogComponent(MpuSeletorComponent);
  }

  constructor(service: ConsultarCrimesService) {
    super(service);
  }

  get navigation(): TjPaginaNavegacao {
    return {
      grupo: { href: '/crimes', title: 'Consultar Crimes' },
      atual: { href: this._router.url, title: this.title },
    };
  }

  get title(): string {
    if (this.readonly) return 'Visualizar Crime do Processo';
    if (this.actionType === 'UPDATE')
      return 'Crime do Processo — Medidas Protetivas';
    return 'Cadastrar Crime do Processo';
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.carregarDominios();
  }

  /** Carrega as tabelas de domínio utilizadas nos selects do formulário. */
  private carregarDominios(): void {
    this.dominioService.listarPolos().subscribe({
      next: itens => (this.poloOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarTiposVinculo().subscribe({
      next: itens => (this.tipoVinculoOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarJustificativasInclusaoMpu().subscribe({
      next: itens => (this.justificativaOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('racas-etnias').subscribe({
      next: itens => (this.racaEtniaOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('estados-civis').subscribe({
      next: itens => (this.estadoCivilOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('religioes').subscribe({
      next: itens => (this.religiaoOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('escolaridades').subscribe({
      next: itens => (this.escolaridadeOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('rendas').subscribe({
      next: itens => (this.rendaOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('drogas').subscribe({
      next: itens => (this.situacaoUsoDrogaOptions = this.paraOptions(itens)),
    });
    this.dominioService.listarDominio('ocupacoes').subscribe({
      next: itens => (this.ocupacaoOptions = this.paraOptions(itens)),
    });
  }

  private paraOptions(
    itens: ItemDominio[]
  ): { label: string; value: number }[] {
    return itens.map(item => ({ label: item.descricao, value: item.id }));
  }

  /** Consulta o processo e as partes no PJe pelo número informado (CSU002 — passo 1). */
  buscarProcessoPje(): void {
    const numero = (this.entity.numeroProcesso || '').trim();
    if (!numero) return;

    this.buscandoPje = true;
    this.erroPje = undefined;
    this.processoPje = undefined;
    this.parteOptions = [];

    this.processoPjeService.consultarPorNumero(numero)
      .pipe(
        finalize(() => {
          this.buscandoPje = false;
          this._cd.detectChanges();
        })
      )
      .subscribe({
        next: processo => {
          this.processoPje = processo;
          this.parteOptions = (processo.partes || []).map(parte => ({
            label: `${parte.nome}${parte.cpfCnpj ? ' — ' + parte.cpfCnpj : ''} (${parte.polo})`,
            value: parte.idParte,
          }));
        },
        error: erro => {
          this.erroPje =
            erro?.error?.message ||
            'Não foi possível localizar o processo no PJe.';
        },
      });
  }

  /** Pré-seleciona o polo processual conforme a parte escolhida no PJe (A→ATIVO, P→PASSIVO). */
  onParteSelecionada(alvo: 'vitima' | 'acusado', idParte: number): void {
    const parte = this.processoPje?.partes?.find(
      p => p.idParte === Number(idParte)
    );
    if (!parte?.polo) return;

    const descricao = parte.polo === 'A' ? 'ATIVO' : 'PASSIVO';
    const opcao = this.poloOptions.find(o =>
      o.label.toUpperCase().startsWith(descricao)
    );
    if (!opcao) return;

    if (alvo === 'vitima') {
      this.entity.idPoloVitima = opcao.value;
    } else {
      this.entity.idPoloAcusado = opcao.value;
    }
  }

  /**
   * Pesquisa CEPs para popular os selects de endereço.
   * CEP completo (8 dígitos) inexistente na base é consultado no ViaCEP e
   * registrado automaticamente pelo backend.
   */
  buscarCeps(): void {
    const prefixo = (this.cepBusca || '').replace(/\D/g, '');
    if (!prefixo) return;

    this.buscandoCep = true;
    this.dominioService.pesquisarCeps(prefixo)
      .pipe(
        finalize(() => {
          this.buscandoCep = false;
          this._cd.detectChanges();
        })
      )
      .subscribe({
        next: ceps => {
          this.cepOptions = ceps.map(cep => ({
            label: `${cep.cep} — ${cep.logradouro || ''} ${cep.municipio || ''}/${cep.uf || ''}`,
            value: cep.id,
          }));
          // Resultado único: pré-seleciona os campos de CEP ainda vazios.
          if (ceps.length === 1) {
            this.entity.idCepFato = this.entity.idCepFato ?? ceps[0].id;
            this.entity.idCepVitima = this.entity.idCepVitima ?? ceps[0].id;
          }
        },
        error: () => {},
      });
  }

  /** Monta o payload do CSU002 antes do POST em /api/crimes. */
  override beforeCreate(): boolean {
    if (!this.processoPje) {
      this.erroPje = 'Busque e selecione um processo do PJe antes de salvar.';
      return false;
    }

    const dados = this.entity;
    const payload: CadastroCrimeRequest = {
      numeroProcesso: this.processoPje.numeroUnico,
      codigoAssunto: Number(dados.codigoAssunto),
      dataFato: this.normalizarDataHora(dados.dataFato),
      idCep: Number(dados.idCepFato),
      medidaProtetiva: dados.medidaProtetiva,
      vitima: {
        idParte: Number(dados.idParteVitima),
        idPolo: Number(dados.idPoloVitima),
        idCep: Number(dados.idCepVitima),
        idRacaEtnia: dados.idRacaEtniaVitima ? Number(dados.idRacaEtniaVitima) : undefined,
        idEstadoCivil: dados.idEstadoCivilVitima ? Number(dados.idEstadoCivilVitima) : undefined,
        idReligiao: dados.idReligiaoVitima ? Number(dados.idReligiaoVitima) : undefined,
        idEscolaridade: dados.idEscolaridadeVitima ? Number(dados.idEscolaridadeVitima) : undefined,
        idRenda: dados.idRendaVitima ? Number(dados.idRendaVitima) : undefined,
        idSituacaoUsoDroga: dados.idSituacaoUsoDrogaVitima ? Number(dados.idSituacaoUsoDrogaVitima) : undefined,
        idOcupacao: dados.idOcupacaoVitima ? Number(dados.idOcupacaoVitima) : undefined,
      },
      acusado: {
        idParte: Number(dados.idParteAcusado),
        idPolo: Number(dados.idPoloAcusado),
        possuiAntecedentes: dados.possuiAntecedentes === 'S' ? 1 : 0,
        reincidente: dados.reincidente === 'S' ? 1 : 0,
        observacaoAntecedentes: dados.observacaoAntecedentes,
        idRacaEtnia: dados.idRacaEtniaAcusado ? Number(dados.idRacaEtniaAcusado) : undefined,
        idEstadoCivil: dados.idEstadoCivilAcusado ? Number(dados.idEstadoCivilAcusado) : undefined,
        idReligiao: dados.idReligiaoAcusado ? Number(dados.idReligiaoAcusado) : undefined,
        idEscolaridade: dados.idEscolaridadeAcusado ? Number(dados.idEscolaridadeAcusado) : undefined,
        idRenda: dados.idRendaAcusado ? Number(dados.idRendaAcusado) : undefined,
        idSituacaoUsoDroga: dados.idSituacaoUsoDrogaAcusado ? Number(dados.idSituacaoUsoDrogaAcusado) : undefined,
        idOcupacao: dados.idOcupacaoAcusado ? Number(dados.idOcupacaoAcusado) : undefined,
      },
      idTipoVinculo: dados.idTipoVinculo
        ? Number(dados.idTipoVinculo)
        : undefined,
      observacaoVinculo: dados.observacaoVinculo,
    };

    // Mescla (não substitui) para não esvaziar os campos ligados ao entity via ngModel —
    // substituir o objeto invalidava o formulário na tela mesmo com o POST bem-sucedido.
    this.entity = { ...this.entity, ...payload };
    return true;
  }

  /** Após carregar o fato (modo edição), prepara o formulário e lista as MPUs (CSU008). */
  override afterRetrieve(): boolean {
    if (this.entity?.dataFato) {
      // tj-date-picker espera 'yyyy-MM-dd'
      this.entity.dataFato = String(this.entity.dataFato).substring(0, 10);
    }
    if (this.entity?.idCep) {
      // Pré-carrega o CEP atual como opção do select
      this.cepOptions = [
        {
          label: this.entity.cepFato || `CEP ${this.entity.idCep}`,
          value: this.entity.idCep,
        },
      ];
    }
    this.carregarMpusVinculadas();
    return true;
  }

  /** Monta o payload de alteração (CSU002) antes do PUT em /api/crimes/{id}. */
  override beforeUpdate(): boolean {
    const dados = this.entity;
    this.entity = {
      ...dados,
      dataFato: this.normalizarDataHora(dados.dataFato),
      idCep: Number(dados.idCep),
      medidaProtetiva: dados.medidaProtetiva,
      idTipoVinculo: dados.idTipoVinculo
        ? Number(dados.idTipoVinculo)
        : undefined,
      observacaoVinculo: dados.observacaoVinculo,
    };
    return true;
  }

  carregarMpusVinculadas(): void {
    if (!this.entityId || this.entityId === 'new') return;
    this.service.listarMpusDoFato(this.entityId).subscribe({
      next: mpus => {
        this.mpusVinculadas = mpus;
        this._cd.detectChanges();
      },
    });
  }

  /** Vincula a MPU selecionada no modal ao fato ocorrido, com justificativa obrigatória. */
  vincularMpu(): void {
    const idMpu = this.mpuSelecionada?.id;
    const idJustificativa = this.vinculoMpu.idJustificativaInclusaoMpu;
    if (!idMpu || !idJustificativa) return;

    this.vinculandoMpu = true;
    this.service
      .vincularMpu(this.entityId, {
        idMpu: Number(idMpu),
        idJustificativaInclusaoMpu: Number(idJustificativa),
        observacaoJustificativa: this.vinculoMpu.observacaoJustificativa,
      })
      .subscribe({
        next: () => {
          this.vinculandoMpu = false;
          this.mpuSelecionada = undefined;
          this.vinculoMpu = {};
          this.carregarMpusVinculadas();
        },
        error: () => {
          this.vinculandoMpu = false;
          this._cd.detectChanges();
        },
      });
  }

  /** Remove o vínculo de MPU do fato ocorrido. */
  removerVinculoMpu(vinculo: MpuVinculada): void {
    if (
      !confirm(
        `Remover o vínculo da MPU ${vinculo.numeroMpu || vinculo.idMpu}?`
      )
    ) {
      return;
    }
    this.service.removerVinculoMpu(this.entityId, vinculo.id).subscribe({
      next: () => this.carregarMpusVinculadas(),
    });
  }

  get podeVincularMpu(): boolean {
    return (
      !!this.mpuSelecionada?.id &&
      !!this.vinculoMpu.idJustificativaInclusaoMpu &&
      !this.vinculandoMpu
    );
  }

  /** Converte o valor do date-picker para o formato ISO aceito pelo backend (LocalDateTime). */
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
