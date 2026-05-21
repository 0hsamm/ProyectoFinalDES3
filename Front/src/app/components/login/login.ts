import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

import {
  RegistroDTO
} from '../../models/usuario';

@Component({

  selector: 'app-login',

  standalone: true,

  imports: [

    CommonModule,

    FormsModule
  ],

  templateUrl: './login.html',

  styleUrl: './login.css'
})
export class Login {

  modoRegistro: boolean = false;

  modoClaro: boolean = false;

  usuario: string = '';

  correo: string = '';

  nombrePersona: string = '';

  fechaNacimiento: string = '';

  contrasena: string = '';

  error: string = '';

  mensaje: string = '';

  cargando: boolean = false;

  constructor(

    private authService: AuthService,

    private router: Router
  ) {}

  toggleTheme(): void {

    this.modoClaro = !this.modoClaro;
  }

  cambiarModo(): void {

    this.modoRegistro = !this.modoRegistro;

    this.error = '';

    this.mensaje = '';
  }

  entrar(): void {

    this.error = '';

    this.mensaje = '';

    if (
      this.usuario.trim() == '' ||
      this.contrasena.trim() == ''
    ) {

      this.error =
        'Debes llenar todos los campos';

      return;
    }

    this.cargando = true;

    this.authService.login(
      this.usuario,
      this.contrasena
    ).subscribe({

      next: (respuesta) => {

        this.authService.guardarSesion(
          respuesta
        );

        this.cargando = false;

        this.router.navigate([
          '/chat'
        ]);
      },

      error: (err) => {

        console.log(err);

        this.cargando = false;

        if (
          typeof err.error == 'string'
        ) {

          this.error = err.error;

        } else {

          this.error =
            'Usuario o contraseña incorrectos';
        }
      }
    });
  }

  registrar(): void {

    this.error = '';

    this.mensaje = '';

    if (
      this.usuario.trim() == '' ||
      this.correo.trim() == '' ||
      this.nombrePersona.trim() == '' ||
      this.fechaNacimiento.trim() == '' ||
      this.contrasena.trim() == ''
    ) {

      this.error =
        'Debes llenar todos los campos';

      return;
    }

    const datos: RegistroDTO = {

      usuario: this.usuario,

      correo: this.correo,

      nombrePersona: this.nombrePersona,

      fechaNacimiento: this.fechaNacimiento,

      contrasena: this.contrasena
    };

    this.cargando = true;

    this.authService.registro(
      datos
    ).subscribe({

      next: () => {

        this.cargando = false;

        this.mensaje =
          'Cuenta creada correctamente';

        this.modoRegistro = false;

        this.contrasena = '';
      },

      error: (err) => {

        console.log(err);

        this.cargando = false;

        if (
          typeof err.error == 'string'
        ) {

          this.error = err.error;

        } else {

          this.error =
            'No se pudo crear la cuenta';
        }
      }
    });
  }
}
