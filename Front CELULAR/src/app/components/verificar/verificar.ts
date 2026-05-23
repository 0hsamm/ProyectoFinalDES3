import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { ActivatedRoute } from '@angular/router';

import { Router } from '@angular/router';

import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environment/environment';

@Component({

  selector: 'app-verificar',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl: './verificar.html',

  styleUrl: './verificar.css'
})
export class Verificar implements OnInit {

  cargando: boolean = true;

  correcto: boolean = false;

  mensaje: string = '';

  constructor(

    private route: ActivatedRoute,

    private http: HttpClient,

    private router: Router
  ) {}

  ngOnInit(): void {

    const token = this.route
      .snapshot
      .queryParamMap
      .get('token');

    if (
      token == null ||
      token.trim() == ''
    ) {

      this.correcto = false;

      this.mensaje =
        'Token invalido';

      this.cargando = false;

      return;
    }

    this.http.get(

      environment.apiUrl +
      '/auth/verificar?token=' +
      token,

      {
        responseType: 'text'
      }

    ).subscribe({

      next: () => {

        this.correcto = true;

        this.mensaje =
          'Cuenta verificada correctamente';

        this.cargando = false;

        setTimeout(() => {

          this.router.navigate(['/']);

        }, 3000);
      },

      error: () => {

        this.correcto = false;

        this.mensaje =
          'No se pudo verificar la cuenta';

        this.cargando = false;
      }
    });
  }
}
