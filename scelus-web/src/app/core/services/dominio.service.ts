import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TjCrudService } from '@tjma/angular-21';
import {
  CepDTO,
  ItemDominio,
} from '../../features/consultar-crimes/consultar-crimes.model';

/**
 * Serviço de consulta das tabelas de domínio do Scelus
 * (tipos de vínculo, polos, justificativas de inclusão de MPU e CEPs).
 */
@Injectable({ providedIn: 'root' })
export class DominioService extends TjCrudService {
  constructor() {
    super('/api/dominios');
  }

  listarTiposVinculo(): Observable<ItemDominio[]> {
    return this._http.get<ItemDominio[]>(
      `${this.backendUrl}/api/dominios/tipos-vinculo`
    );
  }

  listarPolos(): Observable<ItemDominio[]> {
    return this._http.get<ItemDominio[]>(
      `${this.backendUrl}/api/dominios/polos`
    );
  }

  listarJustificativasInclusaoMpu(): Observable<ItemDominio[]> {
    return this._http.get<ItemDominio[]>(
      `${this.backendUrl}/api/dominios/justificativas-inclusao-mpu`
    );
  }

  /** Lista um domínio genérico da whitelist do backend (ocupacoes, drogas, racas-etnias...). */
  listarDominio(chave: string): Observable<ItemDominio[]> {
    return this._http.get<ItemDominio[]>(
      `${this.backendUrl}/api/dominios/${chave}`
    );
  }

  pesquisarCeps(cep: string): Observable<CepDTO[]> {
    const params = new HttpParams().set('cep', cep);
    return this._http.get<CepDTO[]>(`${this.backendUrl}/api/dominios/ceps`, {
      params,
    });
  }
}
