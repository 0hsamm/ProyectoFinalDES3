import {
  Component,
  Input
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { Conversacion } from '../../models/conversacion';

@Component({
  selector: 'app-chat-info',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './chat-info.html',
  styleUrls: ['./chat-info.css']
})
export class ChatInfoComponent {

  @Input()
  conversacion: Conversacion | null = null;

  obtenerNombre(): string {

    return this.conversacion?.nombre ||
      '';
  }

  obtenerIniciales(): string {

    return this.obtenerNombre()
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  obtenerTipo(): string {

    return this.conversacion?.tipoConversacion ||
      'Chat';
  }

  obtenerCantidadParticipantes(): number {

    return this.conversacion?.participantes?.length || 0;
  }
}
