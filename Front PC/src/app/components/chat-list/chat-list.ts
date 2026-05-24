import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Conversacion } from '../../models/conversacion';

import { Usuario } from '../../models/usuario';

import { ConversacionService } from '../../services/conversacion.service';

import { UsuarioService } from '../../services/usuario.service';

import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-chat-list',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './chat-list.html',

  styleUrl: './chat-list.css'
})
export class ChatListComponent implements OnInit, OnDestroy {

  @Input()
  conversacionSeleccionada: Conversacion | null = null;

  @Output()
  readonly conversacionSeleccionadaChange = new EventEmitter<Conversacion | null>();

  conversaciones: Conversacion[] = [];

  filtro = '';

  cargando = true;

  error = '';

  private destruido = false;

  private usuariosPorId =
    new Map<number, Partial<Usuario>>();

  eliminandoConversacionId: number | null = null;

  idUsuarioActual = Number(localStorage.getItem('idUsuario') || 0);
  constructor(
    private conversacionService: ConversacionService,
    private usuarioService: UsuarioService,
    private toastService: ToastService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargarConversaciones();
  }

  ngOnDestroy(): void {

    this.destruido = true;
  }

  cargarConversaciones(
    mostrarCarga = true
  ): void {

    if (mostrarCarga) {

      this.cargando = true;
      this.marcarCambio();
    }

    this.error = '';

    this.conversacionService
      .obtenerConversaciones()
      .subscribe({

        next: (conversaciones) => {

          this.conversaciones =
            this.ordenarConversaciones(
              conversaciones || []
            );

          if (
            this.conversacionSeleccionada?.id != null
          ) {
            const actualizada =
              this.conversaciones.find(
                (item) =>
                  item.id ===
                  this.conversacionSeleccionada?.id
              );

            if (actualizada) {
              this.seleccionarConversacion(
                actualizada
              );
            } else {
              this.conversacionSeleccionada = null;
              this.conversacionSeleccionadaChange.emit(
                null
              );
            }
          }

          this.limpiarVistasPreviasObsoletas();
          this.cargarNombresReales();

          this.cargando = false;
          this.marcarCambio();

          if (
            this.conversaciones.length > 0 &&
            this.conversacionSeleccionada == null
          ) {

            this.seleccionarConversacion(
              this.conversaciones[0]
            );
          }
        },

        error: () => {

          this.cargando = false;
          this.marcarCambio();

          if (mostrarCarga) {

            this.error =
              'No se pudieron cargar las conversaciones';
          }
        }
      });
  }

  obtenerConversacionesFiltradas(): Conversacion[] {

    const texto =
      this.filtro.trim().toLowerCase();

    if (texto === '') {

      return this.conversaciones;
    }

    return this.conversaciones.filter(
      (conversacion) =>
        this.obtenerNombre(conversacion)
          .toLowerCase()
          .includes(texto) ||
        this.obtenerUltimoMensaje(conversacion)
          .toLowerCase()
          .includes(texto)
    );
  }

  seleccionarConversacion(
    conversacion: Conversacion
  ): void {

    this.conversacionSeleccionada = conversacion;

    this.conversacionSeleccionadaChange.emit(
      conversacion
    );
  }

  eliminarConversacion(
    conversacion: Conversacion,
    event: Event
  ): void {

    event.stopPropagation();

    if (conversacion.id == null) {
      return;
    }

    const confirmar =
      // skipcq: JS-0052
      window.confirm(
        '¿Eliminar esta conversación de tu lista? Volverá a aparecer si alguien envía un mensaje nuevo.'
      );

    if (!confirmar) {
      return;
    }

    this.eliminandoConversacionId =
      conversacion.id;

    this.conversacionService
      .ocultarConversacion(conversacion.id)
      .subscribe({
        next: () => {
          this.conversaciones =
            this.conversaciones.filter(
              (item) => item.id !== conversacion.id
            );

          if (
            this.conversacionSeleccionada?.id ===
            conversacion.id
          ) {
            this.conversacionSeleccionada = null;
            this.conversacionSeleccionadaChange.emit(null);
          }

          this.eliminandoConversacionId = null;
          this.toastService.success(
            'Conversación eliminada',
            'Se ocultó de tu lista'
          );
          this.marcarCambio();

          if (
            this.conversaciones.length > 0 &&
            this.conversacionSeleccionada == null
          ) {
            this.seleccionarConversacion(
              this.conversaciones[0]
            );
          }
        },
        error: (err) => {
          this.eliminandoConversacionId = null;
          this.toastService.error(
            'No se pudo eliminar',
            typeof err.error == 'string'
              ? err.error
              : 'Inténtalo nuevamente'
          );
          this.marcarCambio();
        }
      });
  }

  estaSeleccionada(
    conversacion: Conversacion
  ): boolean {

    return this.conversacionSeleccionada?.id ===
      conversacion.id;
  }

// skipcq: JS-0105
  obtenerNombre(
    conversacion: Conversacion
  ): string {

    if (conversacion.nombre?.trim()) {

      return conversacion.nombre.trim();
    }

    return conversacion.tipoConversacion || '';
  }

  obtenerIniciales(
    conversacion: Conversacion
  ): string {

    return this.obtenerNombre(conversacion)
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((parte) => parte[0])
      .join('')
      .toUpperCase();
  }

// skipcq: JS-0105
  obtenerFotoConversacion(
    conversacion: Conversacion
  ): string {

    if (
      conversacion.fotoGrupo?.trim()
    ) {
      return conversacion.fotoGrupo.trim();
    }

    const participanteVisible =
      conversacion.participantes?.[0];

    return participanteVisible?.fotoPerfil || '';
  }

