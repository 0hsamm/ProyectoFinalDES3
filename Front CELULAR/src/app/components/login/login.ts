import {
  Component,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

import { ToastService } from '../../services/toast.service';
import { ThemeService } from '../../services/theme.service';

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

  styleUrls: ['./login.css']
})
export class Login
  implements OnInit {

  modoRegistro = false;

  modoClaro = false;

  usuario = '';

  correo = '';

  nombrePersona = '';

  fechaNacimiento = '';

  contrasena = '';

  error = '';

  mensaje = '';

  cargando = false;

  constructor(

    private authService: AuthService,

    private router: Router,

    private toastService: ToastService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {

    this.themeService.inicializar();

    this.modoClaro =
      this.themeService
        .esTemaClaroActivo();
  }

  toggleTheme(): void {

    this.modoClaro = !this.modoClaro;

    this.themeService.guardarTema(
      this.modoClaro
        ? 'claro'
        : 'oscuro'
    );
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
      this.usuario.trim() === '' ||
      this.contrasena.trim() === ''
    ) {

      this.error =
        'Debes llenar todos los campos';

      this.toastService.warning(
        'Campos incompletos',
        this.error
      );

      return;
    }

    this.cargando = true;

    this.authService.login(
      this.usuario,
      this.contrasena
    ).subscribe({

      next: (respuesta) => {

        if (!respuesta.token) {

          this.error =
            'No se pudo iniciar sesion';

          this.cargando = false;

          return;
        }

        this.authService.guardarSesion(
          respuesta
        );

        this.cargando = false;

        this.toastService.success(
          'Sesion iniciada',
          'Bienvenido a Reload'
        );

        this.router.navigate([
          '/app'
        ]);
      },

      error: (err) => {

        this.cargando = false;

        if (
          typeof err.error == 'string'
        ) {

          this.error = err.error;

        } else {

          this.error =
            'Usuario o contrasena incorrectos';
        }

        this.toastService.error(
          'No se pudo iniciar sesion',
          this.error
        );
      }
    });
  }

  registrar(): void {

    this.error = '';

    this.mensaje = '';

    if (
      this.usuario.trim() === '' ||
      this.correo.trim() === '' ||
      this.nombrePersona.trim() === '' ||
      this.fechaNacimiento.trim() === '' ||
      this.contrasena.trim() === ''
    ) {

      this.error =
        'Debes llenar todos los campos';

      this.toastService.warning(
        'Campos incompletos',
        this.error
      );

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

      next: (respuesta) => {

        this.cargando = false;

        this.mensaje =
          respuesta;

        this.toastService.success(
          'Cuenta creada',
          respuesta
        );

        this.usuario = '';

        this.correo = '';

        this.nombrePersona = '';

        this.fechaNacimiento = '';

        this.contrasena = '';

        this.modoRegistro = false;
      },

      error: (err) => {

        this.cargando = false;

        if (
          typeof err.error == 'string'
        ) {

          this.error = err.error;

        } else {

          this.error =
            'No se pudo crear la cuenta';
        }

        this.toastService.error(
          'Registro fallido',
          this.error
        );
      }
    });
  }
}
