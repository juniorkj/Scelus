import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import { MpuVinculada, VinculoMpuRequest } from './consultar-crimes.model';

@Injectable({ providedIn: 'root' })
export class ConsultarCrimesService extends TjCrudService {
  constructor() {
    super('/api/crimes');
  }

  /** Lista as MPUs vinculadas ao fato ocorrido (CSU008). */
  listarMpusDoFato(
    idFatoOcorrido: number | string
  ): Observable<MpuVinculada[]> {
    return this._http.get<MpuVinculada[]>(
      `${this.backendUrl}/api/crimes/${idFatoOcorrido}/mpus`
    );
  }

  /** Vincula uma MPU ao fato ocorrido com justificativa obrigatória (CSU008). */
  vincularMpu(
    idFatoOcorrido: number | string,
    request: VinculoMpuRequest
  ): Observable<{ idFatoOcorridoMpu: number; mensagem: string }> {
    return this._http.post<{ idFatoOcorridoMpu: number; mensagem: string }>(
      `${this.backendUrl}/api/crimes/${idFatoOcorrido}/mpus`,
      request
    );
  }

  /** Remove o vínculo de MPU do fato ocorrido. */
  removerVinculoMpu(
    idFatoOcorrido: number | string,
    idVinculo: number
  ): Observable<{ mensagem: string }> {
    return this._http.delete<{ mensagem: string }>(
      `${this.backendUrl}/api/crimes/${idFatoOcorrido}/mpus/${idVinculo}`
    );
  }
}
