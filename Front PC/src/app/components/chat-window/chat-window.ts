import {
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnChanges,
  SimpleChanges
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Conversacion } from '../../models/conversacion';

import { Mensaje } from '../../models/mensaje';

import { MensajeService } from '../../services/mensaje.service';

import { LlamadaService } from '../../services/llamada.service';

import { ToastService } from '../../services/toast.service';

import {
  interval,
  Subscription
} from 'rxjs';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './chat-window.html',
  styleUrls: ['./chat-window.css']
})
export class ChatWindowComponent implements OnChanges, OnDestroy {

  @Input()
  conversacion: Conversacion | null = null;

  mensajes: Mensaje[] = [];

  nuevoMensaje: string = '';

  cargando: boolean = false;

  enviando: boolean = false;

  llamadaActivaId: number | null = null;

  error: string = '';

  fraseSecreta: string = '';

  usuarioActual: string =
    localStorage.getItem('usuario') || '';

  idUsuarioActual: number =
    Number(localStorage.getItem('idUsuario') || 0);

  private refrescoSub?: Subscription;

  private destruido: boolean = false;

  constructor(
    private mensajeService: MensajeService,
    private llamadaService: LlamadaService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnChanges(
    changes: SimpleChanges
  ): void {

    if (
      changes['conversacion'] &&
      this.conversacion?.id
    ) {

      this.fraseSecreta = '';

      this.cargarMensajes();

      this.reiniciarRefresco();
    }

    if (
      changes['conversacion'] &&
      this.conversacion == null
    ) {

      this.mensajes = [];
      this.refrescoSub?.unsubscribe();
    }
  }

  ngOnDestroy(): void {

    this.destruido = true;

    this.refrescoSub?.unsubscribe();
  }

  cargarMensajes(
    mostrarCarga: boolean = true
  ): void {

    if (!this.conversacion?.id) {

      return;
    }

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    this.error = '';

    this.mensajeService
      .obtenerMensajesPorConversacion(
        this.conversacion.id,
        this.fraseSecreta
      )
      .subscribe({

        next: (mensajes) => {

          this.mensajes = mensajes || [];

          this.cargando = false;
          this.marcarCambio();
        },

        error: (err) => {

          this.cargando = false;
          this.marcarCambio();

          this.error =
            mostrarCarga
              ? 'No se pudieron cargar los mensajes'
              : '';

          if (mostrarCarga) {

            this.toastService.error(
              'Mensajes no disponibles',
              this.error
            );
          }
        }
      });
  }

  enviarMensaje(): void {

    const contenido =
      this.nuevoMensaje.trim();

    if (
      contenido == '' ||
      !this.conversacion?.id ||
      this.enviando
    ) {

      return;
    }

    this.enviando = true;
    this.marcarCambio();

    if (this.idUsuarioActual == 0) {

      this.enviando = false;
      this.marcarCambio();

      this.toastService.error(
        'Sesion incompleta',
        'Vuelve a iniciar sesion para enviar mensajes'
      );

      return;
    }

    this.mensajeService
      .enviarMensaje(
        this.idUsuarioActual,
        this.conversacion.id,
        contenido,
        this.fraseSecreta
      )
      .subscribe({

        next: (respuesta) => {

          this.nuevoMensaje = '';

          this.enviando = false;
          this.marcarCambio();

          this.toastService.success(
            'Mensaje enviado',
            typeof respuesta == 'string'
              ? respuesta
              : undefined
          );

          this.cargarMensajes();
        },

        error: (err) => {

          this.enviando = false;
          this.marcarCambio();

          this.error =
            'No se pudo enviar el mensaje';

          this.toastService.error(
            'No se envio el mensaje',
            this.obtenerMensajeError(
              err,
              this.error
            )
          );
        }
      });
  }

  iniciarLlamada(
    tipo: 'VOZ' | 'VIDEO'
  ): void {

    const receptorId =
      this.obtenerReceptorId();

    if (
      !this.conversacion?.id ||
      this.idUsuarioActual == 0 ||
      receptorId == null
    ) {

      this.toastService.warning(
        'Llamada no disponible',
        'La conversacion no tiene un receptor valido para iniciar llamada'
      );

      return;
    }

    this.llamadaService
      .iniciarLlamada({
        tipoLlamada: tipo,
        conversacionId: this.conversacion.id,
        usuarioLlamanteId: this.idUsuarioActual,
        usuarioReceptorId: receptorId
      })
      .subscribe({

        next: (respuesta) => {

          this.llamadaActivaId = respuesta.id;
          this.marcarCambio();

          this.toastService.success(
            tipo == 'VOZ'
              ? 'Llamada iniciada'
              : 'Videollamada iniciada',
            `Canal: ${respuesta.canalAgora}`
          );
        },

        error: (err) => {

          this.toastService.error(
            'No se pudo iniciar la llamada',
            this.obtenerMensajeError(
              err,
              'Revisa que ambos usuarios pertenezcan a la conversacion'
            )
          );
        }
      });
  }

  finalizarLlamada(): void {

    if (this.llamadaActivaId == null) {

      return;
    }

    this.llamadaService
      .finalizarLlamada(this.llamadaActivaId)
      .subscribe({

        next: (respuesta) => {

          this.toastService.info(
            'Llamada finalizada',
            respuesta
          );

          this.llamadaActivaId = null;
          this.marcarCambio();
        },

        error: (err) => {

          this.toastService.error(
            'No se pudo finalizar',
            this.obtenerMensajeError(
              err,
              'Intenta nuevamente'
            )
          );
        }
      });
  }

  adjuntoSeleccionado(): void {

    this.toastService.warning(
      'Adjuntos pendientes en backend',
      'El back tiene DTO/servicio de archivos, pero falta un controller HTTP para subir y asociar archivos al mensaje'
    );
  }

  obtenerNombreConversacion(): string {

    return this.conversacion?.nombre ||
      (
        this.conversacion?.id != null
          ? `Chat #${this.conversacion.id}`
          : ''
      );
  }

  obtenerIniciales(): string {

    return this.obtenerNombreConversacion()
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  esMensajePropio(
    mensaje: Mensaje
  ): boolean {

    return mensaje.remitenteId == this.idUsuarioActual ||
      mensaje.usuarioId == this.idUsuarioActual ||
      mensaje.usuario == this.usuarioActual ||
      mensaje.nombreUsuario == this.usuarioActual;
  }

  formatearHora(
    fecha?: string
  ): string {

    const valorFecha =
      fecha || '';

    if (!valorFecha) {

      return '';
    }

    const fechaMensaje =
      new Date(valorFecha);

    if (Number.isNaN(fechaMensaje.getTime())) {

      return '';
    }

    return fechaMensaje.toLocaleTimeString(
      'es-CO',
      {
        hour: '2-digit',
        minute: '2-digit'
      }
    );
  }

  obtenerContenido(
    mensaje: Mensaje
  ): string {

    if (mensaje.contenidoProtegido) {

      return 'Mensaje protegido';
    }

    return mensaje.contenido;
  }

  obtenerHoraMensaje(
    mensaje: Mensaje
  ): string {

    return this.formatearHora(
      mensaje.horaEnvio ||
      mensaje.fechaEnvio
    );
  }

  private obtenerReceptorId(): number | null {

    const participantes =
      this.conversacion?.participantesIds || [];

    const receptor =
      participantes.find(
        (id) => id != this.idUsuarioActual
      );

    return receptor || null;
  }

  private obtenerMensajeError(
    err: any,
    mensajeDefecto: string
  ): string {

    if (typeof err?.error == 'string') {

      return err.error;
    }

    return mensajeDefecto;
  }

  private reiniciarRefresco(): void {

    this.refrescoSub?.unsubscribe();

    this.refrescoSub =
      interval(7000)
        .subscribe(() => {
          this.cargarMensajes(false);
        });
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
