import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../environment/environment';

import {
  Amistad,
  CrearSolicitudAmistadDTO,
  SolicitudAmistad
} from '../models/amistad';

@Injectable({
  providedIn: 'root'
})
export class AmistadService {

  private apiUrl =
    `${environment.apiUrl}/amistades`;

  constructor(
    private http: HttpClient
  ) {}

  obtenerAmigos(): Observable<Amistad[]> {

    return this.http.get<Amistad[]>(
      this.apiUrl
    );
  }

  enviarSolicitud(
    usernameDestino: string
  ): Observable<string> {

    const body: CrearSolicitudAmistadDTO = {
      usernameDestino
    };

    return this.http.post(
      `${this.apiUrl}/solicitudes`,
      body,
      {
        responseType: 'text'
      }
    );
  }

  obtenerSolicitudesRecibidas():
    Observable<SolicitudAmistad[]> {

    return this.http.get<SolicitudAmistad[]>(
      `${this.apiUrl}/solicitudes/recibidas`
    );
  }

  obtenerSolicitudesEnviadas():
    Observable<SolicitudAmistad[]> {

    return this.http.get<SolicitudAmistad[]>(
      `${this.apiUrl}/solicitudes/enviadas`
    );
  }

  aceptarSolicitud(
    id: number
  ): Observable<string> {

    return this.http.post(
      `${this.apiUrl}/solicitudes/${id}/aceptar`,
      null,
      {
        responseType: 'text'
      }
    );
  }

  rechazarSolicitud(
    id: number
  ): Observable<string> {

    return this.http.post(
      `${this.apiUrl}/solicitudes/${id}/rechazar`,
      null,
      {
        responseType: 'text'
      }
    );
  }

  cancelarSolicitud(
    id: number
  ): Observable<string> {

    return this.http.delete(
      `${this.apiUrl}/solicitudes/${id}`,
      {
        responseType: 'text'
      }
    );
  }

  eliminarAmistad(
    usuarioId: number
  ): Observable<string> {

    return this.http.delete(
      `${this.apiUrl}/${usuarioId}`,
      {
        responseType: 'text'
      }
    );
  }
}
