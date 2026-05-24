import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import {
  catchError,
  finalize,
  of
} from 'rxjs';

import { environment } from '../../environment/environment';

import {
  LoginDTO,
  RegistroDTO,
  RespuestaAutenticacionDTO
} from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient
  ) {}

  login(
    usuario: string,
    contrasena: string
  ): Observable<RespuestaAutenticacionDTO> {

    const datos: LoginDTO = {

      usuario,

      contrasena
    };

    return this.http.post<RespuestaAutenticacionDTO>(
      `${this.apiUrl}/login`,
      datos
    );
  }

  registro(
    datos: RegistroDTO
  ): Observable<string> {

    return this.http.post(
      `${this.apiUrl}/registro`,
      datos,
      {
        responseType: 'text'
      }
    );
  }

  static guardarSesion(
    respuesta: RespuestaAutenticacionDTO
  ): void {

    localStorage.setItem(
      'token',
      respuesta.token
    );

    localStorage.setItem(
      'tipoToken',
      respuesta.tipoToken
    );

    localStorage.setItem(
      'idUsuario',
      respuesta.id.toString()
    );

    localStorage.setItem(
      'usuario',
      respuesta.usuario
    );

    localStorage.setItem(
      'correo',
      respuesta.correo
    );

    localStorage.setItem(
      'nombrePersona',
      respuesta.nombrePersona
    );

    if (respuesta.fotoPerfil) {

      localStorage.setItem(
        'fotoPerfil',
        respuesta.fotoPerfil
      );
    } else {

      localStorage.removeItem('fotoPerfil');
    }
  }

  static obtenerToken(): string | null {

    return localStorage.getItem('token');
  }
// skipcq: JS-0105
  estaLogueado(): boolean {

    return AuthService.obtenerToken() !== null;
  }

  cerrarSesion(): Observable<string> {

    return this.http.post(
      `${this.apiUrl}/logout`,
      {},
      {
        responseType: 'text'
      }
    ).pipe(
      catchError(() =>
        of(
          'Sesion cerrada localmente'
        )
      ),
      finalize(() => {
        AuthService.limpiarSesionLocal();
      })
    );
  }

  private static limpiarSesionLocal(): void {

    localStorage.removeItem('token');
    localStorage.removeItem('tipoToken');
    localStorage.removeItem('idUsuario');
    localStorage.removeItem('usuario');
    localStorage.removeItem('correo');
    localStorage.removeItem('nombrePersona');
    localStorage.removeItem('fotoPerfil');

    sessionStorage.clear();
  }
}
