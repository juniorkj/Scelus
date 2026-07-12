import { Routes } from '@angular/router';
import { TjAuthTemplate } from '@tjma/angular-21';

import { authGuard } from './core/guards/auth.guard';

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
      {
        path: 'crimes',
        canActivate: [authGuard],
        data: { objetoSentinela: 'CrimeController', permissoes: 'LEITURA' },
        loadComponent: () =>
          import('./features/consultar-crimes/pesquisar/consultar-crimes-pesquisar.component').then(
            m => m.ConsultarCrimesPesquisarComponent
          ),
        title: 'Consultar Crimes do Processo — Scelus',
      },
      {
        path: 'crimes/:id',
        canActivate: [authGuard],
        data: { objetoSentinela: 'CrimeController', permissoes: 'LEITURA' },
        loadComponent: () =>
          import('./features/consultar-crimes/manter/consultar-crimes-manter.component').then(
            m => m.ConsultarCrimesManterComponent
          ),
        title: 'Manter Crime do Processo — Scelus',
      },

      {
        path: 'mpus',
        canActivate: [authGuard],
        data: { objetoSentinela: 'MpuController', permissoes: 'LEITURA' },
        loadComponent: () =>
          import('./features/mpus/pesquisar/mpus-pesquisar.component').then(
            m => m.MpusPesquisarComponent
          ),
        title: 'Medidas Protetivas de Urgência — Scelus',
      },
      {
        path: 'mpus/:id',
        canActivate: [authGuard],
        data: { objetoSentinela: 'MpuController', permissoes: 'LEITURA' },
        loadComponent: () =>
          import('./features/mpus/manter/mpus-manter.component').then(
            m => m.MpusManterComponent
          ),
        title: 'Manter Medida Protetiva — Scelus',
      },
      {
        path: 'changelog',
        loadChildren: () =>
          import('./features/changelog/changelog.routes').then(
            m => m.CHANGELOG_ROUTES
          ),
        title: 'Change Log — Scelus',
      },
      {
        path: 'acesso-negado',
        loadComponent: () =>
          import('@tjma/angular-21').then(m => m.TjAcessoNegadoComponent),
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
