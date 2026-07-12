import { Component, OnInit, computed, inject, signal } from '@angular/core';
import {
  TjPageModule,
  TjIconModule,
  TjPaginaNavegacao,
} from '@tjma/angular-21';
import { ChangeLogService } from '../services/changelog.service';

/**
 * javadoc Página de histórico de versões (Change Log) do sistema — padrão frottas-new.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Component({
  selector: 'app-changelog-list',
  standalone: true,
  imports: [TjPageModule, TjIconModule],
  template: `
    <tj-page
      title="Histórico de Versões (Change Log)"
      [navegacao]="navigation()">
      @if (!carregando()) {
        <div class="changelog-conteudo" [innerHTML]="conteudo()"></div>
      } @else {
        <div class="changelog-carregando">Carregando histórico...</div>
      }
    </tj-page>
  `,
  styles: [
    `
      :host {
        display: block;
      }

      .changelog-conteudo {
        background: #fff;
        border: 1px solid #e2e8f0;
        border-radius: 0.5rem;
        padding: 1.5rem;
      }

      .changelog-carregando {
        display: flex;
        justify-content: center;
        padding: 3rem;
        color: #94a3b8;
        font-size: 0.875rem;
      }
    `,
  ],
})
export class ChangeLogListComponent implements OnInit {
  conteudo = signal<string>('');
  carregando = signal(false);

  readonly navigation = computed<TjPaginaNavegacao>(() => ({
    atual: { href: '/changelog', title: 'Change Log' },
    grupo: { href: '/', title: 'Scelus' },
  }));

  private service = inject(ChangeLogService);

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.service.listarTodos().subscribe({
      next: (result: string) => {
        this.conteudo.set(result);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }
}
