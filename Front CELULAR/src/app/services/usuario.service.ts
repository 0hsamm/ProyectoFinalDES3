import { Injectable }
  from '@angular/core';

import { HttpClient }
  from '@angular/common/http';

import { Observable }
  from 'rxjs';

import {environment} from
    '../../environment/environment';

import { Usuario } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})

export class UsuarioService {

  private apiUrl =
    `${environment.apiUrl}/usuarios`;

  constructor(
    private http: HttpClient
  ) {}

  obtenerUsuarios():
    Observable<Usuario[]> {

    return this.http.get<Usuario[]>(
      this.apiUrl
    );
  }

  obtenerUsuarioPorId(
    id: number
  ): Observable<Usuario> {

    return this.http.get<Usuario>(
      `${this.apiUrl}/${id}`
    );
  }

  buscarPorUsername(
    username: string
  ): Observable<Usuario> {

    return this.http.get<Usuario>(
      `${this.apiUrl}/username/${username}`
    );
  }

  actualizarUsuario(
    id: number,
    usuario: Partial<Usuario>
  ): Observable<string> {

    return this.http.put(
      `${this.apiUrl}/${id}`,
      usuario,
      {
        responseType: 'text'
      }
    );
  }

  actualizarFotoPerfil(
    id: number,
    archivo: File
  ): Observable<Usuario> {

    const formData =
      new FormData();

    formData.append(
      'archivo',
      archivo
    );

    return this.http.post<Usuario>(
      `${this.apiUrl}/${id}/foto-perfil`,
      formData
    );
  }

}
