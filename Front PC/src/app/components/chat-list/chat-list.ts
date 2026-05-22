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

import { ConversacionService } from '../../services/conversacion.service';

import { UsuarioService } from '../../services/usuario.service';

import {
  interval,
  Subscription
} from 'rxjs';

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
  conversacionSeleccionadaChange =
    new EventEmitter<Conversacion | null>();

  conversaciones: Conversacion[] = [];

  filtro: string = '';

  cargando: boolean = true;

  error: string = '';

  private refrescoSub?: Subscription;

  private destruido: boolean = false;

  idUsuarioActual: number =
    Number(localStorage.getItem('idUsuario') || 0);

  constructor(
    private conversacionService: ConversacionService,
    private usuarioService: UsuarioService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargarConversaciones();

    this.refrescoSub =
      interval(15000)
        .subscribe(() => {
          this.cargarConversaciones(false);
        });
  }

  ngOnDestroy(): void {

    this.destruido = true;

    this.refrescoSub?.unsubscribe();
  }

  cargarConversaciones(
    mostrarCarga: boolean = true
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

          this.conversaciones = conversaciones || [];

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

    if (texto == '') {

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

  estaSeleccionada(
    conversacion: Conversacion
  ): boolean {

    return this.conversacionSeleccionada?.id ==
      conversacion.id;
  }

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

  obtenerUltimoMensaje(
    conversacion: Conversacion
  ): string {

    return conversacion.ultimoMensaje || '';
  }

  private cargarNombresReales(): void {

    this.conversaciones.forEach(
      (conversacion) => {

        if (conversacion.nombre?.trim()) {

          return;
        }

        const participantes =
          conversacion.participantesIds || [];

        const otrosParticipantes =
          participantes.filter(
            (id) => id != this.idUsuarioActual
          );

        const idVisible =
          otrosParticipantes[0] || participantes[0];

        if (idVisible == null) {

          return;
        }

        this.usuarioService
          .obtenerUsuarioPorId(idVisible)
          .subscribe({
            next: (usuario) => {
              conversacion.nombre =
                usuario.nombrePersona ||
                usuario.usuario;
              this.marcarCambio();
            },
            error: () => {}
          });
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
}
