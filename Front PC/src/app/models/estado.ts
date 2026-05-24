export interface Estado {

  id: number;

  texto?: string;

  mediaUrl?: string;

  thumbnailUrl?: string;

  mimeType?: string;

  tipo: 'IMAGEN' | 'VIDEO' | 'TEXTO';

  fechaCreacion?: string;

  fechaExpiracion?: string;

  usuarioId: number;

  usuarioNombre?: string;

  fotoPerfil?: string;

  cantidadVistas?: number;

  cantidadLikes?: number;

  visto?: boolean;

  meGusta?: boolean;

  propio?: boolean;
}

export interface EstadoInteraccion {

  usuarioId: number;

  usuario?: string;

  usuarioNombre?: string;

  fotoPerfil?: string;

  fecha?: string;
}
