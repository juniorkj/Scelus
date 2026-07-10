import { Routes } from '@angular/router';
import { TjAuthTemplate } from '@tjma/angular-21';

export const routes: Routes = [
  {
    path: '',
    component: TjAuthTemplate,
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full',
      },
      {
        path: 'home',
        loadComponent: () =>
          import('./features/home/home-page.component').then(
            m => m.HomePageComponent
          ),
        title: 'Início — Scelus',
      },

      // ── Módulos de domínio do Scelus vão entrar aqui, seguindo o padrão:
      // {
      //   path: 'exemplo',
      //   canActivate: [authGuard],
      //   data: { objetoSentinela: 'ExemploController', permissoes: 'LEITURA' },
      //   loadComponent: () =>
      //     import('./features/exemplo/pesquisar/exemplo-pesquisar.component').then(
      //       m => m.ExemploPesquisarComponent
      //     ),
      //   title: 'Exemplo — Scelus',
      // },

      {
        path: 'acesso-negado',
        loadComponent: () =>
          import('@tjma/angular-21').then(
            m => m.TjAcessoNegadoComponent
          ),
        title: 'Acesso Negado — Scelus',
      },
      {
        path: '404',
        loadComponent: () =>
          import('@tjma/angular-21').then(
            m => m.TjPaginaNaoEncontradaComponent
          ),
        title: 'Página Não Encontrada — Scelus',
      },
      {
        path: '**',
        redirectTo: '404',
      },
    ],
  },
];
