export interface Llamada {

  id?: number;

  canalAgora?: string;

  tipoLlamada: 'VOZ' | 'VIDEO';

  estadoLlamada?: string;

  fechaInicio?: string;

  fechaFin?: string;

  duracionSegundos?: number;

  conversacionId: number;

  usuarioLlamanteId: number;

  usuarioReceptorId: number;

  usuarioLlamanteNombre?: string;

  usuarioReceptorNombre?: string;
}

export interface LlamadaRespuesta {

  id: number;

  canalAgora: string;

  tokenAgora: string;

  uidAgora: number;

  tipoLlamada: 'VOZ' | 'VIDEO';

  estadoLlamada: string;

  appIdAgora: string;

  conversacionId: number;

  usuarioLlamanteId: number;

  usuarioReceptorId: number;
}
