import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Usuario } from '../../models/usuario';

import { ToastService } from '../../services/toast.service';

import { UsuarioService } from '../../services/usuario.service';
import { ThemeService } from '../../services/theme.service';

import {
  interval,
  Subscription,
  timeout
} from 'rxjs';

interface PreferenciasUsuario {
  refrescoAutomatico: boolean;
  notificaciones: boolean;
  sonidos: boolean;
  mostrarEnLinea: boolean;
  enterParaEnviar: boolean;
  tema: 'oscuro' | 'claro' | 'sistema';
}

@Component({
  selector: 'app-settings-panel',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './settings-panel.html',
  styleUrl: './settings-panel.css'
})
export class SettingsPanelComponent
  implements OnInit, OnDestroy {

  usuario: Partial<Usuario> = {};

  preferencias: PreferenciasUsuario =
    this.obtenerPreferenciasGuardadas();

  fotoSeleccionada: File | null = null;

  vistaPreviaFoto = '';

  cargando = true;

  guardando = false;

  subiendoFoto = false;

  perfilError = '';

  pestanaActiva: 'perfil' | 'privacidad' | 'notificaciones' = 'perfil';

  idUsuarioActual =
    Number(localStorage.getItem('idUsuario') || 0);

  private refrescoSub?: Subscription;

  private destruido = false;

  constructor(
    private usuarioService: UsuarioService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {

    this.cargarPerfil();

    this.refrescoSub =
      interval(30000)
        .subscribe(() => {
          this.cargarPerfil(false);
        });
  }

  ngOnDestroy(): void {

    this.destruido = true;
    this.refrescoSub?.unsubscribe();
  }

  cargarPerfil(
    mostrarCarga = true
  ): void {

    if (this.idUsuarioActual === 0) {

      this.usuario =
        this.obtenerPerfilLocal();
      this.cargando = false;
      this.perfilError =
        'No se encontro una sesion activa';
      this.marcarCambio();

      return;
    }

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    this.usuarioService
      .obtenerUsuarioPorId(this.idUsuarioActual)
      .pipe(
        timeout(8000)
      )
      .subscribe({
        next: (usuario) => {

          this.usuario = usuario;
          this.vistaPreviaFoto =
            usuario.fotoPerfil || '';
          this.perfilError = '';
          this.cargando = false;
          this.sincronizarLocalStorage();
          this.marcarCambio();
        },
        error: (err) => {

          this.usuario =
            this.obtenerPerfilLocal();
          this.vistaPreviaFoto =
            this.usuario.fotoPerfil || '';
          this.cargando = false;
          this.perfilError =
            this.obtenerMensajeError(
              err,
              'No se pudo cargar el perfil desde el backend'
            );
          this.marcarCambio();

          if (mostrarCarga) {

            this.toastService.warning(
              'Perfil en modo local',
              this.perfilError
            );
          }
        }
      });
  }

  cambiarPestana(
    pestana: 'perfil' | 'privacidad' | 'notificaciones'
  ): void {

    this.pestanaActiva = pestana;
  }

  guardarPerfil(): void {

    if (this.idUsuarioActual === 0) {

      this.guardarPerfilLocal();

      return;
    }

    this.guardando = true;
    this.marcarCambio();

    this.usuarioService
      .actualizarUsuario(
        this.idUsuarioActual,
        this.usuario
      )
      .pipe(
        timeout(8000)
      )
      .subscribe({
        next: (respuesta) => {

          this.guardando = false;
          this.sincronizarLocalStorage();
          this.marcarCambio();

          if (this.fotoSeleccionada) {

            this.subirFotoPerfil(
              false,
              true
            );

            this.toastService.info(
              'Guardando foto',
              'El perfil se actualizo, falta terminar la imagen'
            );

            return;
          }

          this.notificarPerfilActualizado();

          this.toastService.success(
            'Perfil actualizado',
            respuesta
          );
        },
        error: (err) => {

          this.guardando = false;
          this.guardarPerfilLocal();
          this.marcarCambio();

          this.toastService.error(
            'No se pudo guardar en backend',
            this.obtenerMensajeError(
              err,
              'Los cambios quedaron guardados localmente'
            )
          );
        }
      });
  }

  seleccionarFoto(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    const archivo =
      input.files?.[0] || null;

    if (!archivo) {

      return;
    }

    if (!archivo.type.startsWith('image/')) {

      this.toastService.warning(
        'Archivo no valido',
        'Selecciona una imagen para tu foto de perfil'
      );

      input.value = '';

      return;
    }

    this.fotoSeleccionada = archivo;
    this.vistaPreviaFoto =
      URL.createObjectURL(archivo);

    this.notificarPerfilActualizado(
      this.vistaPreviaFoto
    );
  }

  subirFotoPerfil(
    mostrarToast = true,
    mostrarPerfilGuardado = false
  ): void {

    if (
      this.idUsuarioActual === 0 ||
      this.fotoSeleccionada == null
    ) {

      if (mostrarToast) {

        this.toastService.warning(
          'Foto pendiente',
          'Selecciona una imagen antes de actualizar tu perfil'
        );
      }

      return;
    }

    this.subiendoFoto = true;
    this.marcarCambio();

    this.usuarioService
      .actualizarFotoPerfil(
        this.idUsuarioActual,
        this.fotoSeleccionada
      )
      .pipe(
        timeout(15000)
      )
      .subscribe({
        next: (usuario) => {

          this.usuario = {
            ...this.usuario,
            ...usuario
          };

          this.fotoSeleccionada = null;
          this.vistaPreviaFoto =
            usuario.fotoPerfil || '';
          this.subiendoFoto = false;
          this.sincronizarLocalStorage();
          this.notificarPerfilActualizado();
          this.marcarCambio();

          if (mostrarToast) {

            this.toastService.success(
              'Foto actualizada',
              'Tu perfil ya muestra la nueva imagen'
            );
          }

          if (mostrarPerfilGuardado) {

            this.toastService.success(
              'Perfil actualizado',
              'Datos y foto guardados correctamente'
            );
          }
        },
        error: (err) => {

          this.subiendoFoto = false;
          this.marcarCambio();

          this.toastService.error(
            'No se pudo subir la foto',
            this.obtenerMensajeError(
              err,
              'No se pudo guardar la imagen en el servidor'
            )
          );
        }
      });
  }

  guardarPreferencias(): void {

    localStorage.setItem(
      'preferenciasUsuario',
      JSON.stringify(this.preferencias)
    );

    this.themeService.aplicarTema(
      this.preferencias.tema
    );

    window.dispatchEvent(
      new CustomEvent(
        'preferencias-actualizadas',
        {
          detail: this.preferencias
        }
      )
    );

    this.toastService.success(
      'Preferencias guardadas'
    );
  }

  obtenerIniciales(): string {

    const nombre =
      this.usuario.nombrePersona ||
      this.usuario.usuario ||
      'Usuario';

    return nombre
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  private guardarPerfilLocal(): void {

    this.sincronizarLocalStorage();
    this.notificarPerfilActualizado();

    this.toastService.info(
      'Perfil guardado localmente',
      'Se sincronizara visualmente en esta sesion'
    );
  }

  private sincronizarLocalStorage(): void {

    if (this.usuario.nombrePersona) {
      localStorage.setItem(
        'nombrePersona',
        this.usuario.nombrePersona
      );
    }

    if (this.usuario.correo) {
      localStorage.setItem(
        'correo',
        this.usuario.correo
      );
    }

    if (this.usuario.usuario) {
      localStorage.setItem(
        'usuario',
        this.usuario.usuario
      );
    }

    if (this.usuario.fotoPerfil) {
      localStorage.setItem(
        'fotoPerfil',
        this.usuario.fotoPerfil
      );
    }
  }

  private obtenerPerfilLocal(): Partial<Usuario> {

    return {
      id: this.idUsuarioActual || undefined,
      usuario:
        localStorage.getItem('usuario') || 'Usuario',
      nombrePersona:
        localStorage.getItem('nombrePersona') || 'Usuario',
      correo:
        localStorage.getItem('correo') || '',
      fotoPerfil:
        localStorage.getItem('fotoPerfil') || '',
      sobreMi:
        localStorage.getItem('sobreMi') || ''
    };
  }

  private obtenerPreferenciasGuardadas(): PreferenciasUsuario {

    const guardadas =
      localStorage.getItem('preferenciasUsuario');

    if (guardadas) {

      try {
        return {
          ...this.obtenerPreferenciasDefecto(),
          ...JSON.parse(guardadas)
        };
      } catch {
        return this.obtenerPreferenciasDefecto();
      }
    }

    return this.obtenerPreferenciasDefecto();
  }

  private obtenerPreferenciasDefecto(): PreferenciasUsuario {

    return {
      refrescoAutomatico: true,
      notificaciones: true,
      sonidos: false,
      mostrarEnLinea: true,
      enterParaEnviar: true,
      tema: 'oscuro'
    };
  }

  private obtenerMensajeError(
    err: unknown,
    mensajeDefecto: string
  ): string {

    const errorBody =
      typeof err === 'object' && err !== null && 'error' in err
        ? (err as { error?: unknown }).error
        : undefined;

    const nombreError =
      typeof err === 'object' && err !== null && 'name' in err
        ? (err as { name?: unknown }).name
        : undefined;

    const status =
      typeof err === 'object' && err !== null && 'status' in err
        ? (err as { status?: unknown }).status
        : undefined;

    if (typeof errorBody === 'string') {

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

    if (nombreError === 'TimeoutError') {

      return 'El backend tardo demasiado en responder';
    }

    if (status === 413) {

      return 'El archivo es demasiado grande. Usa una imagen de maximo 25MB.';
    }

    if (status === 0) {

      return 'No hay conexion con el backend o el servidor rechazo la subida.';
    }

    return mensajeDefecto;
  }

  private notificarPerfilActualizado(
    fotoTemporal?: string
  ): void {

    window.dispatchEvent(
      new CustomEvent(
        'perfil-actualizado',
        {
          detail: {
            nombrePersona:
              this.usuario.nombrePersona,
            usuario:
              this.usuario.usuario,
            fotoPerfil:
              fotoTemporal ||
              this.usuario.fotoPerfil
          }
        }
      )
    );
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
