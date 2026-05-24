import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { timeout } from 'rxjs';

import { Usuario } from '../../models/usuario';

import { ToastService } from '../../services/toast.service';

import { UsuarioService } from '../../services/usuario.service';
import { ThemeService } from '../../services/theme.service';

interface PreferenciasUsuario {
  tema: 'oscuro' | 'claro' | 'sistema';
}

type ErrorConRespuesta = {
  error?: unknown;
  message?: unknown;
  status?: number;
  name?: unknown;
};

function obtenerPreferenciasDefecto(): PreferenciasUsuario {

  return {
    tema: 'oscuro'
  };
}

function obtenerMensajeError(
  err: unknown,
  mensajeDefecto: string
): string {

  const error =
    err as ErrorConRespuesta | null | undefined;

  if (typeof error?.error === 'string') {

    return error.error;
  }

  if (
    typeof error?.error === 'object' &&
    error.error != null
  ) {
    const detalle =
      error.error as Record<string, unknown>;

    if (typeof detalle['mensaje'] === 'string') {
      return detalle['mensaje'];
    }

    if (typeof detalle['error'] === 'string') {
      return detalle['error'];
    }

    if (typeof detalle['message'] === 'string') {
      return detalle['message'];
    }
  }

  if (typeof error?.message === 'string') {

    return error.message;
  }

  if (error?.name === 'TimeoutError') {

    return 'La solicitud tardÃ³ demasiado en responder';
  }

  if (error?.status === 413) {

    return 'El archivo es demasiado grande. Usa una imagen de mÃ¡ximo 25 MB.';
  }

  if (error?.status === 0) {

    return 'No se pudo conectar con el servidor o la subida fue rechazada.';
  }

  return mensajeDefecto;
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

  guardandoPreferencias = false;

  subiendoFoto = false;

  perfilError = '';

  idUsuarioActual =
    Number(localStorage.getItem('idUsuario') || 0);

  private destruido = false;

  constructor(
    private usuarioService: UsuarioService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {

    this.cargarPerfil();
  }

  ngOnDestroy(): void {

    this.destruido = true;
  }

  cargarPerfil(
    mostrarCarga = true
  ): void {

    if (this.idUsuarioActual === 0) {

      this.usuario =
        this.obtenerPerfilLocal();
      this.cargando = false;
      this.perfilError =
        'No se encontrÃ³ una sesiÃ³n activa';
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
            obtenerMensajeError(
              err,
              'No se pudo cargar el perfil'
            );
          this.marcarCambio();

          if (mostrarCarga) {

            this.toastService.warning(
              'Perfil no disponible',
              this.perfilError
            );
          }
        }
      });
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
            'No se pudo guardar el perfil',
            obtenerMensajeError(
              err,
              'Se guardÃ³ una copia en este dispositivo'
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
        'Archivo no vÃ¡lido',
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
              'La imagen se actualizÃ³ correctamente'
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
            obtenerMensajeError(
              err,
              'No se pudo guardar la imagen en el servidor'
            )
          );
        }
      });
  }

  guardarPreferencias(): void {

    this.guardandoPreferencias = true;

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

    this.guardandoPreferencias = false;
    this.marcarCambio();

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
      'Perfil guardado',
      'Los cambios se guardaron en este dispositivo'
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
        const valores =
          JSON.parse(guardadas) as Record<string, unknown>;

        return {
          tema:
            valores['tema'] === 'claro' ||
            valores['tema'] === 'sistema'
              ? valores['tema']
              : 'oscuro'
        };
      } catch {
        return obtenerPreferenciasDefecto();
      }
    }

    return obtenerPreferenciasDefecto();
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

