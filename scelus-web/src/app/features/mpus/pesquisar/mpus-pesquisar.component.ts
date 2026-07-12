import {
  Component,
  ChangeDetectionStrategy,
  computed,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import {
  TjInput,
  TjSelect,
  TjFilters,
  TjIconModule,
  TjPageModule,
  TjTableModule,
  TjCrudBaseComponent,
  TjPaginaNavegacao,
} from '@tjma/angular-21';
import { MpusService } from '../mpus.service';

/**
 * javadoc Tela de pesquisa de Medidas Protetivas de Urgência (MPU) — CSU008.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Component({
  selector: 'app-mpus-pesquisar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    TjInput,
    TjSelect,
    TjFilters,
    TjIconModule,
    TjPageModule,
    TjTableModule,
  ],
  templateUrl: './mpus-pesquisar.component.html',
  styleUrl: './mpus-pesquisar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MpusPesquisarComponent extends TjCrudBaseComponent<MpusService> {
  readonly columns = signal({
    numeroMpu: 'Nº da MPU',
    numeroUnico: 'Processo',
    legislacaoFundamento: 'Legislação/Fundamento',
    dataDecisao: 'Data da Decisão',
    concedida: 'Concedida',
    actions: 'Ações',
  });

  readonly navigation = computed<TjPaginaNavegacao>(() => ({
    atual: { href: '/mpus', title: 'Medidas Protetivas de Urgência' },
    grupo: { href: '/', title: 'Scelus' },
  }));

  constructor(service: MpusService) {
    super(service);
  }
}
