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
  TjCrudBaseComponent,
  TjPaginaNavegacao,
} from '@tjma/angular-21';
import { MpusService } from '../mpus.service';

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
    TjTextarea,
    TjIconModule,
    TjCardFooter,
    TjActionPage,
    TjDatePicker,
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
      if (this.entity?.[campo]) {
        this.entity[campo] = String(this.entity[campo]).substring(0, 10);
      }
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
