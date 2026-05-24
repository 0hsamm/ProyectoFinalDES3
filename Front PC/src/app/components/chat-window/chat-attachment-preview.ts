import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chat-attachment-preview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-attachment-preview.html',
  styleUrl: './chat-attachment-preview.css'
})
export class ChatAttachmentPreviewComponent {

  @Input() archivoAdjunto: File | null = null;

  @Input() archivoPreviewUrl: string | null = null;

  @Input() archivoPreviewEsImagen = false;

  @Output() readonly quitar = new EventEmitter<void>();

  obtenerSiglaAdjunto(): string {

    if (this.archivoAdjunto == null) {
      return 'AR';
    }

    const mime =
      (this.archivoAdjunto.type || '')
        .toLowerCase();

    if (mime.startsWith('video/')) {
      return 'VD';
    }

    if (mime.startsWith('audio/')) {
      return 'AU';
    }

    return 'AR';
  }

  obtenerTamanoHumano(): string {

    const bytes =
      this.archivoAdjunto?.size;

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
      valor >= 10 || indice === 0
        ? 0
        : 1;

    return `${valor.toFixed(decimales)} ${unidades[indice]}`;
  }
}
