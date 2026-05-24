import { Routes } from '@angular/router';

import { Login } from './components/login/login';
import { Verificar } from './components/verificar/verificar';
import { MainLayoutComponent } from './components/layout/main-layout/main-layout';

import { authGuard } from './guards/auth-guard-guard';

export const routes: Routes = [

  {
    path: '',
    component: Login
  },

  {
    path: 'verificar',
    component: Verificar
  },

  {
    path: 'app',
    component: MainLayoutComponent,
    canActivate: [authGuard]
  },

  {
    path: '**',
    redirectTo: ''
  }

];
