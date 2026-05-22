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
    id: number
  ): Observable<any[]> {

    return this.http.get<any[]>(

      `${this.apiUrl}/conversacion/${id}`

    );
  }

  enviarMensaje(

    usuario: string,

    conversacionId: number,

    contenido: string

  ): Observable<any> {

    const body = null;

    const params =
      new HttpParams()

        .set(
          'usuario',
          usuario
        )

        .set(
          'conversacionId',
          conversacionId.toString()
        )

        .set(
          'tipoMensaje',
          'TEXTO'
        )

        .set(
          'contenido',
          contenido
        );

    return this.http.post(

      this.apiUrl,

      body,

      { params }

    );
  }

}
