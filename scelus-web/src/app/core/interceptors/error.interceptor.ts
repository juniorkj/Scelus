import { Injectable, inject } from '@angular/core';
import {
  HttpInterceptor, HttpRequest, HttpHandler,
  HttpEvent, HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { TjGlobalService } from '@tjma/angular-21';
import { Observable, EMPTY, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * Interceptor mais interno da cadeia (registrado após tjProviders).
 * Captura erros RFC 9457 do backend, exibe via TjDialog (apenas botão Fechar) e
 * retorna EMPTY — impedindo que TjHttpInterceptor (externo) exiba mensagem duplicada.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  private readonly global = inject(TjGlobalService);
  private readonly router = inject(Router);

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // 401 → TjAuthTemplate redireciona ao Sentinela
        if (error.status === 401) return throwError(() => error);

        const body = this.parseBody(error);
        const title = (body['title'] as string | undefined) ?? this.resolveTitle(error.status);
        const description = this.resolveMessage(error.status, body);

        this.global.error(title, description, {
          disableClose: false,
          state: { title, confirm: { label: 'Fechar' } },
        });

        if (error.status === 403) {
          this.router.navigate(['/acesso-negado'], {
            state: { message: description },
            replaceUrl: true,
          });
        }

        // EMPTY: stream completa sem erro — TjHttpInterceptor nunca vê o erro
        return EMPTY;
      })
    );
  }

  private parseBody(error: HttpErrorResponse): Record<string, unknown> {
    if (error.error == null) return {};
    if (typeof error.error === 'object') return error.error as Record<string, unknown>;
    try { return JSON.parse(error.error as string); } catch { return {}; }
  }

  private resolveTitle(status: number): string {
    switch (status) {
      case 403: return 'Acesso Negado';
      case 404: return 'Não Encontrado';
      case 400: return 'Dados Inválidos';
      case 422: return 'Regra de Negócio';
      default:  return 'Erro de Processamento';
    }
  }

  private resolveMessage(status: number, body: Record<string, unknown>): string {
    if (status === 400 && body['campos']) {
      return Object.entries(body['campos'] as Record<string, string>)
        .map(([campo, msg]) => `${campo}: ${msg}`)
        .join('\n');
    }
    const bodyMessage = (body['message'] ?? body['detail']) as string | undefined;
    if (bodyMessage) return bodyMessage;

    switch (status) {
      case 403: return 'Você não tem permissão para realizar esta operação.';
      case 404: return 'O recurso solicitado não foi encontrado.';
      case 422: return 'A operação não pôde ser concluída. Verifique as regras de negócio.';
      case 400: return 'Os dados informados são inválidos.';
      default:  return 'Ocorreu um erro inesperado. Tente novamente.';
    }
  }
}
