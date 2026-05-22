import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

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

  private apiUrl = environment.apiUrl + '/auth';

  constructor(
    private http: HttpClient
  ) {}

  login(
    usuario: string,
    contrasena: string
  ): Observable<RespuestaAutenticacionDTO> {

    const datos: LoginDTO = {

      usuario: usuario,

      contrasena: contrasena
    };

    return this.http.post<RespuestaAutenticacionDTO>(
      this.apiUrl + '/login',
      datos
    );
  }

  registro(
    datos: RegistroDTO
  ): Observable<string> {

    return this.http.post(
      this.apiUrl + '/registro',
      datos,
      {
        responseType: 'text'
      }
    );
  }

  guardarSesion(
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
  }

  obtenerToken(): string | null {

    return localStorage.getItem('token');
  }

  estaLogueado(): boolean {

    return this.obtenerToken() != null;
  }

  cerrarSesion(): void {

    localStorage.clear();
  }
}
