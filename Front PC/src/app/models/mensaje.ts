export interface Mensaje {

  id?: number;

  contenido: string;

  fechaEnvio?: string;

  horaEnvio?: string;

  usuarioId?: number;

  conversacionId?: number;

  usuario?: string;

  usuarioNombre?: string;

  nombreUsuario?: string;

  remitenteId?: number;

  tipoMensaje?: string;

  estatusMensaje?: string;

  tieneAdjunto?: boolean;

  contenidoProtegido?: boolean;

  fraseSecreta?: string;
}
