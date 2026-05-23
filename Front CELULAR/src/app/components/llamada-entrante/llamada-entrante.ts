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
  styleUrl: './llamada-entrante.css'
})
export class LlamadaEntranteComponent {

  @Input() llamada!: Llamada;
  @Input() idUsuarioActual!: number;

  @Output() aceptada = new EventEmitter<LlamadaRespuesta>();
  @Output() rechazada = new EventEmitter<void>();

  constructor(private llamadaService: LlamadaService) {}

  obtenerNombreLlamante(): string {

    return this.llamada.usuarioLlamanteNombre ||
      'Usuario en llamada';
  }

  aceptar(): void {
    this.llamadaService
      .aceptarLlamada(this.llamada.id!, this.idUsuarioActual)
      .subscribe({
        next: (respuesta) => this.aceptada.emit(respuesta),
        error: () => this.rechazada.emit()
      });
  }

  rechazar(): void {
    this.llamadaService
      .rechazarLlamada(this.llamada.id!)
      .subscribe({ next: () => {}, error: () => {} });
    this.rechazada.emit();
  }
}
