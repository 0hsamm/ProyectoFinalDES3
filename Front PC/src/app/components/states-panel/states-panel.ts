import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import {
  Estado,
  EstadoInteraccion
} from '../../models/estado';

import { EstadoService } from '../../services/estado.service';

import { ToastService } from '../../services/toast.service';

import {
  forkJoin,
  interval,
  of,
  Subscription
} from 'rxjs';

@Component({
  selector: 'app-states-panel',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './states-panel.html',
  styleUrl: './states-panel.css'
})
export class StatesPanelComponent
  implements OnInit, OnDestroy {

  estados: Estado[] = [];

  misEstados: Estado[] = [];

  filtroActivo: 'todos' | 'mios' = 'todos';

  texto = '';

  archivo: File | null = null;

  vistaPreviaArchivo = '';

  tipoArchivo: 'IMAGEN' | 'VIDEO' | null = null;

  cargando = true;

  publicando = false;

  procesandoEstadoId: number | null = null;

  detalleEstado: Estado | null = null;

  vistasDetalle: EstadoInteraccion[] = [];

  likesDetalle: EstadoInteraccion[] = [];

  cargandoDetalle = false;

  private refrescoSub?: Subscription;

  private vistasRegistradas =
    new Set<number>();

  private destruido = false;

  idUsuarioActual =
    Number(localStorage.getItem('idUsuario') || 0);

  constructor(
    private estadoService: EstadoService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargarEstados();

    this.refrescoSub =
      interval(15000)
        .subscribe(() => {
          this.cargarEstados(false);
        });
  }

  ngOnDestroy(): void {

    this.destruido = true;

    this.refrescoSub?.unsubscribe();
  }

  cargarEstados(
    mostrarCarga = true
  ): void {

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    const peticiones = {
      estados: this.estadoService
        .obtenerEstadosActivos(this.idUsuarioActual),
      misEstados: this.idUsuarioActual === 0
        ? of([])
        : this.estadoService
          .obtenerEstadosUsuario(
            this.idUsuarioActual,
            this.idUsuarioActual
          )
    };

    forkJoin(peticiones)
      .subscribe({
        next: ({ estados, misEstados }) => {

          this.estados = estados || [];
          this.misEstados = misEstados || [];
          this.cargando = false;

          this.sincronizarDetalleActivo();
          this.registrarVistasPendientes();
          this.marcarCambio();
        },
        error: (err) => {

          this.cargando = false;
          this.marcarCambio();

          if (mostrarCarga) {

            this.toastService.error(
              'Estados no disponibles',
              this.obtenerMensajeError(
                err,
                'No se pudieron cargar los estados'
              )
            );
          }
        }
      });
  }

  cambiarFiltro(
    filtro: 'todos' | 'mios'
  ): void {

    this.filtroActivo = filtro;
  }

  obtenerEstadosVisibles(): Estado[] {

    return this.filtroActivo === 'mios'
      ? this.misEstados
      : this.estados;
  }

  seleccionarArchivo(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    const archivo =
      input.files?.[0] || null;

    if (!archivo) {

      this.quitarArchivo();

      return;
    }

    const esImagen =
      archivo.type.startsWith('image/');

    const esVideo =
      archivo.type.startsWith('video/');

    if (!esImagen && !esVideo) {

      this.toastService.warning(
        'Archivo no válido',
        'Selecciona una imagen o un video'
      );

      input.value = '';

      return;
    }

    this.archivo = archivo;
    this.tipoArchivo =
      esVideo
        ? 'VIDEO'
        : 'IMAGEN';
    this.vistaPreviaArchivo =
      URL.createObjectURL(archivo);
  }

  publicarEstado(): void {

    const texto =
      this.texto.trim();

    if (
      this.idUsuarioActual === 0 ||
      (texto === '' && this.archivo === null)
    ) {

      this.toastService.warning(
        'Estado incompleto',
        'Escribe un texto o selecciona una imagen/video'
      );

      return;
    }

    this.publicando = true;
    this.marcarCambio();

    const peticion = this.archivo
      ? this.estadoService.crearEstadoArchivo(
        this.idUsuarioActual,
        this.tipoArchivo || 'IMAGEN',
        this.archivo,
        texto
      )
      : this.estadoService.crearEstadoTexto(
        this.idUsuarioActual,
        texto
      );

    peticion.subscribe({
      next: () => {

        this.publicando = false;
        this.texto = '';
        this.quitarArchivo();
        this.filtroActivo = 'mios';

        this.toastService.success(
          'Estado publicado'
        );

        this.cargarEstados();
      },
      error: (err) => {

        this.publicando = false;
        this.marcarCambio();

        this.toastService.error(
        'No se publicó el estado',
          this.obtenerMensajeError(
            err,
            'Revisa la conexión con el backend'
          )
        );
      }
    });
  }

  registrarVista(
    estado: Estado
  ): void {

    if (
      this.idUsuarioActual === 0 ||
      estado.id == null ||
      estado.propio
    ) {

      return;
    }

    this.estadoService
      .registrarVista(
        estado.id,
        this.idUsuarioActual
      )
      .subscribe({
        next: () => {
          estado.visto = true;
          this.vistasRegistradas.add(estado.id);
          this.marcarCambio();
        },
        // skipcq: JS-0321
        error: () => {}
      });
  }

  alternarLike(
    estado: Estado
  ): void {

    if (
      this.idUsuarioActual === 0 ||
      estado.id == null ||
      this.procesandoEstadoId != null
    ) {

      return;
    }

    this.procesandoEstadoId = estado.id;
    this.marcarCambio();

    const peticion = estado.meGusta
      ? this.estadoService
        .quitarLike(
          estado.id,
          this.idUsuarioActual
        )
      : this.estadoService
        .darLike(
          estado.id,
          this.idUsuarioActual
        );

    peticion.subscribe({
      next: (estadoActualizado) => {

        this.procesandoEstadoId = null;
        this.actualizarEstadoLocal(estadoActualizado);

        this.toastService.success(
          estadoActualizado.meGusta
            ? 'Te gusta este estado'
            : 'Like retirado'
        );
      },
      error: (err) => {

        this.procesandoEstadoId = null;
        this.marcarCambio();

        this.toastService.error(
          'No se pudo actualizar el like',
          this.obtenerMensajeError(
            err,
            'Intenta nuevamente'
          )
        );
      }
    });
  }

  eliminarEstado(
    estado: Estado
  ): void {

    if (
      !estado.propio ||
      estado.id == null ||
      this.procesandoEstadoId != null
    ) {

      return;
    }
    // skipcq: JS-0052
    const confirmar = window.confirm(
        '¿Quieres eliminar este estado? Esta acción no se puede deshacer.'
      );

    if (!confirmar) {

      return;
    }

    this.procesandoEstadoId = estado.id;
    this.marcarCambio();

    this.estadoService
      .eliminarEstado(
        estado.id,
        this.idUsuarioActual
      )
      .subscribe({
        next: (respuesta) => {

          this.procesandoEstadoId = null;
          this.estados =
            this.estados.filter((item) =>
              item.id !== estado.id);
          this.misEstados =
            this.misEstados.filter((item) =>
              item.id !== estado.id);

          if (this.detalleEstado?.id === estado.id) {
            this.cerrarDetalle();
          }

          this.toastService.info(
            'Estado eliminado',
            respuesta
          );

          this.marcarCambio();
        },
        error: (err) => {

          this.procesandoEstadoId = null;
          this.marcarCambio();

          this.toastService.error(
            'No se pudo eliminar',
            this.obtenerMensajeError(
              err,
              'Intenta nuevamente'
            )
          );
        }
      });
  }

  abrirDetalle(
    estado: Estado
  ): void {

    if (!estado.propio || estado.id == null) {

      return;
    }

    this.detalleEstado = estado;
    this.cargandoDetalle = true;
    this.vistasDetalle = [];
    this.likesDetalle = [];
    this.marcarCambio();

    forkJoin({
      vistas: this.estadoService
        .obtenerVistasDetalle(
          estado.id,
          this.idUsuarioActual
        ),
      likes: this.estadoService
        .obtenerLikesDetalle(
          estado.id,
          this.idUsuarioActual
        )
    })
      .subscribe({
        next: ({ vistas, likes }) => {

          this.vistasDetalle = vistas || [];
          this.likesDetalle = likes || [];
          this.cargandoDetalle = false;
          this.marcarCambio();
        },
        error: (err) => {

          this.cargandoDetalle = false;
          this.marcarCambio();

          this.toastService.error(
            'Detalle no disponible',
            this.obtenerMensajeError(
              err,
              'Solo puedes ver el detalle de tus estados'
            )
          );
        }
      });
  }

  cerrarDetalle(): void {

    this.detalleEstado = null;
    this.vistasDetalle = [];
    this.likesDetalle = [];
  }

  quitarArchivo(): void {

    this.archivo = null;
    this.tipoArchivo = null;
    this.vistaPreviaArchivo = '';
  }
  // skipcq: JS-0105
  formatearFecha(
    fecha?: string
  ): string {

    if (!fecha) {

      return '';
    }

    const valor =
      new Date(fecha);

    if (Number.isNaN(valor.getTime())) {

      return '';
    }

    return valor.toLocaleString(
      'es-CO',
      {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: 'short'
      }
    );
  }
  // skipcq: JS-0105
  obtenerIniciales(
    nombre?: string
  ): string {

    return (nombre || '')
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

  private actualizarEstadoLocal(
    estadoActualizado: Estado
  ): void {

    const reemplazar =
      (estado: Estado) =>
        estado.id === estadoActualizado.id
          ? {
            ...estado,
            ...estadoActualizado
          }
          : estado;

    this.estados =
      this.estados.map(reemplazar);

    this.misEstados =
      this.misEstados.map(reemplazar);

    if (this.detalleEstado?.id === estadoActualizado.id) {

      this.detalleEstado = {
        ...this.detalleEstado,
        ...estadoActualizado
      };
    }

    this.marcarCambio();
  }

  private sincronizarDetalleActivo(): void {

    if (!this.detalleEstado?.id) {

      return;
    }

    const actualizado =
      this.misEstados.find((estado) =>
        estado.id === this.detalleEstado?.id);

    if (actualizado) {

      this.detalleEstado = actualizado;
    }
  }
  // skipcq: JS-0105
  private obtenerMensajeError(
    // skipcq: JS-0323
    err: any,
    mensajeDefecto: string
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

    return mensajeDefecto;
  }

  private registrarVistasPendientes(): void {

    this.estados
      .filter((estado) =>
        estado.id != null &&
        estado.usuarioId !== this.idUsuarioActual &&
        !this.vistasRegistradas.has(estado.id) &&
        !estado.visto)
      .forEach((estado) => {
        this.registrarVista(estado);
      });
  }
  tieneVistas(): boolean {
    return this.vistasDetalle.length > 0;
  }

  tieneLikes(): boolean {
    return this.likesDetalle.length > 0;
  }
  // skipcq: JS-0105
  obtenerFotoVista(vista: EstadoInteraccion): string {
    return vista.fotoPerfil || '';
  }
  // skipcq: JS-0105
  obtenerNombreVista(vista: EstadoInteraccion): string {
    return vista.usuarioNombre || vista.usuario || '';
  }
  // skipcq: JS-0105
  obtenerFotoLike(like: EstadoInteraccion): string {
    return like.fotoPerfil || '';
  }
  // skipcq: JS-0105
  obtenerNombreLike(like: EstadoInteraccion): string {
    return like.usuarioNombre || like.usuario || '';
  }
  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
