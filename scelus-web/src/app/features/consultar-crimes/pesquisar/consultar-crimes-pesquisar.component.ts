import {
  Component,
  ChangeDetectionStrategy,
  signal,
  computed,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import {
  TjCrudBaseComponent,
  TjTableModule,
  TjRowActions,
  TjFormModule,
  TjIconModule,
  TjPageModule,
  TjButtonModule,
  TjFilters,
  TjPaginaNavegacao,
  TjCpfCnpjPipe,
} from '@tjma/angular-21';
import { ConsultarCrimesService } from '../consultar-crimes.service';
import { DominioService } from '../../../core/services/dominio.service';
import { ItemDominio } from '../consultar-crimes.model';

type Opcao = { label: string; value: number };

/**
 * javadoc Componente de pesquisa e listagem de Crimes do Processo — CSU001.
 * Implementa os filtros simples (Tela 1.1), avançados (Tela 1.2 / SF01) e
 * avançados compostos (Tela 1.3 / SF02, com OR dentro da mesma lista — RN001.02).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 2.0
 * @since 10/07/2026
 */
@Component({
  selector: 'app-consultar-crimes-pesquisar',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    MatTableModule,
    TjRowActions,
    TjFormModule,
    TjIconModule,
    TjPageModule,
    TjButtonModule,
    TjFilters,
    TjTableModule,
    TjCpfCnpjPipe,
  ],
  templateUrl: './consultar-crimes-pesquisar.component.html',
  styleUrl: './consultar-crimes-pesquisar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConsultarCrimesPesquisarComponent extends TjCrudBaseComponent<ConsultarCrimesService> {
  private readonly dominioService = inject(DominioService);

  readonly columns = signal({
    numeroProcesso: 'Número do Processo',
    codigoAssunto: 'Assunto (Código)',
    dataFato: 'Data do Fato',
    medidaProtetiva: 'MPU',
    nomeVitima: 'Vítima',
    nomeAcusado: 'Acusado',
    tipoVinculo: 'Vínculo',
    deficienciaVitima: 'Deficiência da Vítima',
    actions: 'Ações',
  });

  readonly navigation = computed<TjPaginaNavegacao>(() => ({
    atual: { href: '/crimes', title: 'Consultar Crimes do Processo' },
    grupo: { href: '/', title: 'Scelus' },
  }));

  /** SF01/SF02 — visibilidade das seções de filtros avançados e compostos. */
  mostrarAvancados = false;
  mostrarCompostos = false;

  /** Domínios dos selects (carregados do backend). */
  dominios: Record<string, Opcao[]> = {
    tiposVinculo: [],
    consequenciasViolencia: [],
    ocupacoes: [],
    deficiencias: [],
    estadosCivis: [],
    religioes: [],
    escolaridades: [],
    rendas: [],
    drogas: [],
    racasEtnias: [],
    tiposBeneficio: [],
  };

  /** Valores em edição nos selects dos filtros compostos (antes do botão +). */
  selecaoComposto: Record<string, number | undefined> = {};

  /** Campos dos filtros avançados (Tela 1.2) por bloco. */
  readonly avancadosVitima = [
    { chave: 'idOcupacaoVitima', dominio: 'ocupacoes', label: 'Ocupação' },
    {
      chave: 'idDeficienciaVitima',
      dominio: 'deficiencias',
      label: 'Deficiência',
    },
    {
      chave: 'idEstadoCivilVitima',
      dominio: 'estadosCivis',
      label: 'Estado Civil',
    },
    { chave: 'idReligiaoVitima', dominio: 'religioes', label: 'Religião' },
    {
      chave: 'idEscolaridadeVitima',
      dominio: 'escolaridades',
      label: 'Escolaridade',
    },
    { chave: 'idRendaVitima', dominio: 'rendas', label: 'Renda' },
  ];

  readonly avancadosAcusado = [
    { chave: 'idOcupacaoAcusado', dominio: 'ocupacoes', label: 'Ocupação' },
    {
      chave: 'idDeficienciaAcusado',
      dominio: 'deficiencias',
      label: 'Deficiência',
    },
    {
      chave: 'idEstadoCivilAcusado',
      dominio: 'estadosCivis',
      label: 'Estado Civil',
    },
    { chave: 'idReligiaoAcusado', dominio: 'religioes', label: 'Religião' },
    {
      chave: 'idEscolaridadeAcusado',
      dominio: 'escolaridades',
      label: 'Escolaridade',
    },
    { chave: 'idRendaAcusado', dominio: 'rendas', label: 'Renda' },
  ];

  /** Campos dos filtros avançados compostos (Tela 1.3) por bloco — OR dentro da lista. */
  readonly compostosVitima = [
    { chave: 'idsDrogaVitima', dominio: 'drogas', label: 'Drogas Utilizadas' },
    { chave: 'idsOcupacaoVitima', dominio: 'ocupacoes', label: 'Ocupação' },
    {
      chave: 'idsDeficienciaVitima',
      dominio: 'deficiencias',
      label: 'Deficiência',
    },
    {
      chave: 'idsRacaEtniaVitima',
      dominio: 'racasEtnias',
      label: 'Raça / Etnia',
    },
    {
      chave: 'idsBeneficioVitima',
      dominio: 'tiposBeneficio',
      label: 'Benefício',
    },
    {
      chave: 'idsConsequenciaViolenciaVitima',
      dominio: 'consequenciasViolencia',
      label: 'Consequência da Violência',
    },
  ];

  readonly compostosAcusado = [
    { chave: 'idsDrogaAcusado', dominio: 'drogas', label: 'Drogas Utilizadas' },
    { chave: 'idsOcupacaoAcusado', dominio: 'ocupacoes', label: 'Ocupação' },
    {
      chave: 'idsDeficienciaAcusado',
      dominio: 'deficiencias',
      label: 'Deficiência',
    },
    {
      chave: 'idsRacaEtniaAcusado',
      dominio: 'racasEtnias',
      label: 'Raça / Etnia',
    },
    {
      chave: 'idsBeneficioAcusado',
      dominio: 'tiposBeneficio',
      label: 'Benefício',
    },
  ];

  constructor() {
    super(inject(ConsultarCrimesService));
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.carregarDominios();
  }

  private carregarDominios(): void {
    const fontes: Array<[string, string]> = [
      ['tiposVinculo', 'tipos-vinculo'],
      ['consequenciasViolencia', 'consequencias-violencia'],
      ['ocupacoes', 'ocupacoes'],
      ['deficiencias', 'deficiencias'],
      ['estadosCivis', 'estados-civis'],
      ['religioes', 'religioes'],
      ['escolaridades', 'escolaridades'],
      ['rendas', 'rendas'],
      ['drogas', 'drogas'],
      ['racasEtnias', 'racas-etnias'],
      ['tiposBeneficio', 'tipos-beneficio'],
    ];
    for (const [chave, endpoint] of fontes) {
      this.dominioService.listarDominio(endpoint).subscribe({
        next: (itens: ItemDominio[]) => {
          this.dominios[chave] = itens.map(i => ({
            label: i.descricao,
            value: i.id,
          }));
          this._cd.detectChanges();
        },
      });
    }
  }

  toggleAvancados(): void {
    this.mostrarAvancados = !this.mostrarAvancados;
    if (!this.mostrarAvancados) {
      this.mostrarCompostos = false;
    }
  }

  toggleCompostos(): void {
    this.mostrarCompostos = !this.mostrarCompostos;
  }

  /** SF02 — inclui um valor no quadro-resumo do filtro composto (botão +). */
  adicionarComposto(chave: string): void {
    const valor = this.selecaoComposto[chave];
    if (valor === undefined || valor === null) return;

    const lista: number[] = this.searchParams[chave] || [];
    if (!lista.includes(Number(valor))) {
      this.searchParams[chave] = [...lista, Number(valor)];
    }
    this.selecaoComposto[chave] = undefined;
  }

  /** SF02 — remove um valor do quadro-resumo do filtro composto. */
  removerComposto(chave: string, valor: number): void {
    const lista: number[] = (this.searchParams[chave] || []).filter(
      (v: number) => v !== valor
    );
    if (lista.length) {
      this.searchParams[chave] = lista;
    } else {
      delete this.searchParams[chave];
    }
  }

  valoresComposto(chave: string): number[] {
    return this.searchParams[chave] || [];
  }

  descricaoDominio(dominio: string, valor: number): string {
    return (
      this.dominios[dominio]?.find(o => o.value === Number(valor))?.label ||
      String(valor)
    );
  }
}
