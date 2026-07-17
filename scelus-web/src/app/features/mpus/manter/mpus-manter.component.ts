import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  TjCard,
  TjPage,
  TjInput,
  TjRadio,
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
import { toDateOnly } from '../../../core/utils/date.util';

/**
 * javadoc Tela de cadastro/edição de Medida Protetiva de Urgência (MPU) — CSU008.
 *
 * Cadastro puro de MPU (legislação, datas, concessão etc.) — a vinculação a
 * vítima/acusado passou a ser exclusiva do wizard de Crimes (CSU002), que já
 * a faz automaticamente a partir do contexto da parte em edição.
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

  readonly simNaoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

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
    return true;
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
