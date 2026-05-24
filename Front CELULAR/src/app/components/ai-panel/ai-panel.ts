import {
  Component,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { HistorialIa } from '../../models/ia';

import { IaService } from '../../services/ia.service';

import { ToastService } from '../../services/toast.service';

import { ChangeDetectorRef } from '@angular/core';

interface AiMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Component({
  selector: 'app-ai-panel',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './ai-panel.html',
  styleUrl: './ai-panel.css'
})
export class AiPanelComponent
  implements OnInit {

  mensajes: AiMessage[] = [];

  historial: HistorialIa[] = [];

  pregunta = '';

  enviando = false;

  cargandoHistorial = false;

  constructor(
    private iaService: IaService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.cargarHistorial();
  }

  enviar(): void {

    const pregunta =
      this.pregunta.trim();

    if (pregunta === '') {

      return;
    }

    this.mensajes.push({
      role: 'user',
      content: pregunta
    });

    this.pregunta = '';

    this.enviando = true;

    this.iaService
      .consultar({
        mensaje: pregunta
      })
      .subscribe({
        next: (respuesta) => {
          this.enviando = false;
          this.mensajes.push({
            role: 'assistant',
            content: respuesta.respuesta
          });
          this.cdr.detectChanges();
          this.cargarHistorial();
        },
        error: (err) => {
          this.enviando = false;
          this.toastService.error(
            'IA no disponible',
            AiPanelComponent.obtenerMensajeError(err)
          );
        }
      });
  }

  cargarHistorial(): void {

    this.cargarHistorialInterno(false);
  }

  refrescarHistorial(): void {

    this.cargarHistorialInterno(true);
  }

  private cargarHistorialInterno(
    mostrarToastError: boolean
  ): void {

    this.cargandoHistorial = true;

    this.iaService
      .obtenerHistorial()
      .subscribe({
        next: (historial) => {
          this.historial = historial || [];
          this.cargandoHistorial = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.cargandoHistorial = false;
          this.cdr.detectChanges();

          if (mostrarToastError) {
            this.toastService.warning(
              'Historial no disponible',
              'No se pudo actualizar el historial de IA'
            );
          }
        }
      });
  }

  private static obtenerMensajeError(err: unknown): string {
    const error = err as Record<string, unknown>;
    if (typeof (error?.['error'] as Record<string, unknown>)?.['error'] === 'string') return (error['error'] as Record<string, unknown>)['error'] as string;
    if (typeof (error?.['error'] as Record<string, unknown>)?.['mensaje'] === 'string') return (error['error'] as Record<string, unknown>)['mensaje'] as string;
    if (typeof (error?.['error'] as Record<string, unknown>)?.['message'] === 'string') return (error['error'] as Record<string, unknown>)['message'] as string;
    if (typeof error?.['error'] === 'string') return error['error'] as string;
    return 'Revisa la configuración de GEMINI_API_KEY en el backend';
  }
}
