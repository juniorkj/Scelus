import { Injectable } from '@angular/core';
import { TjCrudService } from '@tjma/angular-21';

@Injectable({ providedIn: 'root' })
export class MpusService extends TjCrudService {
  constructor() {
    super('/api/mpus');
  }
}
