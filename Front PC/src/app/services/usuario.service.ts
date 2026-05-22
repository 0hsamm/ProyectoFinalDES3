import { Injectable }
  from '@angular/core';

import { HttpClient }
  from '@angular/common/http';

import { Observable }
  from 'rxjs';

import {environment} from
    '../../environment/environment';


@Injectable({
  providedIn: 'root'
})

export class UsuarioService {

  private apiUrl =
    environment.apiUrl + '/usuarios';

  constructor(
    private http: HttpClient
  ) {}

  obtenerUsuarios():
    Observable<any> {

    return this.http.get(
      this.apiUrl
    );
  }

  obtenerUsuarioPorId(
    id: number
  ): Observable<any> {

    return this.http.get(
      `${this.apiUrl}/${id}`
    );
  }

  buscarPorUsername(
    username: string
  ): Observable<any> {

    return this.http.get(
      `${this.apiUrl}/username/${username}`
    );
  }

}
