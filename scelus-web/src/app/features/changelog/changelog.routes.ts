import { Routes } from '@angular/router';

export const CHANGELOG_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/changelog-list.component').then(
        m => m.ChangeLogListComponent
      ),
  },
];
