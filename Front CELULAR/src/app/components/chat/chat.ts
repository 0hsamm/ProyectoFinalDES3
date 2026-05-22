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

  conversaciones: any[] = [];

  mensajes: any[] = [];

  conversacionSeleccionada: number = 0;

  nuevoMensaje: string = '';

  usuarioActual: string = '';

  error: string = '';

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

    console.log(
      this.usuarioActual
    );

    this.cargarConversaciones();
  }

  cargarConversaciones(): void {

    this.conversacionService
      .obtenerConversaciones()
      .subscribe({

        next: (data: any[]) => {

          this.conversaciones =
            data;

          if(
            this.conversaciones.length > 0
          ) {

            this.conversacionSeleccionada =

              this.conversaciones[0].id;

            this.cargarMensajes();
          }
        },

        error: (err) => {

          console.log(err);
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

        next: (data: any[]) => {

          this.mensajes =
            data;
        },

        error: (err) => {

          console.log(err);
        }

      });
  }

  enviarMensaje(): void {

    if(
      this.nuevoMensaje.trim() == ''
    ) {

      return;
    }

    this.mensajeService
      .enviarMensaje(

        this.usuarioActual,

        this.conversacionSeleccionada,

        this.nuevoMensaje

      )
      .subscribe({

        next: () => {

          this.nuevoMensaje = '';

          this.cargarMensajes();
        },

        error: (err) => {

          console.log(err);

          this.error =
            'No se pudo enviar el mensaje';
        }

      });
  }

}
