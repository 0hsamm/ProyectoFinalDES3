export interface ParticipanteConversacion {

  id?: number;

  usuario?: string;

  nombrePersona?: string;

  correo?: string;

  fotoPerfil?: string;

  sobreMi?: string;

  descripcion?: string;
}

export interface Conversacion {

  id?: number;

  nombre?: string;

  tipoConversacion?: string;

  fotoGrupo?: string;

  ultimoMensaje?: string;

  fechaUltimoMensaje?: string;

  fechaCreacion?: string;

  participantes?: ParticipanteConversacion[];

  participantesIds?: number[];

  fraseSecretaConfigurada?: boolean;

  oculta?: boolean;
}
