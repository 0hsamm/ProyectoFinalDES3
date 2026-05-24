import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import {
  Amistad,
  SolicitudAmistad
} from '../../models/amistad';

import { Usuario } from '../../models/usuario';

import { AmistadService } from '../../services/amistad.service';

import { ConversacionService } from '../../services/conversacion.service';

import { UsuarioService } from '../../services/usuario.service';

import { ToastService } from '../../services/toast.service';

import {
  interval,
  Subscription
} from 'rxjs';

@Component({
  selector: 'app-friends-panel',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './friends-panel.html',
  styleUrl: './friends-panel.css'
})
export class FriendsPanelComponent
  implements OnInit, OnDestroy {

  usernameDestino = '';

  usuarioEncontrado: Usuario | null = null;

  amigos: Amistad[] = [];

  solicitudesRecibidas: SolicitudAmistad[] = [];

  solicitudesEnviadas: SolicitudAmistad[] = [];

  cargando = true;

  buscando = false;

  procesando = false;

  mensaje = '';

  error = '';

  private readonly textoInicialesPorDefecto = '';

  fraseSecretaChat = '';

  amigosSeleccionadosIds: number[] = [];

  idUsuarioActual =
    Number(localStorage.getItem('idUsuario') || 0);

  private refrescoSub?: Subscription;

  private destruido = false;

  constructor(
    private amistadService: AmistadService,
    private conversacionService: ConversacionService,
    private usuarioService: UsuarioService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargarDatos();

    this.refrescoSub =
      interval(15000)
        .subscribe(() => {
          this.cargarDatos(false);
        });
  }

  ngOnDestroy(): void {

    this.destruido = true;

    this.refrescoSub?.unsubscribe();
  }

  cargarDatos(
    mostrarCarga = true
  ): void {

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    this.error = '';

    let pendientes = 3;

    const finalizar = () => {

      pendientes--;

      if (pendientes === 0) {

        this.cargando = false;
      }
    };

    this.amistadService
      .obtenerAmigos()
      .subscribe({

        next: (amigos) => {

        this.amigos = amigos || [];
        this.amigosSeleccionadosIds =
          this.amigosSeleccionadosIds.filter(
            (id) =>
              this.amigos.some(
                (amigo) =>
                  amigo.usuarioId === id
              )
          );

        finalizar();
        this.marcarCambio();
      },

        error: () => {

          this.error =
            'No se pudieron cargar los amigos';

          if (mostrarCarga) {

            this.toastService.error(
              'Amigos no disponibles',
              this.error
            );
          }

          finalizar();
          this.marcarCambio();
        }
      });

    this.amistadService
      .obtenerSolicitudesRecibidas()
      .subscribe({

        next: (solicitudes) => {

          this.solicitudesRecibidas =
            solicitudes || [];

          finalizar();
          this.marcarCambio();
        },

        error: () => {

          this.error =
            'No se pudieron cargar las solicitudes recibidas';

          if (mostrarCarga) {

            this.toastService.error(
              'Solicitudes no disponibles',
              this.error
            );
          }

          finalizar();
          this.marcarCambio();
        }
      });

    this.amistadService
      .obtenerSolicitudesEnviadas()
      .subscribe({

        next: (solicitudes) => {

          this.solicitudesEnviadas =
            solicitudes || [];

          finalizar();
          this.marcarCambio();
        },

        error: () => {

          this.error =
            'No se pudieron cargar las solicitudes enviadas';

          if (mostrarCarga) {

            this.toastService.error(
              'Solicitudes no disponibles',
              this.error
            );
          }

          finalizar();
          this.marcarCambio();
        }
      });
  }

  buscarUsuario(): void {

    const username =
      this.usernameDestino.trim();

    this.mensaje = '';

    this.error = '';

    this.usuarioEncontrado = null;

    if (username === '') {

      this.error =
        'Ingresa un nombre de usuario';

      this.toastService.warning(
        'Busqueda incompleta',
        this.error
      );

      return;
    }

        this.buscando = true;
    this.marcarCambio();

    this.usuarioService
      .buscarPorUsername(username)
      .subscribe({

        next: (usuario) => {

          this.usuarioEncontrado = usuario;

          this.buscando = false;
          this.marcarCambio();

          this.toastService.success(
            'Usuario encontrado',
            usuario.usuario
          );
        },

        error: () => {

          this.buscando = false;
          this.marcarCambio();

          this.error =
            'No se encontro un usuario con ese nombre';

          this.toastService.error(
            'Usuario no encontrado',
            this.error
          );
        }
      });
  }

  enviarSolicitud(): void {

    const username =
      this.usernameDestino.trim();

    this.mensaje = '';

    this.error = '';

    if (username === '') {

      this.error =
        'Ingresa un nombre de usuario';

      this.toastService.warning(
        'Solicitud incompleta',
        this.error
      );

      return;
    }

    this.procesando = true;
    this.marcarCambio();

    this.amistadService
      .enviarSolicitud(username)
      .subscribe({

        next: (respuesta) => {

          this.mensaje = respuesta;

          this.toastService.success(
            'Solicitud enviada',
            respuesta
          );

          this.procesando = false;
          this.marcarCambio();

          this.usuarioEncontrado = null;

          this.usernameDestino = '';

          this.cargarDatos();
        },

        error: (err) => {

          this.procesando = false;
          this.marcarCambio();

          this.error =
            this.obtenerMensajeError(
              err,
              'No se pudo enviar la solicitud'
            );

          this.toastService.error(
            'No se pudo enviar',
            this.error
          );
        }
      });
  }

  aceptarSolicitud(
    solicitud: SolicitudAmistad
  ): void {

    this.responderSolicitud(
      solicitud.id,
      true
    );
  }

  rechazarSolicitud(
    solicitud: SolicitudAmistad
  ): void {

    this.responderSolicitud(
      solicitud.id,
      false
    );
  }

  cancelarSolicitud(
    solicitud: SolicitudAmistad
  ): void {

    this.mensaje = '';

    this.error = '';

    this.procesando = true;
    this.marcarCambio();

    this.amistadService
      .cancelarSolicitud(solicitud.id)
      .subscribe({

        next: (respuesta) => {

          this.procesando = false;
          this.marcarCambio();

          this.toastService.info(
            'Solicitud cancelada',
            respuesta
          );

          this.cargarDatos();
        },

        error: (err) => {

          this.procesando = false;
          this.marcarCambio();

          this.error =
            this.obtenerMensajeError(
              err,
              'No se pudo cancelar la solicitud'
            );

          this.toastService.error(
            'No se pudo cancelar',
            this.error
          );
        }
      });
  }

  eliminarAmigo(
    amigo: Amistad
  ): void {

    this.mensaje = '';

    this.error = '';

    this.procesando = true;
    this.marcarCambio();

    this.amistadService
      .eliminarAmistad(amigo.usuarioId)
      .subscribe({

        next: (respuesta) => {

          this.mensaje = respuesta;

          this.toastService.success(
            'Amistad actualizada',
            respuesta
          );

          this.procesando = false;
          this.marcarCambio();

          this.cargarDatos();
        },

        error: (err) => {

          this.procesando = false;
          this.marcarCambio();

          this.error =
            this.obtenerMensajeError(
              err,
              'No se pudo eliminar la amistad'
            );

          this.toastService.error(
            'No se pudo eliminar',
            this.error
          );
        }
      });
  }

  crearChatPrivado(
    amigo: Amistad
  ): void {

    const frase =
      this.fraseSecretaChat.trim();

    if (this.idUsuarioActual === 0) {

      this.toastService.error(
        'Sesion incompleta',
        'Vuelve a iniciar sesion para crear el chat'
      );

      return;
    }

    if (frase.length < 8) {

      this.toastService.warning(
        'Frase secreta requerida',
        'Debe tener minimo 8 caracteres'
      );

      return;
    }

    this.procesando = true;
    this.marcarCambio();

    this.conversacionService
      .crearConversacionPrivada(
        this.idUsuarioActual,
        amigo.usuarioId,
        frase
      )
      .subscribe({

        next: (respuesta) => {

          this.procesando = false;
          this.marcarCambio();

          this.toastService.success(
            'Chat privado creado',
            respuesta
          );
        },

        error: (err) => {

          this.procesando = false;
          this.marcarCambio();

          this.toastService.error(
            'No se pudo crear el chat',
            this.obtenerMensajeError(
              err,
              'Revisa que ambos usuarios existan y no haya datos incompletos'
            )
          );
        }
      });
  }

  alternarSeleccionGrupo(
    amigo: Amistad
  ): void {

    if (
      this.estaSeleccionadoParaGrupo(
        amigo
      )
    ) {
      this.amigosSeleccionadosIds =
        this.amigosSeleccionadosIds.filter(
          (id) =>
            id !== amigo.usuarioId
        );

      return;
    }

    this.amigosSeleccionadosIds = [
      ...this.amigosSeleccionadosIds,
      amigo.usuarioId
    ];
  }

  estaSeleccionadoParaGrupo(
    amigo: Amistad
  ): boolean {

    return this.amigosSeleccionadosIds
      .includes(amigo.usuarioId);
  }

  crearChatGrupal(): void {

    const frase =
      this.fraseSecretaChat.trim();

    if (this.idUsuarioActual === 0) {

      this.toastService.error(
        'Sesion incompleta',
        'Vuelve a iniciar sesion para crear el grupo'
      );

      return;
    }

    if (
      this.amigosSeleccionadosIds.length < 2
    ) {

      this.toastService.warning(
        'Seleccion insuficiente',
        'Selecciona al menos 2 amigos para crear un grupo'
      );

      return;
    }

    if (frase.length < 8) {

      this.toastService.warning(
        'Frase secreta requerida',
        'Debe tener minimo 8 caracteres'
      );

      return;
    }

    this.procesando = true;
    this.marcarCambio();

    this.conversacionService
      .crearConversacionGrupal(
        [
          this.idUsuarioActual,
          ...this.amigosSeleccionadosIds
        ],
        frase
      )
      .subscribe({
        next: (respuesta) => {

          this.procesando = false;
          this.amigosSeleccionadosIds = [];
          this.marcarCambio();

          this.toastService.success(
            'Grupo creado',
            respuesta
          );
        },
        error: (err) => {

          this.procesando = false;
          this.marcarCambio();

          this.toastService.error(
            'No se pudo crear el grupo',
            this.obtenerMensajeError(
              err,
              'Revisa la frase secreta y los participantes seleccionados'
            )
          );
        }
      });
  }

  obtenerIniciales(
    nombre?: string,
    usuario?: string
  ): string {

    const texto =
      (nombre || usuario || this.textoInicialesPorDefecto)
        .trim();

    return texto
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  private responderSolicitud(
    id: number,
    aceptar: boolean
  ): void {

    this.mensaje = '';

    this.error = '';

    this.procesando = true;
    this.marcarCambio();

    const peticion = aceptar
      ? this.amistadService.aceptarSolicitud(id)
      : this.amistadService.rechazarSolicitud(id);

    peticion.subscribe({

      next: (respuesta) => {

        this.mensaje = respuesta;

        this.toastService.success(
          aceptar
            ? 'Solicitud aceptada'
            : 'Solicitud rechazada',
          respuesta
        );

        this.procesando = false;
        this.marcarCambio();

        this.cargarDatos();
      },

      error: (err) => {

        this.procesando = false;
        this.marcarCambio();

        this.error =
          this.obtenerMensajeError(
            err,
            'No se pudo responder la solicitud'
          );

        this.toastService.error(
          'No se pudo responder',
          this.error
        );
      }
    });
  }
// skipcq: JS-0105
  private obtenerMensajeError(
    err: unknown,
    mensajeDefecto: string
  ): string {

    const errorBody =
      typeof err === 'object' && err !== null && 'error' in err
        ? (err as { error?: unknown }).error
        : undefined;

    const status =
      typeof err === 'object' && err !== null && 'status' in err
        ? (err as { status?: unknown }).status
        : undefined;

    if (typeof errorBody === 'string') {

      if (
        errorBody.includes('NoResourceFoundException') ||
        errorBody.includes('No static resource') ||
        errorBody.includes('trace')
      ) {

        return 'El backend activo todavia no tiene este endpoint. Revisa que el back este actualizado y reinicia Spring Boot.';
      }

      return errorBody;
    }

    if (
      typeof errorBody === 'object' &&
      errorBody !== null
    ) {

      const errorObject =
        errorBody as Record<string, unknown>;

      if (typeof errorObject['mensaje'] === 'string') {

        return errorObject['mensaje'];
      }

      if (typeof errorObject['error'] === 'string') {

        return errorObject['error'];
      }

      if (typeof errorObject['message'] === 'string') {

        return errorObject['message'];
      }
    }

    if (status === 404) {

      return 'El backend no encontro esta ruta o recurso. Revisa que el back este actualizado y reinicia Spring Boot.';
    }

    return mensajeDefecto;
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
