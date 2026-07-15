import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  TjCard,
  TjPage,
  TjInput,
  TjRadio,
  TjSelect,
  TjTextarea,
  TjIconModule,
  TjCardFooter,
  TjActionPage,
  TjDatePicker,
  TjRadioOption,
  TjButtonModule,
  TjCrudBaseComponent,
  TjPaginaNavegacao,
} from '@tjma/angular-21';
import { MpusService } from '../mpus.service';
import { ProcessoPjeService } from '../../consultar-crimes/processo-pje.service';
import { ProcessoPje } from '../../consultar-crimes/consultar-crimes.model';
import { toDateOnly } from '../../../core/utils/date.util';

type Opcao = { label: string; value: number };

/**
 * javadoc Tela de cadastro/edição de Medida Protetiva de Urgência (MPU) — CSU008.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Component({
  selector: 'app-mpus-manter',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TjCard,
    TjPage,
    TjInput,
    TjRadio,
    TjSelect,
    TjTextarea,
    TjIconModule,
    TjCardFooter,
    TjActionPage,
    TjDatePicker,
    TjButtonModule,
  ],
  templateUrl: './mpus-manter.component.html',
  styleUrl: './mpus-manter.component.scss',
})
export class MpusManterComponent extends TjCrudBaseComponent<MpusService> {
  private static readonly CAMPOS_DATA = [
    'dataDecisao',
    'dataIntimacaoAcusado',
    'dataIntimacaoVitima',
    'dataCienciaVitima',
    'dataCienciaAcusado',
  ];

  private readonly processoPjeService = inject(ProcessoPjeService);

  readonly simNaoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

  /**
   * Vítima/Acusado (CSU008): a MPU só fica visível na busca automática por
   * par (RN008.02) se estiver vinculada a uma vítima/acusado que já existe no
   * Scelus — o que só ocorre depois que um fato ocorrido foi cadastrado para
   * o processo (ver CSU008, pré-condição e RE02/RE03). Estes campos buscam
   * essas litigâncias já existentes para o processo informado, em vez de
   * deixar a MPU "órfã" (sem vínculo, invisível para a busca por par).
   */
  buscandoVinculos = false;
  vinculosBuscados = false;
  vitimaOptions: Opcao[] = [];
  acusadoOptions: Opcao[] = [];
  erroVinculos?: string;

  constructor(service: MpusService) {
    super(service);
  }

  get navigation(): TjPaginaNavegacao {
    return {
      grupo: { href: '/mpus', title: 'Medidas Protetivas' },
      atual: { href: this._router.url, title: this.title },
    };
  }

  get title(): string {
    if (this.readonly) return 'Visualizar Medida Protetiva';
    if (this.actionType === 'UPDATE') return 'Editar Medida Protetiva';
    return 'Cadastrar Medida Protetiva';
  }

  /** Converte as datas ISO do backend para o formato do tj-date-picker. */
  override afterRetrieve(): boolean {
    for (const campo of MpusManterComponent.CAMPOS_DATA) {
      this.entity[campo] = toDateOnly(this.entity?.[campo]);
    }
    if (this.entity?.numeroUnico) {
      this.buscarVinculosDisponiveis();
    }
    return true;
  }

  /**
   * Busca vítimas/acusados já cadastrados no Scelus para o número de processo
   * informado, para permitir vincular a MPU a um par existente (em vez de
   * salvá-la sem vínculo, o que a torna invisível para a busca por par —
   * RN008.02). Cruza os ids retornados pelo backend com os nomes das partes
   * no PJe (mesmo serviço usado no wizard de cadastro de crimes).
   */
  buscarVinculosDisponiveis(): void {
    const numeroUnico = (this.entity?.numeroUnico || '').trim();
    if (!numeroUnico) return;

    this.buscandoVinculos = true;
    this.vitimaOptions = [];
    this.acusadoOptions = [];
    this.erroVinculos = undefined;

    forkJoin({
      // Busca no PJe é só para exibir o nome da parte no label do select — se
      // falhar (dblink lento/instável), não pode derrubar a busca de vínculos
      // já existentes no Scelus, que é o dado que realmente importa aqui.
      processo: this.processoPjeService.consultarPorNumero(numeroUnico).pipe(
        catchError(() =>
          of<ProcessoPje>({
            idProcesso: 0,
            numeroUnico,
            possuiSentenca: 'N',
            partes: [],
            assuntos: [],
          })
        )
      ),
      vinculos: this.service.buscarVinculosDisponiveis(numeroUnico),
    }).subscribe({
      next: ({ processo, vinculos }) => {
        const nomesPorParte = new Map(
          (processo.partes || []).map(p => [p.idParte, p.nome])
        );
        this.vitimaOptions = vinculos
          .filter(v => v.idVitima != null)
          .map(v => ({
            label:
              nomesPorParte.get(v.idParte) || `Vítima (parte ${v.idParte})`,
            value: v.idVitima!,
          }));
        this.acusadoOptions = vinculos
          .filter(v => v.idAcusado != null)
          .map(v => ({
            label:
              nomesPorParte.get(v.idParte) || `Acusado (parte ${v.idParte})`,
            value: v.idAcusado!,
          }));
        this.vinculosBuscados = true;
        this.buscandoVinculos = false;
      },
      error: erro => {
        this.vinculosBuscados = true;
        this.buscandoVinculos = false;
        this.erroVinculos =
          erro?.error?.detail ||
          erro?.error?.message ||
          `Não foi possível buscar os vínculos (HTTP ${erro?.status ?? '?'}).`;
      },
    });
  }

  override beforeCreate(): boolean {
    this.normalizarDatas();
    return true;
  }

  override beforeUpdate(): boolean {
    this.normalizarDatas();
    return true;
  }

  /** Converte os valores dos date-pickers para LocalDateTime ISO aceito pelo backend. */
  private normalizarDatas(): void {
    for (const campo of MpusManterComponent.CAMPOS_DATA) {
      const valor = this.entity?.[campo];
      if (!valor) {
        this.entity[campo] = null;
        continue;
      }
      if (valor instanceof Date) {
        const ano = valor.getFullYear();
        const mes = String(valor.getMonth() + 1).padStart(2, '0');
        const dia = String(valor.getDate()).padStart(2, '0');
        this.entity[campo] = `${ano}-${mes}-${dia}T00:00:00`;
      } else {
        const texto = String(valor);
        this.entity[campo] = texto.includes('T') ? texto : `${texto}T00:00:00`;
      }
    }
  }
}
