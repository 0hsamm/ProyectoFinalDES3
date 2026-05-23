import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { SolicitudAmistad } from '../../models/amistad';

import { Llamada } from '../../models/llamada';

import { AmistadService } from '../../services/amistad.service';

import { LlamadaService } from '../../services/llamada.service';

import { ToastService } from '../../services/toast.service';

import {
  interval,
  Subscription
} from 'rxjs';

@Component({
  selector: 'app-notifications-panel',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './notifications-panel.html',
  styleUrl: './notifications-panel.css'
})
export class NotificationsPanelComponent
  implements OnInit, OnDestroy {

  solicitudes: SolicitudAmistad[] = [];

  llamadas: Llamada[] = [];

  cargando: boolean = true;

  idUsuarioActual: number =
    Number(localStorage.getItem('idUsuario') || 0);

  private refrescoSub?: Subscription;

  private destruido: boolean = false;

  constructor(
    private amistadService: AmistadService,
    private llamadaService: LlamadaService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargar();

    this.refrescoSub =
      interval(15000)
        .subscribe(() => {
          this.cargar(false);
        });
  }

  ngOnDestroy(): void {

    this.destruido = true;

    this.refrescoSub?.unsubscribe();
  }

  cargar(
    mostrarCarga: boolean = true
  ): void {

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    let pendientes = 2;

    const finalizar = () => {

      pendientes--;

      if (pendientes == 0) {

        this.cargando = false;
      }
    };

    this.amistadService
      .obtenerSolicitudesRecibidas()
      .subscribe({
        next: (solicitudes) => {
          this.solicitudes = solicitudes || [];
          finalizar();
          this.marcarCambio();
        },
        error: () => {
          finalizar();
          this.marcarCambio();

          if (mostrarCarga) {

            this.toastService.error(
              'Notificaciones incompletas',
              'No se pudieron cargar las solicitudes'
            );
          }
        }
      });

    if (this.idUsuarioActual == 0) {

      finalizar();
      this.marcarCambio();

      return;
    }

    this.llamadaService
      .obtenerHistorialUsuario(this.idUsuarioActual)
      .subscribe({
        next: (llamadas) => {
          this.llamadas = (llamadas || []).slice(0, 10);
          finalizar();
          this.marcarCambio();
        },
        error: () => {
          finalizar();
          this.marcarCambio();

          if (mostrarCarga) {

            this.toastService.error(
              'Llamadas no disponibles',
              'No se pudo cargar el historial'
            );
          }
        }
      });
  }

  aceptar(
    solicitud: SolicitudAmistad
  ): void {

    this.amistadService
      .aceptarSolicitud(solicitud.id)
      .subscribe({
        next: (respuesta) => {
          this.toastService.success(
            'Solicitud aceptada',
            respuesta
          );
          this.cargar();
          this.marcarCambio();
        },
        error: (err) => {
          this.toastService.error(
            'No se pudo aceptar',
            this.obtenerMensajeError(err)
          );
        }
      });
  }

  rechazar(
    solicitud: SolicitudAmistad
  ): void {

    this.amistadService
      .rechazarSolicitud(solicitud.id)
      .subscribe({
        next: (respuesta) => {
          this.toastService.info(
            'Solicitud rechazada',
            respuesta
          );
          this.cargar();
          this.marcarCambio();
        },
        error: (err) => {
          this.toastService.error(
            'No se pudo rechazar',
            this.obtenerMensajeError(err)
          );
        }
      });
  }

  obtenerNombreInterlocutor(
    llamada: Llamada
  ): string {

    if (
      llamada.usuarioLlamanteId ==
      this.idUsuarioActual
    ) {
      return llamada
        .usuarioReceptorNombre ||
        `Usuario ${llamada.usuarioReceptorId}`;
    }

    return llamada
      .usuarioLlamanteNombre ||
      `Usuario ${llamada.usuarioLlamanteId}`;
  }

  obtenerEtiquetaLlamada(
    llamada: Llamada
  ): string {

    const nombre =
      this.obtenerNombreInterlocutor(
        llamada
      );

    if (
      llamada.usuarioLlamanteId ==
      this.idUsuarioActual
    ) {
      return `Llamaste a ${nombre}`;
    }

    return `${nombre} te llamo`;
  }

  private obtenerMensajeError(
    err: any
  ): string {

    if (typeof err?.error == 'string') {

      return err.error;
    }

    if (typeof err?.error?.mensaje == 'string') {

      return err.error.mensaje;
    }

    if (typeof err?.error?.error == 'string') {

      return err.error.error;
    }

    if (typeof err?.error?.message == 'string') {

      return err.error.message;
    }

    return 'Intenta nuevamente';
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
