import { Routes } from '@angular/router';

import { Login } from './components/login/login';

import { Chat } from './components/chat/chat';

import { Verificar } from './components/verificar/verificar';

import { authGuard } from './services/guard.service';

export const routes: Routes = [

  {

    path: '',

    component: Login
  },

  {

    path: 'chat',

    component: Chat,

    canActivate: [
      authGuard
    ]
  },

  {

    path: 'verificar',

    component: Verificar
  },

  {

    path: '**',

    redirectTo: ''
  }

];
