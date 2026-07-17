import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import {
  TjInput,
  TjSelect,
  TjFilters,
  TjIconModule,
  TjTableModule,
  TjSearchFieldDialog,
  TjSearchFieldDialogContent,
} from '@tjma/angular-21';
import { MpusService } from '../mpus.service';

/**
 * javadoc Busca ampla de MPUs já cadastradas na base (por número, processo ou
 * concessão), com seleção múltipla — reaproveita `TjSearchFieldDialog` /
 * `TjSearchFieldDialogContent` (o mesmo mecanismo de modal de busca+seleção
 * usado pelo `tj-search-field` da infra), aberto a partir do passo "Fato
 * Ocorrido" do wizard de Crimes (CSU002), para não restringir a busca só às
 * MPUs já vinculadas ao par vítima-acusado atual (RN008.02).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 17/07/2026
 */
@Component({
  selector: 'app-mpu-selecao-modal',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    TjInput,
    TjSelect,
    TjFilters,
    TjIconModule,
    TjTableModule,
    TjSearchFieldDialogContent,
  ],
  templateUrl: './mpu-selecao-modal.component.html',
})
export class MpuSelecaoModalComponent extends TjSearchFieldDialog<MpusService> {
  readonly concedidaOptions = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

  readonly columns = {
    numeroMpu: 'Nº da MPU',
    numeroUnico: 'Processo',
    dataDecisao: 'Data da Decisão',
    concedida: 'Concedida',
  };

  constructor(service: MpusService) {
    super(service);
  }
}
