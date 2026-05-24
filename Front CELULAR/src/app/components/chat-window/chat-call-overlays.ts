import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  Llamada,
  LlamadaRespuesta
} from '../../models/llamada';

import { LlamadaComponent } from '../llamada/llamada';
import { LlamadaEntranteComponent } from '../llamada-entrante/llamada-entrante';

@Component({
  selector: 'app-chat-call-overlays',
  standalone: true,
  imports: [
    CommonModule,
    LlamadaComponent,
    LlamadaEntranteComponent
  ],
  templateUrl: './chat-call-overlays.html',
  styleUrl: './chat-call-overlays.css'
})
export class ChatCallOverlaysComponent {

  @Input() llamadaActiva: LlamadaRespuesta | null = null;

  @Input() llamadaEntrante: Llamada | null = null;

  @Input() idUsuarioActual?: number;

  @Output() readonly colgar = new EventEmitter<void>();

  @Output() readonly aceptada =
    new EventEmitter<LlamadaRespuesta>();

  @Output() readonly rechazada =
    new EventEmitter<void>();
}
