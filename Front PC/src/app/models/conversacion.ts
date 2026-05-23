export interface Conversacion {

  id?: number;

  nombre?: string;

  tipoConversacion?: string;

  fotoGrupo?: string;

  ultimoMensaje?: string;

  fechaUltimoMensaje?: string;

  fechaCreacion?: string;

  participantes?: any[];

  participantesIds?: number[];

  fraseSecretaConfigurada?: boolean;

  oculta?: boolean;
}
