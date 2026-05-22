export interface CrearSolicitudAmistadDTO {

  usernameDestino: string;
}

export interface SolicitudAmistad {

  id: number;

  solicitanteId: number;

  solicitanteUsuario: string;

  solicitanteNombre: string;

  receptorId: number;

  receptorUsuario: string;

  receptorNombre: string;

  estado: string;

  fechaSolicitud?: string;

  fechaRespuesta?: string;
}

export interface Amistad {

  solicitudId: number;

  usuarioId: number;

  usuario: string;

  nombrePersona: string;

  sobreMi?: string;

  enLinea: boolean;

  fechaAmistad?: string;
}
