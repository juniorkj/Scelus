import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import {
  CadastroCrimeCompletoRequest,
  CadastroCrimeCompletoResponse,
  CrimeCompletoDetalhe,
  MpuVinculada,
  VinculoMpuRequest,
} from './consultar-crimes.model';

@Injectable({ providedIn: 'root' })
export class ConsultarCrimesService extends TjCrudService {
  constructor() {
    super('/api/crimes');
  }

  /** Envia o wizard completo do CSU002 (persistência diferida — RN01). */
  cadastrarCompleto(
    request: CadastroCrimeCompletoRequest
  ): Observable<CadastroCrimeCompletoResponse> {
    return this._http.post<CadastroCrimeCompletoResponse>(
      `${this.backendUrl}/api/crimes/completo`,
      request
    );
  }

  /** Busca o detalhe completo de um crime/fato ocorrido (edição/visualização). */
  buscarCompleto(
    idFatoOcorrido: number | string
  ): Observable<CrimeCompletoDetalhe> {
    return this._http.get<CrimeCompletoDetalhe>(
      `${this.backendUrl}/api/crimes/${idFatoOcorrido}/completo`
    );
  }

  /** Atualiza o wizard completo do CSU002 (edição — RN01). */
  atualizarCompleto(
    idFatoOcorrido: number | string,
    request: CadastroCrimeCompletoRequest
  ): Observable<CadastroCrimeCompletoResponse> {
    return this._http.put<CadastroCrimeCompletoResponse>(
      `${this.backendUrl}/api/crimes/${idFatoOcorrido}/completo`,
      request
    );
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
