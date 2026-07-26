import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import {
  TjInput,
  TjRadio,
  TjTextarea,
  TjIconModule,
  TjDatePicker,
  TjRadioOption,
  TjButtonModule,
} from '@tjma/angular-21';
import { CadastroMpuDados } from '../../consultar-crimes/consultar-crimes.model';

/**
 * javadoc Formulário de cadastro de uma nova MPU (dados próprios da medida —
 * legislação, datas, concessão etc.), reaproveitado como modal a partir do
 * passo "Fato Ocorrido" do wizard de Crimes (CSU002). Não inclui vínculo com
 * vítima/acusado — quem abre o modal decide se/quando persistir e vincular
 * (crime novo: só fica em memória até o "Finalizar"; crime já salvo: grava e
 * vincula de imediato).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 17/07/2026
 */
@Component({
  selector: 'app-mpu-cadastro-modal',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TjInput,
    TjRadio,
    TjTextarea,
    TjIconModule,
    TjDatePicker,
    TjButtonModule,
  ],
  templateUrl: './mpu-cadastro-modal.component.html',
})
export class MpuCadastroModalComponent {
  private readonly dialogRef = inject(MatDialogRef<MpuCadastroModalComponent>);

  readonly simNaoOptions: TjRadioOption[] = [
    { label: 'Sim', value: 'S' },
    { label: 'Não', value: 'N' },
  ];

  mpu: CadastroMpuDados = {
    numeroMpu: '',
    numeroUnico: '',
    dataDecisao: '',
    concedida: '',
    legislacaoFundamento: '',
    dataIntimacaoAcusado: '',
    dataIntimacaoVitima: '',
    dataCienciaAcusado: '',
    dataCienciaVitima: '',
    pedidoDesistencia: '',
    inqueritoInstaurado: '',
    observacoes: '',
    dataFimVigencia: '',
  };

  get podeSalvar(): boolean {
    const m = this.mpu;
    return !!(
      m.numeroMpu &&
      m.legislacaoFundamento &&
      m.dataDecisao &&
      m.concedida &&
      m.dataIntimacaoAcusado &&
      m.dataIntimacaoVitima &&
      m.pedidoDesistencia &&
      m.inqueritoInstaurado
    );
  }

  cancelar(): void {
    this.dialogRef.close();
  }

  salvar(): void {
    if (!this.podeSalvar) return;
    this.dialogRef.close(this.mpu);
  }
}
