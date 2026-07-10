import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { TjAuthService, UserContext, TjGlobalService, TjSnackBar, TjSnackBarConfig } from '@tjma/angular-21';

/**
 * Guard funcional para validar permissões do Sentinela em rotas GET.
 *
 * Exemplo de uso no app.routes.ts:
 * {
 *   path: 'exemplo',
 *   canActivate: [authGuard],
 *   data: { objetoSentinela: 'ExemploController', permissoes: 'L' }
 * }
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(TjAuthService);
  const router = inject(Router);
  const globalService = inject(TjGlobalService);

  const user = authService.currentUser();
  const objeto = route.data['objetoSentinela'];
  const permissoes = route.data['permissoes'] || 'LEITURA';

  // Se a rota não exige objeto de segurança, permite o acesso
  if (!objeto) {
    return true;
  }

  const temPermissao = UserContext.possuiPermissoes(user, objeto, permissoes);

  if (temPermissao) {
    return true;
  }

  // Acesso negado: Redireciona para a página customizada
  const snackBar = inject(TjSnackBar);
  const config = new TjSnackBarConfig('warning');
  config.title = 'Acesso Restrito';
  config.description = `Você não tem permissão (${permissoes}) para acessar: ${objeto}`;
  config.horizontalPosition = 'center';
  config.verticalPosition = 'top';
  config.duration = 5000;

  snackBar.open(config);

  router.navigate(['/acesso-negado'], {
    state: { message: config.description },
    replaceUrl: true
  });

  return false;
};