  obtenerUltimoMensaje(
    conversacion: Conversacion
  ): string {

    if (
      conversacion.fraseSecretaConfigurada &&
      conversacion.id != null
    ) {
      const ultimoMensajeGuardado =
        sessionStorage.getItem(
          this.obtenerClaveUltimoMensaje(
            conversacion.id
          )
        );

      if (
        ultimoMensajeGuardado != null &&
        ultimoMensajeGuardado.trim() !== ''
      ) {
        return ultimoMensajeGuardado;
      }
    }

    return conversacion.ultimoMensaje || '';
  }

  private cargarNombresReales(): void {

    this.conversaciones.forEach(
      (conversacion) => {
        this.actualizarPresentacionConversacion(
          conversacion
        );

        this.obtenerParticipantesVisibles(
          conversacion
        ).forEach((idVisible) => {
          this.usuarioService
            .obtenerUsuarioPorId(idVisible)
            .subscribe({
              next: (usuario) => {
                this.usuariosPorId.set(
                  idVisible,
                  usuario
                );

                this.conversaciones.forEach(
                  (item) =>
                    this.actualizarPresentacionConversacion(
                      item
                    )
                );

                this.marcarCambio();
              },
              // skipcq: JS-0321
              error: () => {}
            });
        });
      }
    );
  }

  private actualizarPresentacionConversacion(
    conversacion: Conversacion
  ): void {

    const participantes =
      conversacion.participantesIds || [];

    const visibles =
      this.obtenerParticipantesVisibles(
        conversacion
      );

    if (
      conversacion.tipoConversacion ===
      'GRUPAL'
    ) {
      const nombres =
        visibles
          .map((id) =>
            this.obtenerNombreUsuario(
              id
            )
          )
          .filter(Boolean) as string[];

      conversacion.nombre =
        nombres.length > 0
          ? this.formatearNombreGrupal(
              nombres,
              visibles.length
            )
          : `Grupo (${participantes.length})`;

      conversacion.participantes =
        visibles.map((id) => ({
          id,
          usuario:
            this.usuariosPorId.get(id)?.usuario ||
            `usuario_${id}`,
          nombrePersona:
            this.obtenerNombreUsuario(id),
            fotoPerfil:
              this.usuariosPorId.get(id)?.fotoPerfil ||
            '',
          sobreMi:
            this.usuariosPorId.get(id)?.sobreMi ||
            ''
        }));

      return;
    }

    const idVisible =
      visibles[0];

    if (idVisible == null) {

      return;
    }

    const nombreVisible =
      this.obtenerNombreUsuario(
        idVisible
      );

    const fotoVisible =
      this.usuariosPorId.get(idVisible)
        ?.fotoPerfil || '';

    if (nombreVisible) {
      conversacion.nombre =
        nombreVisible;
    }

    conversacion.fotoGrupo =
      fotoVisible;

    conversacion.participantes =
      nombreVisible
        ? [{
            id: idVisible,
            usuario:
              this.usuariosPorId.get(idVisible)?.usuario ||
              `usuario_${idVisible}`,
            nombrePersona: nombreVisible,
            fotoPerfil: fotoVisible,
            sobreMi:
              this.usuariosPorId.get(idVisible)?.sobreMi ||
              ''
          }]
        : [];
  }

// skipcq: JS-0105
  private ordenarConversaciones(
    conversaciones: Conversacion[]
  ): Conversacion[] {

    return [...conversaciones].sort((a, b) => {
      const fechaA =
        new Date(
          a.fechaUltimoMensaje ||
          a.fechaCreacion ||
          ''
        ).getTime() || 0;

      const fechaB =
        new Date(
          b.fechaUltimoMensaje ||
          b.fechaCreacion ||
          ''
        ).getTime() || 0;

      return fechaB - fechaA;
    });
  }

  private obtenerParticipantesVisibles(
    conversacion: Conversacion
  ): number[] {

    const participantes =
      conversacion.participantesIds || [];

    const otrosParticipantes =
      participantes.filter(
        (id) => id !== this.idUsuarioActual
      );

    if (otrosParticipantes.length > 0) {
      return otrosParticipantes;
    }

    return participantes;
  }

// skipcq: JS-0105
  private formatearNombreGrupal(
    nombres: string[],
    totalParticipantesVisibles: number
  ): string {

    if (nombres.length === 1) {
      return `Grupo con ${nombres[0]}`;
    }

    if (nombres.length === 2) {
      return `${nombres[0]} y ${nombres[1]}`;
    }

    return `${nombres[0]}, ${nombres[1]} +${Math.max(1, totalParticipantesVisibles - 2)}`;
  }

  private obtenerNombreUsuario(
    usuarioId: number
  ): string {

    const usuario =
      this.usuariosPorId.get(usuarioId);

    return usuario?.nombrePersona ||
      usuario?.usuario ||
      `Usuario ${usuarioId}`;
  }

  private limpiarVistasPreviasObsoletas(): void {

    this.conversaciones.forEach(
      (conversacion) => {
        if (
          conversacion.id != null &&
          (!conversacion.ultimoMensaje ||
            conversacion.ultimoMensaje.trim() === '')
        ) {
          sessionStorage.removeItem(
            this.obtenerClaveUltimoMensaje(
              conversacion.id
            )
          );
        }
      }
    );
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private obtenerClaveUltimoMensaje(
    conversacionId: number
  ): string {

    return `ultimoMensajeConversacion:${this.idUsuarioActual}:${conversacionId}`;
  }
}
