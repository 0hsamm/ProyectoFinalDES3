export interface Mensaje {

  id?: number;

  contenido?: string | null;

  fechaEnvio?: string;

  horaEnvio?: string;

  usuarioId?: number;

  conversacionId?: number;

  usuario?: string;

  usuarioNombre?: string;

  nombreUsuario?: string;

  remitenteId?: number;

  remitenteUsuario?: string;

  remitenteNombre?: string;

  tipoMensaje?: string;

  estatusMensaje?: string;

  tieneAdjunto?: boolean;

  adjuntoUrl?: string;

  adjuntoNombreOriginal?: string;

  adjuntoFormato?: string;

  adjuntoTamano?: number;

  contenidoProtegido?: boolean;

  fijado?: boolean;

  fechaFijado?: string;

  fraseSecreta?: string;
}
