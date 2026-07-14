import { Component } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {
  TjInput,
  TjTable,
  TjSelect,
  TjFilters,
  TjRowSelection,
  TjSearchFieldDialog,
  TjSearchFieldDialogContent,
} from '@tjma/angular-21';
import { MpuSeletorService } from './mpu-seletor.service';

/**
 * javadoc Modal seletor de Medidas Protetivas de Urgência (MPU) — CSU008.
 * Aberto via TjSearchField para pesquisa e seleção de uma MPU a vincular ao fato ocorrido.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Component({
  standalone: true,
  selector: 'app-mpu-seletor',
  templateUrl: './mpu-seletor.component.html',
  styleUrl: './mpu-seletor.component.scss',
  providers: [MpuSeletorService],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    TjInput,
    TjSelect,
    TjTable,
    TjFilters,
    TjRowSelection,
    TjSearchFieldDialogContent,
  ],
})
export class MpuSeletorComponent extends TjSearchFieldDialog<MpuSeletorService> {
  columns = {
    id: 'Código',
    numeroMpu: 'Nº da MPU',
    numeroUnico: 'Processo',
    legislacaoFundamento: 'Legislação/Fundamento',
    dataDecisao: 'Data da Decisão',
    concedida: 'Concedida',
  };

  constructor(service: MpuSeletorService) {
    super(service);
  }
}
