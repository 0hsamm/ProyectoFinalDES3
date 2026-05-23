import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy
} from '@angular/core';

import { CommonModule } from '@angular/common';

import AgoraRTC, {
  IAgoraRTCClient,
  ILocalAudioTrack,
  ILocalVideoTrack
} from 'agora-rtc-sdk-ng';

import { LlamadaRespuesta } from '../../models/llamada';

@Component({
  selector: 'app-llamada',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './llamada.html',
  styleUrl: './llamada.css'
})
export class LlamadaComponent implements OnInit, OnDestroy {

  @Input() llamada!: LlamadaRespuesta;
  @Input() idUsuarioActual?: number;

  @Output() colgar = new EventEmitter<void>();

  conectado: boolean = false;
  error: string = '';
  duracionSegundos: number = 0;

  private cliente: IAgoraRTCClient | null = null;
  private audioTrack: ILocalAudioTrack | null = null;
  private videoTrack: ILocalVideoTrack | null = null;
  private contadorDuracion?: ReturnType<typeof setInterval>;
  private cerrando: boolean = false;

  ngOnInit(): void {

    this.actualizarDuracion();

    this.contadorDuracion =
      setInterval(() => {
        this.actualizarDuracion();
      }, 1000);
  }

  async unirse(): Promise<void> {
    try {
      this.cliente = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' });

      this.cliente.on(
        'user-published',
        async (user: any, mediaType: 'audio' | 'video') => {
          if (!this.cliente) {
            return;
          }

          await this.cliente.subscribe(user, mediaType);

          if (mediaType == 'audio') {
            user.audioTrack?.play();
          }

          if (mediaType == 'video') {
            user.videoTrack?.play('video-remoto');
          }
        }
      );

      await this.cliente.join(
        this.llamada.appIdAgora,
        this.llamada.canalAgora,
        this.llamada.tokenAgora || null,
        this.llamada.uidAgora
      );

      this.audioTrack = await AgoraRTC.createMicrophoneAudioTrack()
        .catch(() => null);

      if (this.llamada.tipoLlamada == 'VIDEO') {

        this.videoTrack = await AgoraRTC.createCameraVideoTrack()
          .catch(() => null);

        this.videoTrack?.play('video-local');
      }

      const tracks =
        [
          this.audioTrack,
          this.videoTrack
        ].filter(Boolean) as Array<
          ILocalAudioTrack | ILocalVideoTrack
        >;

      if (tracks.length > 0) {
        await this.cliente.publish(tracks);
      }

      this.conectado = true;

    } catch (e: any) {
      this.error = 'No se pudo conectar: ' + (e?.message || e);
    }
  }

  async salir(
    emitirEvento: boolean = true
  ): Promise<void> {

    if (this.cerrando) {
      return;
    }

    this.cerrando = true;
    this.limpiarContador();
    this.audioTrack?.stop();
    this.audioTrack?.close();
    this.videoTrack?.stop();
    this.videoTrack?.close();
    await this.cliente?.leave();
    this.cliente = null;
    this.conectado = false;

    if (emitirEvento) {
      this.colgar.emit();
    }
  }

  ngOnDestroy(): void {
    this.salir(false);
  }

  obtenerNombreContraparte(): string {

    if (
      this.idUsuarioActual != null &&
      this.llamada.usuarioLlamanteId ==
      this.idUsuarioActual
    ) {
      return this.llamada.usuarioReceptorNombre ||
        'Usuario en llamada';
    }

    return this.llamada.usuarioLlamanteNombre ||
      'Usuario en llamada';
  }

  obtenerDuracion(): string {

    const horas =
      Math.floor(this.duracionSegundos / 3600);

    const minutos =
      Math.floor(
        (this.duracionSegundos % 3600) / 60
      );

    const segundos =
      this.duracionSegundos % 60;

    const partes =
      horas > 0
        ? [horas, minutos, segundos]
        : [minutos, segundos];

    return partes
      .map((parte) => String(parte).padStart(2, '0'))
      .join(':');
  }

  private actualizarDuracion(): void {

    const inicio =
      this.llamada?.fechaInicio
        ? new Date(this.llamada.fechaInicio).getTime()
        : Date.now();

    this.duracionSegundos =
      Math.max(
        0,
        Math.floor((Date.now() - inicio) / 1000)
      );
  }

  private limpiarContador(): void {

    if (this.contadorDuracion) {
      clearInterval(this.contadorDuracion);
      this.contadorDuracion = undefined;
    }
  }
}
