export interface Conversacion {

  id?: number;

  nombre?: string;

  tipoConversacion?: string;

  fotoGrupo?: string;

  ultimoMensaje?: string;

  fechaUltimoMensaje?: string;

  participantes?: any[];

  participantesIds?: number[];

  fraseSecretaConfigurada?: boolean;
}
