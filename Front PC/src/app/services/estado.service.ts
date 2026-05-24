import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { HttpParams } from '@angular/common/http';

import { environment } from '../../environment/environment';

import {
  Estado,
  EstadoInteraccion
} from '../models/estado';

@Injectable({
  providedIn: 'root'
})
export class EstadoService {

  private apiUrl = `${environment.apiUrl}/estados`;

  constructor(
    private http: HttpClient
  ) {}

  obtenerEstadosActivos(
    usuarioId?: number
  ): Observable<Estado[]> {

    let params =
      new HttpParams();

    if (usuarioId) {

      params = params.set(
        'usuarioId',
        usuarioId.toString()
      );
    }

    return this.http.get<Estado[]>(
      this.apiUrl,
      {
        params
      }
    );
  }

  obtenerEstadosUsuario(
    usuarioId: number,
    viewerId?: number
  ): Observable<Estado[]> {

    let params =
      new HttpParams();

    if (viewerId) {

      params = params.set(
        'viewerId',
        viewerId.toString()
      );
    }

    return this.http.get<Estado[]>(
      `${this.apiUrl}/usuario/${usuarioId}`,
      {
        params
      }
    );
  }

  crearEstadoTexto(
    usuarioId: number,
    texto: string
  ): Observable<Estado> {

    const formData =
      new FormData();

    formData.append(
      'usuarioId',
      usuarioId.toString()
    );

    formData.append(
      'texto',
      texto
    );

    formData.append(
      'tipo',
      'TEXTO'
    );

    return this.http.post<Estado>(
      this.apiUrl,
      formData
    );
  }

  crearEstadoArchivo(
    usuarioId: number,
    tipo: 'IMAGEN' | 'VIDEO',
    archivo: File,
    texto?: string
  ): Observable<Estado> {

    const formData =
      new FormData();

    formData.append(
      'usuarioId',
      usuarioId.toString()
    );

    formData.append(
      'tipo',
      tipo
    );

    formData.append(
      'archivo',
      archivo
    );

    if (texto?.trim()) {

      formData.append(
        'texto',
        texto.trim()
      );
    }

    return this.http.post<Estado>(
      this.apiUrl,
      formData
    );
  }

  registrarVista(
    estadoId: number,
    usuarioId: number
  ): Observable<string> {

    const formData =
      new FormData();

    formData.append(
      'usuarioId',
      usuarioId.toString()
    );

    return this.http.post(
      `${this.apiUrl}/${estadoId}/ver`,
      formData,
      {
        responseType: 'text'
      }
    );
  }

  darLike(
    estadoId: number,
    usuarioId: number
  ): Observable<Estado> {

    const formData =
      new FormData();

    formData.append(
      'usuarioId',
      usuarioId.toString()
    );

    return this.http.post<Estado>(
      `${this.apiUrl}/${estadoId}/like`,
      formData
    );
  }

  quitarLike(
    estadoId: number,
    usuarioId: number
  ): Observable<Estado> {

    const params =
      new HttpParams()
        .set(
          'usuarioId',
          usuarioId.toString()
        );

    return this.http.delete<Estado>(
      `${this.apiUrl}/${estadoId}/like`,
      {
        params
      }
    );
  }

  eliminarEstado(
    estadoId: number,
    usuarioId: number
  ): Observable<string> {

    const params =
      new HttpParams()
        .set(
          'usuarioId',
          usuarioId.toString()
        );

    return this.http.delete(
      `${this.apiUrl}/${estadoId}`,
      {
        params,
        responseType: 'text'
      }
    );
  }

  obtenerVistasDetalle(
    estadoId: number,
    usuarioId: number
  ): Observable<EstadoInteraccion[]> {

    return this.obtenerDetalle(
      estadoId,
      usuarioId,
      'vistas/detalle'
    );
  }

  obtenerLikesDetalle(
    estadoId: number,
    usuarioId: number
  ): Observable<EstadoInteraccion[]> {

    return this.obtenerDetalle(
      estadoId,
      usuarioId,
      'likes'
    );
  }

  private obtenerDetalle(
    estadoId: number,
    usuarioId: number,
    ruta: string
  ): Observable<EstadoInteraccion[]> {

    const params =
      new HttpParams()
        .set(
          'usuarioId',
          usuarioId.toString()
        );

    return this.http.get<EstadoInteraccion[]>(
      `${this.apiUrl}/${estadoId}/${ruta}`,
      {
        params
      }
    );
  }
}
