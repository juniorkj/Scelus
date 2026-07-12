import { Injectable } from '@angular/core';
import { TjCrudService } from '@tjma/angular-21';

@Injectable()
export class MpuSeletorService extends TjCrudService {
  constructor() {
    super('/api/mpus');
  }
}
