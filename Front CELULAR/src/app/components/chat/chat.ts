import {
  Component,
  OnInit
}
  from '@angular/core';

import { CommonModule }
  from '@angular/common';

import { FormsModule }
  from '@angular/forms';

import { ConversacionService }
  from '../../services/conversacion.service';

import { MensajeService }
  from '../../services/mensaje.service';
import { Mensaje } from '../../models/mensaje';

interface Conversacion {
  id: number;
  contenido: string;
}

@Component({

  selector: 'app-chat',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './chat.html',

  styleUrl: './chat.css'

})

export class Chat
  implements OnInit {

  conversaciones: Conversacion[] = [];

  mensajes: Mensaje[] = [];

  conversacionSeleccionada = 0;

  nuevoMensaje = '';

  usuarioActual = '';

  idUsuarioActual = 0;

  error = '';

  constructor(

    private conversacionService:
    ConversacionService,

    private mensajeService:
    MensajeService

  ) {}

  ngOnInit(): void {

    this.usuarioActual =

      localStorage.getItem(
        'usuario'
      ) || '';

    this.idUsuarioActual =
      Number(
        localStorage.getItem(
          'idUsuario'
        ) || 0
      );

    this.cargarConversaciones();
  }

  cargarConversaciones(): void {

    this.conversacionService
      .obtenerConversaciones()
      .subscribe({

        next: (data: unknown[]) => {

          this.conversaciones =
            data as Conversacion[];

          if(
            this.conversaciones.length > 0
          ) {

            this.conversacionSeleccionada =

              this.conversaciones[0].id;

            this.cargarMensajes();
          }
        },

        error: () => {
          this.error =
            'No se pudieron cargar las conversaciones';
        }

      });
  }

  seleccionarConversacion(
    id: number
  ): void {

    this.conversacionSeleccionada =
      id;

    this.cargarMensajes();
  }

  cargarMensajes(): void {

    this.mensajeService
      .obtenerMensajesPorConversacion(

        this.conversacionSeleccionada

      )
      .subscribe({

        next: (data: unknown[]) => {

          this.mensajes =
            data as Mensaje[];
        },

        error: () => {

          this.error =
            'No se pudieron cargar los mensajes';
        }

      });
  }

  enviarMensaje(): void {

    if (
      this.nuevoMensaje.trim() === ''
    ) {

      return;
    }

    this.mensajeService
      .enviarMensaje(

        this.idUsuarioActual,

        this.conversacionSeleccionada,

        this.nuevoMensaje

      )
      .subscribe({

        next: () => {

          this.nuevoMensaje = '';

          this.cargarMensajes();
        },

        error: (err: unknown) => {

          console.error(
            'Error al enviar el mensaje',
            err
          );

          this.error =
            'No se pudo enviar el mensaje';
        }

      });
  }

}
