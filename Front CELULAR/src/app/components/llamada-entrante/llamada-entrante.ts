import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { Llamada, LlamadaRespuesta } from '../../models/llamada';
import { LlamadaService } from '../../services/llamada.service';

@Component({
  selector: 'app-llamada-entrante',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './llamada-entrante.html',
  styleUrl: './llamada-entrante.css',
})
export class LlamadaEntranteComponent {
  @Input() llamada!: Llamada;
  @Input() idUsuarioActual!: number;

  @Output()
  readonly aceptada = new EventEmitter<LlamadaRespuesta>();

  @Output()
  readonly rechazada = new EventEmitter<void>();

  constructor(private llamadaService: LlamadaService) {}

  obtenerNombreLlamante(): string {
    return this.llamada.usuarioLlamanteNombre || 'Usuario en llamada';
  }

  aceptar(): void {
    const llamadaId = this.llamada.id;

    if (llamadaId === undefined || llamadaId === null) {
      this.rechazada.emit();
      return;
    }

    this.llamadaService.aceptarLlamada(llamadaId, this.idUsuarioActual).subscribe({
      next: (respuesta) => {
        this.aceptada.emit(respuesta);
      },
      error: () => {
        this.rechazada.emit();
      },
    });
  }

  rechazar(): void {
    const llamadaId = this.llamada.id;

    if (llamadaId === undefined || llamadaId === null) {
      this.rechazada.emit();
      return;
    }

    this.llamadaService.rechazarLlamada(llamadaId).subscribe({
      next: () => {
        this.rechazada.emit();
      },
      error: () => {
        this.rechazada.emit();
      },
    });
  }
}
