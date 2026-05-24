import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../environment/environment';

import {
  HistorialIa,
  RespuestaIa,
  SolicitudIa
} from '../models/ia';

@Injectable({
  providedIn: 'root'
})
export class IaService {

  private apiUrl = `${environment.apiUrl}/api/ia`;

  constructor(
    private http: HttpClient
  ) {}

  consultar(
    solicitud: SolicitudIa
  ): Observable<RespuestaIa> {

    return this.http.post<RespuestaIa>(
      `${this.apiUrl}/chat`,
      solicitud
    );
  }

  obtenerHistorial(): Observable<HistorialIa[]> {

    return this.http.get<HistorialIa[]>(
      `${this.apiUrl}/historial`
    );
  }
}
