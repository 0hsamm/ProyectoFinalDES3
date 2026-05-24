export interface Usuario {

  id?: number;

  usuario: string;

  correo: string;

  nombrePersona: string;

  sobreMi?: string;

  fotoPerfil?: string;

  enLinea?: boolean;

  mostrarEnLinea?: boolean;

  ultimaVezEnLinea?: string;

  rol?: string;

  contrasena?: string;

  fechaNacimiento?: string;
}

export interface LoginDTO {

  usuario: string;

  contrasena: string;
}

export interface RegistroDTO {

  usuario: string;

  correo: string;

  nombrePersona: string;

  contrasena: string;

  fechaNacimiento: string;
}

export interface RespuestaAutenticacionDTO {

  token: string;

  tipoToken: string;

  id: number;

  usuario: string;

  correo: string;

  nombrePersona: string;

  fotoPerfil?: string;
}
