import 'reflect-metadata';
import 'zone.js';
import 'zone.js/testing';
import '@analogjs/vitest-angular/setup-zone';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting,
} from '@angular/platform-browser-dynamic/testing';
import { vi } from 'vitest';
import {
  Component,
  Input,
  forwardRef,
  NgModule,
  Directive,
  HostBinding,
  InjectionToken,
  input,
} from '@angular/core';
import {
  NG_VALUE_ACCESSOR,
  ControlValueAccessor,
  FormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';

console.log('--- TEST SETUP EXECUTING ---');
// Initialize the Angular testing environment.
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

// --- TOKENS ---
export const APP_ENV = new InjectionToken<any>('APP_ENV');
export const BACKEND_URL = new InjectionToken<string>('BACKEND_URL');

// --- STUBS INFRASTRUCTURE ---

@Component({
  selector: 'tj-input',
  template:
    '<div class="tj-input-stub"><label>{{label}}</label><ng-content></ng-content></div>',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TjInputStub),
      multi: true,
    },
  ],
})
export class TjInputStub implements ControlValueAccessor {
  readonly label = input('');
  readonly readOnly = input<any>(false);
  readonly readonly = input<any>(false);
  readonly placeholder = input('');
  writeValue(obj: any): void {}
  registerOnChange(fn: any): void {}
  registerOnTouched(fn: any): void {}
  setDisabledState?(isDisabled: boolean): void {}
}

@Component({
  selector: 'tj-select',
  template:
    '<div class="tj-select-stub"><label>{{label}}</label><ng-content></ng-content></div>',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TjSelectStub),
      multi: true,
    },
  ],
})
export class TjSelectStub implements ControlValueAccessor {
  readonly label = input('');
  readonly readOnly = input<any>(false);
  readonly readonly = input<any>(false);
  readonly client = input<any>(false);
  readonly placeholder = input('');
  readonly options = input<any[]>([]);
  writeValue(obj: any): void {}
  registerOnChange(fn: any): void {}
  registerOnTouched(fn: any): void {}
  setDisabledState?(isDisabled: boolean): void {}
}

@Component({
  selector: 'tj-textarea',
  template:
    '<div class="tj-textarea-stub"><label>{{label}}</label><ng-content></ng-content></div>',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TjTextAreaStub),
      multi: true,
    },
  ],
})
export class TjTextAreaStub implements ControlValueAccessor {
  @Input() label = '';
  @Input() readOnly: any = false;
  @Input() readonly: any = false;
  @Input() placeholder = '';
  @Input() rows: any;
  writeValue(obj: any): void {}
  registerOnChange(fn: any): void {}
  registerOnTouched(fn: any): void {}
  setDisabledState?(isDisabled: boolean): void {}
}

@Component({
  selector: 'tj-icon',
  template: '<i [attr.icon]="icon" [class]="\'tj-icon-\' + icon"></i>',
  standalone: true,
})
export class TjIconStub {
  @Input() @HostBinding('attr.icon') icon = '';
  @Input() size: any;
  @Input() color: any;
}

@Component({
  selector: 'tj-page',
  template: `
    <section
      class="tj-page-stub"
      [attr.back]="back ? 'true' : 'false'"
      [attr.title]="title">
      <div class="tj-page-nav-mock" *ngIf="navegacao?.grupo?.icon">
        <tj-icon [icon]="navegacao.grupo.icon"></tj-icon>
      </div>
      <ng-content></ng-content>
    </section>
  `,
  standalone: true,
  imports: [CommonModule, forwardRef(() => TjIconStub)],
})
export class TjPageStub {
  @Input() @HostBinding('attr.title') title = '';
  @Input() @HostBinding('attr.back') back: any = false;
  @Input() breadcrumbs: any[] = [];
  readonly navegacao = input<any>();
}

