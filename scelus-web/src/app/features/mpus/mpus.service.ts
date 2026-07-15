import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import {
  MpuDTO,
  VinculoDisponivelMpu,
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

  /** Vítimas/acusados já cadastrados no Scelus para o processo, disponíveis para vincular a uma MPU avulsa. */
  buscarVinculosDisponiveis(
    numeroUnico: string
  ): Observable<VinculoDisponivelMpu[]> {
    return this._http.get<VinculoDisponivelMpu[]>(
      `${this.backendUrl}/api/mpus/vinculos-disponiveis`,
      { params: { numeroUnico } }
    );
  }
}
