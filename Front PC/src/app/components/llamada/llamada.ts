import {
  Component,
  Input,
  Output,
  EventEmitter,
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
export class LlamadaComponent implements OnDestroy {

  @Input() llamada!: LlamadaRespuesta;

  @Output() colgar = new EventEmitter<void>();

  conectado: boolean = false;
  error: string = '';

  private cliente: IAgoraRTCClient | null = null;
  private audioTrack: ILocalAudioTrack | null = null;
  private videoTrack: ILocalVideoTrack | null = null;


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

  async salir(): Promise<void> {
    this.audioTrack?.stop();
    this.audioTrack?.close();
    this.videoTrack?.stop();
    this.videoTrack?.close();
    await this.cliente?.leave();
    this.cliente = null;
    this.conectado = false;
    this.colgar.emit();
  }

  ngOnDestroy(): void {
    this.salir();
  }
}
