import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../environment/environment';

import {
  Llamada,
  LlamadaRespuesta
} from '../models/llamada';

@Injectable({
  providedIn: 'root'
})
export class LlamadaService {

  private apiUrl =
    environment.apiUrl + '/api/llamadas';

  constructor(
    private http: HttpClient
  ) {}

  iniciarLlamada(
    llamada: Llamada
  ): Observable<LlamadaRespuesta> {

    return this.http.post<LlamadaRespuesta>(
      this.apiUrl + '/iniciar',
      llamada
    );
  }

  finalizarLlamada(
    id: number
  ): Observable<string> {

    return this.http.put(
      `${this.apiUrl}/${id}/finalizar`,
      null,
      {
        responseType: 'text'
      }
    );
  }

  rechazarLlamada(
    id: number
  ): Observable<string> {

    return this.http.put(
      `${this.apiUrl}/${id}/rechazar`,
      null,
      {
        responseType: 'text'
      }
    );
  }

  obtenerPorConversacion(
    conversacionId: number
  ): Observable<Llamada[]> {

    return this.http.get<Llamada[]>(
      `${this.apiUrl}/conversacion/${conversacionId}`
    );
  }

  obtenerHistorialUsuario(
    usuarioId: number
  ): Observable<Llamada[]> {

    return this.http.get<Llamada[]>(
      `${this.apiUrl}/historial/${usuarioId}`
    );
  }
  aceptarLlamada(
    llamadaId: number,
    usuarioReceptorId: number
  ): Observable<LlamadaRespuesta> {
    return this.http.put<LlamadaRespuesta>(
      `${this.apiUrl}/${llamadaId}/aceptar/${usuarioReceptorId}`,
      null
    );
  }

  obtenerLlamadasPendientes(
    usuarioId: number
  ): Observable<Llamada[]> {
    return this.http.get<Llamada[]>(
      `${this.apiUrl}/historial/${usuarioId}`
    );
  }

}
