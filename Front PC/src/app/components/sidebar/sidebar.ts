import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl: './sidebar.html',

  styleUrl: './sidebar.css'
})
export class SidebarComponent {

  @Input()
  panelActivo: string = 'chats';

  @Output()
  panelActivoChange =
    new EventEmitter<string>();

  confirmandoSalida: boolean = false;

  nombreUsuario: string =
    localStorage.getItem('nombrePersona') ||
    localStorage.getItem('usuario') ||
    'Usuario';

  fotoPerfil: string =
    localStorage.getItem('fotoPerfil') || '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  obtenerIniciales(): string {

    return this.nombreUsuario
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  @HostListener(
    'window:perfil-actualizado',
    ['$event']
  )
  sincronizarPerfilDesdeEvento(
    event: Event
  ): void {

    const detalle =
      (event as CustomEvent).detail || {};

    this.nombreUsuario =
      detalle.nombrePersona ||
      detalle.usuario ||
      localStorage.getItem('nombrePersona') ||
      localStorage.getItem('usuario') ||
      'Usuario';

    this.fotoPerfil =
      detalle.fotoPerfil ||
      localStorage.getItem('fotoPerfil') ||
      '';
  }

  @HostListener('window:storage')
  sincronizarPerfil(): void {

    this.nombreUsuario =
      localStorage.getItem('nombrePersona') ||
      localStorage.getItem('usuario') ||
      'Usuario';

    this.fotoPerfil =
      localStorage.getItem('fotoPerfil') || '';
  }

  cerrarSesion(): void {

    this.authService.cerrarSesion();

    this.router.navigate(['/']);
  }

  solicitarCerrarSesion(): void {

    this.confirmandoSalida = true;
  }

  cancelarCerrarSesion(): void {

    this.confirmandoSalida = false;
  }

  cambiarPanel(
    panel: string
  ): void {

    this.panelActivo = panel;

    this.panelActivoChange.emit(panel);
  }
}
