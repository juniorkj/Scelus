import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import {
  TjAuthService,
  TjIconModule,
  TjPageModule,
  TjPaginaNavegacao,
  TjCpfCnpjPipe,
} from '@tjma/angular-21';
import { ConsultarCrimesService } from '../consultar-crimes/consultar-crimes.service';
import { MpusService } from '../mpus/mpus.service';
import { ChangeLogService } from '../changelog/services/changelog.service';

/**
 * javadoc Painel Principal (Dashboard/Home) do Sistema Scelus — CSU000.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.1
 * @since 12/07/2026
 */
@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TjPageModule,
    TjIconModule,
    TjCpfCnpjPipe,
  ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss',
})
export class HomePageComponent implements OnInit {
  private authService = inject(TjAuthService);
  private crimesService = inject(ConsultarCrimesService);
  private mpusService = inject(MpusService);
  private changelogService = inject(ChangeLogService);
  private router = inject(Router);

  readonly carregando = signal(true);
  readonly totalCrimes = signal(0);
  readonly totalMpus = signal(0);
  readonly mpusConcedidas = signal(0);
  readonly mpusNegadas = signal(0);

  readonly ultimosCrimes = signal<any[]>([]);
  readonly changelogHtml = signal<string>('');

  readonly nomeUsuario = computed(() => {
    const info = this.authService.currentUser()?.infoUsuario;
    return info?.nome ?? info?.login ?? 'Usuário';
  });

  readonly navigation = computed<TjPaginaNavegacao>(() => ({
    atual: { href: '/home', title: 'Início' },
  }));

  ngOnInit(): void {
    this.carregarDashboard();
  }

  carregarDashboard(): void {
    this.carregando.set(true);

    forkJoin({
      crimes: this.crimesService.search({ _limit: 5, _offset: 0 }),
      mpus: this.mpusService.search({ _limit: 1, _offset: 0 }),
      mpusConcedidas: this.mpusService.search({
        _limit: 1,
        _offset: 0,
        concedida: 'S',
      }),
      changelog: this.changelogService.listarTodos(),
    }).subscribe({
      next: (res: any) => {
        this.totalCrimes.set(res.crimes.totalCount || 0);
        this.ultimosCrimes.set(res.crimes.result || []);

        const totalM = res.mpus.totalCount || 0;
        const concedidas = res.mpusConcedidas.totalCount || 0;

        this.totalMpus.set(totalM);
        this.mpusConcedidas.set(concedidas);
        this.mpusNegadas.set(totalM - concedidas);

        // Tratar o changelog para exibir de forma resumida na Home (ex: primeiras linhas do markdown/HTML)
        this.changelogHtml.set(res.changelog || '');
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
      },
    });
  }

  abrirChangelogDoMenu(): void {
    const btn = document.querySelector(
      '.tj-change-log button'
    ) as HTMLButtonElement;
    if (btn) {
      btn.click();
    } else {
      this.router.navigate(['/changelog']);
    }
  }
}
