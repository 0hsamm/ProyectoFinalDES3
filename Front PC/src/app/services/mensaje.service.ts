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
    environment.apiUrl +
    '/mensajes';

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
      fraseSecreta.trim() != ''
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

    contenido: string,

    fraseSecreta?: string,

    tipoMensaje: string = 'TEXTO'

  ): Observable<any> {

    const body: Mensaje = {

      remitenteId,

      conversacionId,

      tipoMensaje,

      contenido,

      fraseSecreta
    };

    return this.http.post(

      this.apiUrl,

      body,

      {
        responseType: 'text'
      }

    );
  }

}
