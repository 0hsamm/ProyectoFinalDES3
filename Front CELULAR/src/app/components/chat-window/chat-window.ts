import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewChild
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

  @ViewChild('adjuntoInput')
  adjuntoInput?: ElementRef<HTMLInputElement>;

  mensajes: Mensaje[] = [];

  nuevoMensaje: string = '';

  cargando: boolean = false;

  enviando: boolean = false;

  llamadaActivaId: number | null = null;

  error: string = '';

  fraseSecreta: string = '';

  archivoAdjunto: File | null = null;

  archivoPreviewUrl: string | null = null;

  archivoPreviewEsImagen: boolean = false;

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
      this.mensajes = [];
      this.error = '';
      this.nuevoMensaje = '';
      this.limpiarAdjuntoSeleccionado();

      this.fraseSecreta =
        this.obtenerFraseGuardada(
          this.conversacion.id
        );

      this.cargarMensajes();
      this.reiniciarRefresco();
    }

    if (
      changes['conversacion'] &&
      this.conversacion == null
    ) {
      this.mensajes = [];
      this.nuevoMensaje = '';
      this.limpiarAdjuntoSeleccionado();
      this.refrescoSub?.unsubscribe();
    }
  }

  ngOnDestroy(): void {

    this.destruido = true;
    this.refrescoSub?.unsubscribe();
    this.limpiarAdjuntoSeleccionado();
  }

  cargarMensajes(
    mostrarCarga: boolean = true
  ): void {

    if (!this.conversacion?.id) {
      return;
    }

    if (
      this.conversacion.fraseSecretaConfigurada &&
      this.fraseSecreta.trim().length < 8
    ) {
      this.mensajes = [];
      this.cargando = false;
      this.error =
        'Ingresa la frase secreta de esta conversacion para ver los mensajes';
      this.marcarCambio();
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

          if (this.mensajes.length == 0) {
            this.limpiarUltimoMensajeConversacion();
          }

          const hayMensajesProtegidos =
            this.mensajes.some(
              (mensaje) =>
                mensaje.contenidoProtegido
            );

          const hayMensajesVisibles =
            this.mensajes.some(
              (mensaje) =>
                this.tieneMensajeVisible(
                  mensaje
                )
            );

          if (
            this.conversacion?.fraseSecretaConfigurada &&
            this.fraseSecreta.trim().length >= 8 &&
            this.mensajes.length > 0 &&
            hayMensajesProtegidos &&
            !hayMensajesVisibles
          ) {
            this.error =
              'La frase secreta no coincide con esta conversacion';

            if (mostrarCarga) {
              this.toastService.warning(
                'Frase secreta invalida',
                this.error
              );
            }
          } else {
            this.error = '';
          }

          if (
            this.conversacion?.id &&
            this.conversacion.fraseSecretaConfigurada
          ) {
            this.guardarFraseSecreta(
              this.conversacion.id,
              this.fraseSecreta
            );
          }

          this.actualizarUltimoMensajeConversacion();

          this.cargando = false;
          this.marcarCambio();
        },

        error: () => {

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

    const archivo =
      this.archivoAdjunto;

    if (
      (contenido == '' && archivo == null) ||
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

    if (
      this.conversacion.fraseSecretaConfigurada &&
      this.fraseSecreta.trim().length < 8
    ) {
      this.enviando = false;
      this.marcarCambio();

      this.toastService.warning(
        'Frase secreta requerida',
        'Debes ingresar la frase secreta de la conversacion antes de enviar mensajes'
      );

      return;
    }

    this.mensajeService
      .enviarMensaje(
        this.idUsuarioActual,
        this.conversacion.id,
        contenido == '' ? null : contenido,
        this.fraseSecreta,
        this.obtenerTipoMensajeAdjunto(
          archivo
        ),
        archivo != null
      )
      .subscribe({

        next: (mensajeCreado) => {

          if (
            archivo != null &&
            mensajeCreado.id != null
          ) {
            this.subirAdjunto(
              mensajeCreado,
              archivo,
              contenido
            );
            return;
          }

          this.finalizarEnvioExitoso(
            'Mensaje enviado'
          );
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
      .finalizarLlamada(
        this.llamadaActivaId
      )
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

  adjuntoSeleccionado(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    const archivo =
      input.files?.item(0) || null;

    if (archivo == null) {
      this.limpiarAdjuntoSeleccionado();
      return;
    }

    this.limpiarPreviewAnterior();

    this.archivoAdjunto =
      archivo;

    this.archivoPreviewEsImagen =
      this.esImagenMime(
        archivo.type
      );

    if (this.archivoPreviewEsImagen) {
      this.archivoPreviewUrl =
        URL.createObjectURL(archivo);
    }

    this.error = '';
    this.marcarCambio();
  }

  quitarAdjunto(): void {
    this.limpiarAdjuntoSeleccionado();
    this.marcarCambio();
  }

  obtenerNombreConversacion(): string {

    return this.conversacion?.nombre ||
      (
        this.conversacion?.id != null
          ? `Chat #${this.conversacion.id}`
          : ''
      );
  }

  obtenerFotoConversacion(): string {

    if (
      this.conversacion?.fotoGrupo &&
      this.conversacion.fotoGrupo.trim() != ''
    ) {
      return this.conversacion.fotoGrupo;
    }

    const participanteVisible =
      this.conversacion?.participantes?.[0];

    return participanteVisible?.fotoPerfil || '';
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

  debeMostrarRemitente(
    mensaje: Mensaje
  ): boolean {

    return this.esConversacionGrupal() &&
      this.obtenerNombreRemitente(
        mensaje
      ) != '';
  }

  obtenerNombreRemitente(
    mensaje: Mensaje
  ): string {

    if (this.esMensajePropio(mensaje)) {
      return 'Tu';
    }

    return mensaje.remitenteNombre ||
      mensaje.usuarioNombre ||
      mensaje.nombreUsuario ||
      mensaje.remitenteUsuario ||
      mensaje.usuario ||
      (
        mensaje.remitenteId != null
          ? `Usuario ${mensaje.remitenteId}`
          : ''
      );
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

    return (mensaje.contenido || '')
      .trim();
  }

  tieneContenidoVisible(
    mensaje: Mensaje
  ): boolean {

    return this.obtenerContenido(
      mensaje
    ) != '';
  }

  tieneAdjuntoVisible(
    mensaje: Mensaje
  ): boolean {

    return Boolean(
      mensaje.tieneAdjunto &&
      mensaje.adjuntoUrl
    );
  }

  tieneImagenAdjunta(
    mensaje: Mensaje
  ): boolean {

    return this.tieneAdjuntoVisible(
      mensaje
    ) && this.esImagenMime(
      mensaje.adjuntoFormato
    );
  }

  tieneAdjuntoNoImagen(
    mensaje: Mensaje
  ): boolean {

    return this.tieneAdjuntoVisible(
      mensaje
    ) && !this.tieneImagenAdjunta(
      mensaje
    );
  }

  obtenerNombreAdjunto(
    mensaje: Mensaje
  ): string {

    return mensaje.adjuntoNombreOriginal ||
      this.obtenerDescripcionAdjunto(
        mensaje
      );
  }

  obtenerDescripcionAdjunto(
    mensaje: Mensaje
  ): string {

    if (this.tieneImagenAdjunta(mensaje)) {
      return 'Imagen adjunta';
    }

    if (mensaje.tipoMensaje == 'AUDIO') {
      return 'Audio adjunto';
    }

    if (mensaje.tipoMensaje == 'VIDEO') {
      return 'Video adjunto';
    }

    return 'Archivo adjunto';
  }

  obtenerTamanoHumano(
    bytes?: number
  ): string {

    if (bytes == null || bytes <= 0) {
      return '';
    }

    const unidades =
      ['B', 'KB', 'MB', 'GB'];

    let valor =
      bytes;

    let indice =
      0;

    while (
      valor >= 1024 &&
      indice < unidades.length - 1
    ) {
      valor /= 1024;
      indice++;
    }

    const decimales =
      valor >= 10 || indice == 0
        ? 0
        : 1;

    return `${valor.toFixed(decimales)} ${unidades[indice]}`;
  }

  obtenerHoraMensaje(
    mensaje: Mensaje
  ): string {

    return this.formatearHora(
      mensaje.horaEnvio ||
      mensaje.fechaEnvio
    );
  }

  obtenerSiglaAdjunto(
    tipoMensaje?: string
  ): string {

    if (tipoMensaje == 'VIDEO') {
      return 'VD';
    }

    if (tipoMensaje == 'AUDIO') {
      return 'AU';
    }

    return 'AR';
  }

  private subirAdjunto(
    mensajeCreado: Mensaje,
    archivo: File,
    contenido: string
  ): void {

    this.mensajeService
      .subirAdjunto(
        mensajeCreado.id as number,
        archivo,
        this.fraseSecreta
      )
      .subscribe({

        next: (mensajeActualizado) => {
          this.finalizarEnvioExitoso(
            this.obtenerTituloEnvioAdjunto(
              mensajeActualizado,
              contenido
            )
          );
        },

        error: (err) => {
          this.manejarErrorAdjunto(
            err,
            mensajeCreado.id as number,
            contenido
          );
        }
      });
  }

  private manejarErrorAdjunto(
    err: any,
    mensajeId: number,
    contenido: string
  ): void {

    if (contenido == '') {
      this.mensajeService
        .eliminarMensaje(mensajeId)
        .subscribe({
          next: () => {},
          error: () => {}
        });
    } else {
      this.nuevoMensaje = '';
      this.limpiarAdjuntoSeleccionado();
    }

    this.enviando = false;
    this.marcarCambio();

    this.toastService.error(
      'No se pudo subir el archivo',
      contenido == ''
        ? this.obtenerMensajeError(
            err,
            'El archivo no se pudo enviar'
          )
        : this.obtenerMensajeError(
            err,
            'El texto se envio, pero el archivo no se pudo adjuntar'
          )
    );
  }

  private finalizarEnvioExitoso(
    titulo: string
  ): void {

    this.nuevoMensaje = '';
    this.limpiarAdjuntoSeleccionado();
    this.enviando = false;
    this.marcarCambio();

    if (
      this.conversacion?.id &&
      this.conversacion.fraseSecretaConfigurada
    ) {
      this.guardarFraseSecreta(
        this.conversacion.id,
        this.fraseSecreta
      );
    }

    this.toastService.success(
      titulo
    );

    this.cargarMensajes();
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

  private obtenerTituloEnvioAdjunto(
    mensaje: Mensaje,
    contenido: string
  ): string {

    const descripcion =
      this.obtenerDescripcionAdjunto(
        mensaje
      );

    if (contenido == '') {
      return descripcion;
    }

    return `Mensaje y ${descripcion.toLowerCase()}`;
  }

  private obtenerMensajeError(
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

  private reiniciarRefresco(): void {

    this.refrescoSub?.unsubscribe();

    this.refrescoSub =
      interval(7000)
        .subscribe(() => {
          this.cargarMensajes(false);
        });
  }

  private obtenerFraseGuardada(
    conversacionId: number
  ): string {

    return sessionStorage.getItem(
      this.obtenerClaveFrase(
        conversacionId
      )
    ) || '';
  }

  private guardarFraseSecreta(
    conversacionId: number,
    frase: string
  ): void {

    sessionStorage.setItem(
      this.obtenerClaveFrase(
        conversacionId
      ),
      frase.trim()
    );
  }

  private actualizarUltimoMensajeConversacion(): void {

    if (
      !this.conversacion?.id ||
      this.mensajes.length == 0
    ) {
      return;
    }

    const ultimoMensaje =
      this.mensajes[
        this.mensajes.length - 1
      ];

    const vistaPrevia =
      this.obtenerVistaPreviaMensaje(
        ultimoMensaje
      );

    if (vistaPrevia == '') {
      return;
    }

    this.conversacion.ultimoMensaje =
      vistaPrevia;

    sessionStorage.setItem(
      this.obtenerClaveUltimoMensaje(
        this.conversacion.id
      ),
      vistaPrevia
    );
  }

  private limpiarUltimoMensajeConversacion(): void {

    if (!this.conversacion?.id) {
      return;
    }

    this.conversacion.ultimoMensaje = '';

    sessionStorage.removeItem(
      this.obtenerClaveUltimoMensaje(
        this.conversacion.id
      )
    );
  }

  private obtenerVistaPreviaMensaje(
    mensaje: Mensaje
  ): string {

    if (mensaje.contenidoProtegido) {
      return '';
    }

    const contenido =
      this.obtenerContenido(mensaje);

    if (contenido != '') {
      return contenido;
    }

    if (this.tieneAdjuntoVisible(mensaje)) {
      return this.obtenerDescripcionAdjunto(
        mensaje
      );
    }

    return '';
  }

  private tieneMensajeVisible(
    mensaje: Mensaje
  ): boolean {

    return this.tieneContenidoVisible(
      mensaje
    ) || this.tieneAdjuntoVisible(
      mensaje
    );
  }

  private esConversacionGrupal(): boolean {

    return this.conversacion?.tipoConversacion ==
      'GRUPAL';
  }

  obtenerTipoMensajeAdjunto(
    archivo: File | null
  ): string {

    if (archivo == null) {
      return 'TEXTO';
    }

    const mime =
      (archivo.type || '')
        .toLowerCase();

    if (mime.startsWith('image/')) {
      return 'IMAGEN';
    }

    if (mime.startsWith('audio/')) {
      return 'AUDIO';
    }

    if (mime.startsWith('video/')) {
      return 'VIDEO';
    }

    return 'ARCHIVO';
  }

  private esImagenMime(
    mime?: string | null
  ): boolean {

    return (mime || '')
      .toLowerCase()
      .startsWith('image/');
  }

  private limpiarAdjuntoSeleccionado(): void {

    this.archivoAdjunto = null;
    this.archivoPreviewEsImagen = false;
    this.limpiarPreviewAnterior();

    if (this.adjuntoInput?.nativeElement) {
      this.adjuntoInput.nativeElement.value = '';
    }
  }

  private limpiarPreviewAnterior(): void {

    if (this.archivoPreviewUrl != null) {
      URL.revokeObjectURL(
        this.archivoPreviewUrl
      );
    }

    this.archivoPreviewUrl = null;
  }

  private obtenerClaveFrase(
    conversacionId: number
  ): string {

    return `fraseSecretaConversacion:${this.idUsuarioActual}:${conversacionId}`;
  }

  private obtenerClaveUltimoMensaje(
    conversacionId: number
  ): string {

    return `ultimoMensajeConversacion:${this.idUsuarioActual}:${conversacionId}`;
  }

  private marcarCambio(): void {

    setTimeout(() => {
      if (!this.destruido) {
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
