import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import { ProcessoPje } from './consultar-crimes.model';

@Injectable({ providedIn: 'root' })
export class ProcessoPjeService extends TjCrudService {
  constructor() {
    super('/api/processos-pje');
  }

  /** Consulta o processo e suas partes no PJe pelo número único formatado (CNJ). */
  consultarPorNumero(numero: string): Observable<ProcessoPje> {
    const params = new HttpParams().set('numero', numero);
    return this._http.get<ProcessoPje>(`${this.backendUrl}/api/processos-pje`, {
      params,
    });
  }
}
