import { Component, computed, inject } from '@angular/core';
import { TjAuthService } from '@tjma/angular-21';

@Component({
  selector: 'app-home-page',
  standalone: true,
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss',
})
export class HomePageComponent {
  private authService = inject(TjAuthService);

  readonly nomeUsuario = computed(() => {
    const info = this.authService.currentUser()?.infoUsuario;
    return info?.nome ?? info?.login ?? 'Usuário';
  });
}
