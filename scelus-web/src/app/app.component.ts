import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
  NavigationEnd,
  NavigationSkipped,
  Router,
  RouterOutlet,
} from '@angular/router';
import { environment } from '../environments/environment';
import { TjApp, TjAuthService, TjAuthTemplateContext } from '@tjma/angular-21';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { filter, Subscription } from 'rxjs';

/**
 * Componente raiz do scelus-web.
 *
 * Usa o TjApp da infra-angular como shell da aplicação.
 * O TjAuthTemplate (configurado nas rotas) gerencia o SSO Sentinela.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TjApp],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private tjContext = inject(TjAuthTemplateContext, { optional: true });
  private authService = inject(TjAuthService);
  private breakpointObserver = inject(BreakpointObserver);
  private subscriptions = new Subscription();

  constructor() {
    this.ajustarFluxoLogout();
  }

  /**
   * Corrige o comportamento do botão "Sair".
   * A infra por padrão tenta chamar /scelus-api/logout, que resulta em 500.
   * Forçamos o redirecionamento para o fluxo de logout do Sentinela, usando a
   * URL base que o próprio backend informou no login (header Seguranca-URL,
   * guardado pela infra em LOGIN_URL_KEY) — assim o destino acompanha o
   * ambiente real (dev/homolog/prod) sem depender do environment do build.
   */
  private ajustarFluxoLogout(): void {
    this.authService.logout = () => {
      // Limpa estado local
      this.authService.sentinelaToken.next(null);

      // Acessando via bracket notation para evitar erro de compilação com campo privado
      (this.authService as any)['_userContext']?.next(null);

      // TjStorage usa sessionStorage quando disponível, com fallback para localStorage
      const storage = window.sessionStorage ?? window.localStorage;
      storage.removeItem('USER_CONTEXT_KEY');

      const sentinelaUrl =
        storage.getItem('LOGIN_URL_KEY') || environment.sentinelaUrl;
      const base = sentinelaUrl.endsWith('/')
        ? sentinelaUrl.slice(0, -1)
        : sentinelaUrl;

      // Redireciona para o Sentinela — a própria página de base encerra a sessão SSO
      window.location.assign(base);
    };
  }

  ngOnInit(): void {
    // Monitora a troca de rotas para fechar o menu mobile automaticamente ao navegar.
    const navSubscription = this.router.events
      .pipe(
        filter(
          (event): event is NavigationEnd | NavigationSkipped =>
            event instanceof NavigationEnd || event instanceof NavigationSkipped
        )
      )
      .subscribe(() => {
        // retorna true se a tela corresponder ao breakpoint de Handset (mobile).
        if (
          this.breakpointObserver.isMatched(Breakpoints.Handset) &&
          this.tjContext
        ) {
          this.tjContext.close();
        }
      });
    this.subscriptions.add(navSubscription);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
