import { Injectable }
  from '@angular/core';

import { HttpClient }
  from '@angular/common/http';

import { Observable }
  from 'rxjs';

import { environment }
  from '../../environment/environment';

import { Conversacion }
  from '../models/conversacion';

@Injectable({

  providedIn: 'root'

})

export class ConversacionService {

  private apiUrl =
    environment.apiUrl +
    '/conversaciones';

  constructor(
    private http: HttpClient
  ) {}

  obtenerConversaciones():
    Observable<Conversacion[]> {

    return this.http.get<Conversacion[]>(

      this.apiUrl + '/mis'

    );
  }

  crearConversacionPrivada(
    participanteAId: number,
    participanteBId: number,
    fraseSecreta: string
  ): Observable<string> {

    return this.http.post(
      this.apiUrl,
      {
        tipoConversacion: 'PRIVADA',
        participantesIds: [
          participanteAId,
          participanteBId
        ],
        fraseSecreta
      },
      {
        responseType: 'text'
      }
    );
  }

}
