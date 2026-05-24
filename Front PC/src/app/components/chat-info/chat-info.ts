import {
  Component,
  Input
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { Conversacion, Participante} from '../../models/conversacion';



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

  obtenerFoto(): string {

    if (
      this.conversacion?.fotoGrupo &&
      this.conversacion.fotoGrupo.trim() !== ''
    ) {
      return this.conversacion.fotoGrupo.trim();
    }

    const participanteVisible =
      this.conversacion?.participantes?.[0];

    return participanteVisible?.fotoPerfil || '';
  }

  obtenerTipo(): string {

    return this.conversacion?.tipoConversacion ||
      'Chat';
  }

  obtenerCantidadParticipantes(): number {

    return this.conversacion?.participantesIds?.length ||
      this.conversacion?.participantes?.length ||
      0;
  }

  obtenerDescripcionPrincipal(): string {

    const participanteVisible =
      this.conversacion?.participantes?.[0];

    const descripcion =
      participanteVisible?.sobreMi ||
      participanteVisible?.descripcion ||
      '';

    if (descripcion.trim() !== '') {
      return descripcion.trim();
    }

    return this.conversacion?.tipoConversacion === 'GRUPAL'
      ? 'Conversación grupal'
      : 'Sin descripción disponible';
  }

// skipcq: JS-0105
  obtenerDescripcionParticipante(
    participante: Participante
  ): string {
    const descripcion = participante?.sobreMi || participante?.descripcion || '';
    return descripcion.trim() !== '' ? descripcion.trim() : 'Sin descripción';
  }
}
