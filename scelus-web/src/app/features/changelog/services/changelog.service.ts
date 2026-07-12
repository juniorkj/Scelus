import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChangeLogService {
  private readonly apiUrl = environment.apiUrl + '/changelog';
  private readonly http = inject(HttpClient);

  listarTodos(): Observable<string> {
    return this.http.get(this.apiUrl, { responseType: 'text' });
  }
}