@Component({
  selector: 'tj-card',
  template:
    '<div class="tj-card-stub" [attr.nonExpansive]="nonExpansive"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjCardStub {
  @Input() nonExpansive: any;
}

@Component({
  selector: 'tj-card-footer',
  template: '<div class="tj-card-footer-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjCardFooterStub {}

@Component({
  selector: 'tj-card-header',
  template: '<div class="tj-card-header-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjCardHeaderStub {}

@Component({
  selector: 'tj-card-header-title',
  template:
    '<div class="tj-card-header-title-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjCardHeaderTitleStub {}

@Component({
  selector: 'tj-card-header-description',
  template:
    '<div class="tj-card-header-description-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjCardHeaderDescriptionStub {}

@Component({
  selector: 'tj-acao-pagina',
  template: `
    <div class="tj-acao-pagina-stub">
      <button mat-raised-button class="tj-button primary" color="primary">
        <tj-icon icon="CircleCheck"></tj-icon> Salvar
      </button>
      <button mat-raised-button class="tj-button secondary">
        <tj-icon icon="XCircle"></tj-icon> Cancelar
      </button>
      <ng-content></ng-content>
    </div>
  `,
  standalone: true,
  imports: [TjIconStub, forwardRef(() => MatButtonStub)],
})
export class TjAcaoPaginaStub {
  @Input() client: any;
  @Input() esconderExcluir: any;
}

@Component({
  selector: 'tj-filters',
  template: '<div class="tj-filters-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjFiltersStub {
  @Input() client: any;
  @Input() esconderBotoes: any;
}

@Component({
  selector: 'tj-table',
  template: `
    <div class="tj-table-stub">
      <div class="tj-table-header">
        <div class="tj-table-actions" tjTableActions>
          <button class="tj-button primary">
            <tj-icon icon="Plus"></tj-icon> Adicionar
          </button>
          <ng-content select="[tjTableActions]"></ng-content>
        </div>
      </div>
      <ng-content></ng-content>
    </div>
  `,
  standalone: true,
  imports: [forwardRef(() => TjIconStub)],
})
export class TjTableStub {
  @Input() @HostBinding('attr.title') title = '';
  @Input() description = '';
  @Input() data: any[] = [];
  @Input() dataSource: any = { result: [] };
  @Input() columns: any = {};
  @Input() client: any;
  @Input() hideExportPDF: any;
  @Input() hideExportXLS: any;
  get columnKeys() {
    return this.columns ? Object.keys(this.columns) : [];
  }
}

@Directive({
  selector: '[tjTableActions]',
  standalone: true,
})
export class TjTableActionsStub {}

@Component({
  selector: 'tj-loading',
  template: '<div class="tj-loading-stub"><ng-content></ng-content></div>',
  standalone: true,
})
export class TjLoadingStub {
  @Input() loading = false;
}

@Component({
  selector: 'mat-card',
  template: '<div class="mat-card-stub"><ng-content></ng-content></div>',
  standalone: true,
  host: {
    '[class]': 'hostClass',
  },
})
export class MatCardStub {
  @Input('class') hostClass = '';
}

@Directive({
  selector: '[mat-button],[mat-raised-button],[mat-icon-button]',
  standalone: true,
  host: {
    '[class]': 'hostClass',
  },
})
export class MatButtonStub {
  @Input('class') hostClass = '';
  @Input() color: any;
  @Input() disabled: any;
}

@Component({
  selector: 'mat-icon',
  template: '<span><ng-content></ng-content></span>',
  standalone: true,
})
export class MatIconStub {}

@Component({
  selector: 'lucide-angular',
  template: '<i [attr.name]="name"></i>',
  standalone: true,
})
export class LucideStub {
  @Input() name = '';
}

@Component({
  selector: 'tj-date-picker',
  template:
    '<div class="tj-date-picker-stub"><label>{{label}}</label><input [ngModel]="value"></div>',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TjDatePickerStub),
      multi: true,
    },
  ],
  imports: [FormsModule],
})
export class TjDatePickerStub implements ControlValueAccessor {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() name = '';
  @Input() readOnly: any = false;
  value: any;
  writeValue(obj: any): void {
    this.value = obj;
  }
  registerOnChange(fn: any): void {}
  registerOnTouched(fn: any): void {}
}

@NgModule({
  imports: [
    TjPageStub,
    TjAcaoPaginaStub,
    TjFiltersStub,
    TjTableStub,
    TjTableActionsStub,
    TjInputStub,
    TjSelectStub,
    TjTextAreaStub,
    TjDatePickerStub,
    TjLoadingStub,
    TjCardHeaderStub,
    TjCardHeaderTitleStub,
    TjCardHeaderDescriptionStub,
  ],
  exports: [
    TjPageStub,
    TjAcaoPaginaStub,
    TjFiltersStub,
    TjTableStub,
    TjTableActionsStub,
    TjInputStub,
    TjSelectStub,
    TjTextAreaStub,
    TjDatePickerStub,
    TjLoadingStub,
    TjCardHeaderStub,
    TjCardHeaderTitleStub,
    TjCardHeaderDescriptionStub,
  ],
})
export class TjPageModuleStub {}

@NgModule({
  imports: [TjInputStub, TjSelectStub, TjTextAreaStub, TjDatePickerStub],
  exports: [TjInputStub, TjSelectStub, TjTextAreaStub, TjDatePickerStub],
})
export class TjFormModuleStub {}

@NgModule({
  imports: [TjIconStub],
  exports: [TjIconStub],
})
export class TjIconModuleStub {}

@NgModule({
  imports: [
    TjCardStub,
    TjCardFooterStub,
    TjCardHeaderStub,
    TjCardHeaderTitleStub,
    TjCardHeaderDescriptionStub,
  ],
  exports: [
    TjCardStub,
    TjCardFooterStub,
    TjCardHeaderStub,
    TjCardHeaderTitleStub,
    TjCardHeaderDescriptionStub,
  ],
})
export class TjCardModuleStub {}

@NgModule({
  imports: [TjTableStub, TjTableActionsStub],
  exports: [TjTableStub, TjTableActionsStub],
})
export class TjTableModuleStub {}

@NgModule({
  exports: [],
})
class TjButtonModuleStub {}

// --- VITEST MOCKS ---

vi.mock('@tjma/angular-21', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return {
    ...actual,
    TjPageModule: TjPageModuleStub,
    TjFormModule: TjFormModuleStub,
    TjIconModule: TjIconModuleStub,
    TjCardModule: TjCardModuleStub,
    TjTableModule: TjTableModuleStub,
    TjSelectModule: TjFormModuleStub,
    TjInputModule: TjFormModuleStub,
    TjButtonModule: TjButtonModuleStub,
    // Sobrescrevendo componentes reais com stubs para evitar NG0300
    TjPage: TjPageStub,
    TjInput: TjInputStub,
    TjSelect: TjSelectStub,
    TjTextArea: TjTextAreaStub,
    TjDatePicker: TjDatePickerStub,
    TjFilters: TjFiltersStub,
    TjIcon: TjIconStub,
    TjCard: TjCardStub,
    TjTable: TjTableStub,
    TjLoading: TjLoadingStub,
    TjAcaoPagina: TjAcaoPaginaStub,
    TjCardFooter: TjCardFooterStub,
    TjCardHeader: TjCardHeaderStub,
    TjCardHeaderTitle: TjCardHeaderTitleStub,
    TjCardHeaderDescription: TjCardHeaderDescriptionStub,
    TjTableActions: TjTableActionsStub,
  };
});

vi.mock('lucide-angular', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return {
    ...actual,
    LucideAngularModule: { forRoot: () => ({ ngModule: class {} }) },
    LucideIconComponent: LucideStub,
    LucideAngularComponent: LucideStub,
    LucideComponent: LucideStub,
  };
});

// Mock Material
vi.mock('@angular/material/card', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual, MatCard: MatCardStub };
});
vi.mock('@angular/material/button', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual, MatButton: MatButtonStub };
});
vi.mock('@angular/material/icon', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual, MatIcon: MatIconStub };
});
vi.mock('@angular/material/input', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual };
});
vi.mock('@angular/material/select', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual };
});
vi.mock('@angular/material/form-field', async importOriginal => {
  const actual = (await importOriginal()) as any;
  return { ...actual };
});
