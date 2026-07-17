import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import {
  MpuDTO,
  MpuVinculoOutroProcesso,
} from '../consultar-crimes/consultar-crimes.model';

@Injectable({ providedIn: 'root' })
export class MpusService extends TjCrudService {
  constructor() {
    super('/api/mpus');
  }

  /** Busca global de MPUs pelo par vítima-acusado, via partes do PJe (RN008.02). */
  buscarPorPar(
    idParteVitima: number,
    idParteAcusado: number
  ): Observable<MpuDTO[]> {
    return this._http.get<MpuDTO[]>(`${this.backendUrl}/api/mpus/par`, {
      params: { idParteVitima, idParteAcusado },
    });
  }

  /**
   * MPUs já vinculadas à vítima e/ou ao acusado em OUTROS processos/fatos
   * ocorridos (RN008.02) — usado no passo Fato Ocorrido do CSU002 para
   * alertar sobre vínculos preexistentes.
   */
  buscarVinculosDeOutrosProcessos(
    idParteVitima: number,
    idParteAcusado: number,
    idFatoOcorridoAtual?: number
  ): Observable<MpuVinculoOutroProcesso[]> {
    return this._http.get<MpuVinculoOutroProcesso[]>(
      `${this.backendUrl}/api/mpus/por-parte`,
      {
        params: {
          idParteVitima,
          idParteAcusado,
          ...(idFatoOcorridoAtual ? { idFatoOcorridoAtual } : {}),
        },
      }
    );
  }
}
