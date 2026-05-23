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

  pregunta: string = '';

  enviando: boolean = false;

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

    if (pregunta == '') {

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
            this.obtenerMensajeError(err)
          );
        }
      });
  }

  cargarHistorial(): void {

    this.iaService
      .obtenerHistorial()
      .subscribe({
        next: (historial) => {
          this.historial = historial || [];
        },
        error: () => {}
      });
  }

  private obtenerMensajeError(
    err: any
  ): string {

    if (typeof err?.error?.error == 'string') {

      return err.error.error;
    }

    if (typeof err?.error?.mensaje == 'string') {

      return err.error.mensaje;
    }

    if (typeof err?.error?.message == 'string') {

      return err.error.message;
    }

    if (typeof err?.error == 'string') {

      return err.error;
    }

    return 'Revisa la configuracion de GEMINI_API_KEY en el backend';
  }
}
