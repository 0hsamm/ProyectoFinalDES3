export interface SolicitudIa {

  mensaje?: string;

  conversacionId?: number;

  contexto?: string;

  idiomaDestino?: string;
}

export interface RespuestaIa {

  respuesta: string;

  tipoAccion: string;

  conversacionId?: number;

  fechaCreacion?: string;
}

export interface HistorialIa {

  id: number;

  tipoAccion: string;

  conversacionId?: number;

  vistaPreviaSolicitud?: string;

  vistaPreviaRespuesta?: string;

  exitoso: boolean;

  fechaCreacion?: string;
}
