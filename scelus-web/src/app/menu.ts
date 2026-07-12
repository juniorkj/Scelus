import { Provider } from '@angular/core';
import {
  MenuItem,
  TJ_AUTH_TEMPLATE_CONFIGS,
  TjAuthTemplateConfigs,
} from '@tjma/angular-21';
import { environment } from '../environments/environment';

/**
 * Menu lateral do scelus-web.
 *
 * Cada item com `objetoSentinela` é automaticamente exibido/ocultado pelo
 * TjAuthTemplate conforme os grupos do usuário autenticado no Sentinela.
 *
 * Os nomes dos `objetoSentinela` correspondem exatamente aos Controller names
 * do sistema scelus cadastrados no Oracle Sentinela para controle de permissão.
 *
 * Ícones: Lucide Icons — https://lucide.dev/icons/
 */
export const MENU: MenuItem[] = [
  {
    label: 'Início',
    icon: 'House',
    path: '/home',
  },
  {
    label: 'Crimes do Processo',
    icon: 'FileText',
    objetoSentinela: 'CrimeController',
    children: [
      {
        label: 'Consultar Crimes',
        icon: 'Search',
        path: '/crimes',
        objetoSentinela: 'CrimeController',
        permissoes: 'LEITURA',
      },
      {
        label: 'Cadastrar Crime',
        icon: 'FilePlus',
        path: '/crimes/new',
        objetoSentinela: 'CrimeController',
        permissoes: 'INCLUSAO',
      },
    ],
  },
  {
    label: 'Medidas Protetivas',
    icon: 'Shield',
    path: '/mpus',
    objetoSentinela: 'MpuController',
    permissoes: 'LEITURA',
  },
];

export const tjMenuProvider = (): Provider => {
  const value: TjAuthTemplateConfigs = {
    menu: MENU,
    changeLogPath: environment.apiUrl + '/changelog',
    configuracoes: { esconder: true },
    notificacoes: { esconder: true },
  };

  return {
    useValue: value,
    provide: TJ_AUTH_TEMPLATE_CONFIGS,
  };
};
