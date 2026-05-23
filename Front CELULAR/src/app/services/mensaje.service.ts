import { Injectable }
  from '@angular/core';

import {
  HttpClient,
  HttpParams
}
  from '@angular/common/http';

import { Observable }
  from 'rxjs';

import { environment }
  from '../../environment/environment';

import { Mensaje }
  from '../models/mensaje';

@Injectable({

  providedIn: 'root'

})

export class MensajeService {

  private apiUrl =
    `${environment.apiUrl}/mensajes`;

  constructor(
    private http: HttpClient
  ) {}

  obtenerMensajesPorConversacion(
    id: number,
    fraseSecreta?: string
  ): Observable<Mensaje[]> {

    let params =
      new HttpParams();

    if (
      fraseSecreta != null &&
      fraseSecreta.trim() !== ''
    ) {

      params = params.set(
        'fraseSecreta',
        fraseSecreta
      );
    }

    return this.http.get<Mensaje[]>(

      `${this.apiUrl}/conversacion/${id}`,

      { params }

    );
  }

  enviarMensaje(

    remitenteId: number,

    conversacionId: number,

    contenido: string | null,

    fraseSecreta?: string,

    tipoMensaje = 'TEXTO',

    tieneAdjunto = false

  ): Observable<Mensaje> {

    const body: Mensaje = {

      remitenteId,

      conversacionId,

      tipoMensaje,

      contenido,

      fraseSecreta,

      tieneAdjunto,

      contenidoProtegido: false
    };

    return this.http.post<Mensaje>(

      this.apiUrl,

      body

    );
  }

  subirAdjunto(
    mensajeId: number,
    archivo: File,
    fraseSecreta?: string
  ): Observable<Mensaje> {

    const formData =
      new FormData();

    formData.append(
      'archivo',
      archivo
    );

    let params =
      new HttpParams();

    if (
      fraseSecreta != null &&
      fraseSecreta.trim() !== ''
    ) {
      params = params.set(
        'fraseSecreta',
        fraseSecreta
      );
    }

    return this.http.post<Mensaje>(
      `${this.apiUrl}/${mensajeId}/adjunto`,
      formData,
      { params }
    );
  }

  eliminarMensaje(
    mensajeId: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${mensajeId}`
    );
  }

}
