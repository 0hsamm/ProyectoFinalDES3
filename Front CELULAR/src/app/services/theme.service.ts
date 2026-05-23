import { Injectable } from '@angular/core';

export type PreferenciaTema =
  'oscuro' | 'claro' | 'sistema';

interface PreferenciasGuardadas {
  tema?: PreferenciaTema;
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  private readonly storageKey =
    'preferenciasUsuario';

  private readonly mediaQuery =
    typeof window != 'undefined'
      ? window.matchMedia(
          '(prefers-color-scheme: light)'
        )
      : null;

  private inicializado: boolean = false;

  inicializar(): void {

    if (
      this.inicializado ||
      typeof window == 'undefined' ||
      typeof document == 'undefined'
    ) {
      return;
    }

    this.inicializado = true;

    this.aplicarTemaGuardado();

    window.addEventListener(
      'storage',
      this.sincronizarTema
    );

    window.addEventListener(
      'preferencias-actualizadas',
      this.onPreferenciasActualizadas as EventListener
    );

    if (this.mediaQuery == null) {
      return;
    }

    const mediaQuery =
      this.mediaQuery;

    if (
      typeof mediaQuery.addEventListener ==
      'function'
    ) {
      mediaQuery.addEventListener(
        'change',
        this.onCambioTemaSistema
      );

      return;
    }

    if (
      typeof mediaQuery.addListener ==
      'function'
    ) {
      mediaQuery.addListener(
        this.onCambioTemaSistema
      );
    }
  }

  aplicarTemaGuardado(): void {

    this.aplicarTema(
      this.obtenerTemaGuardado()
    );
  }

  aplicarTema(
    tema: PreferenciaTema
  ): void {

    if (typeof document == 'undefined') {
      return;
    }

    const temaResuelto =
      this.resolverTema(tema);

    document.documentElement.setAttribute(
      'data-theme',
      temaResuelto == 'claro'
        ? 'light'
        : 'dark'
    );

    document.body.classList.toggle(
      'theme-light',
      temaResuelto == 'claro'
    );

    document.body.classList.toggle(
      'theme-dark',
      temaResuelto != 'claro'
    );
  }

  guardarTema(
    tema: PreferenciaTema
  ): void {

    const preferencias:
      PreferenciasGuardadas =
      this.leerPreferencias();

    preferencias['tema'] = tema;

    localStorage.setItem(
      this.storageKey,
      JSON.stringify(preferencias)
    );

    this.aplicarTema(tema);
  }

  obtenerTemaGuardado(): PreferenciaTema {

    const tema =
      this.leerPreferencias()
        ?.['tema'];

    if (
      tema == 'claro' ||
      tema == 'oscuro' ||
      tema == 'sistema'
    ) {
      return tema;
    }

    return 'oscuro';
  }

  esTemaClaroActivo(): boolean {

    return this.resolverTema(
      this.obtenerTemaGuardado()
    ) == 'claro';
  }

  private resolverTema(
    tema: PreferenciaTema
  ): 'oscuro' | 'claro' {

    if (tema == 'sistema') {
      return this.mediaQuery?.matches
        ? 'claro'
        : 'oscuro';
    }

    return tema;
  }

  private leerPreferencias(): PreferenciasGuardadas {

    const guardadas =
      localStorage.getItem(
        this.storageKey
      );

    if (!guardadas) {
      return {};
    }

    try {
      return JSON.parse(
        guardadas
      ) || {};
    } catch {
      return {};
    }
  }

  private readonly sincronizarTema =
    (): void => {
      this.aplicarTemaGuardado();
    };

  private readonly onPreferenciasActualizadas =
    (event: Event): void => {

      const detalle =
        (event as CustomEvent)
          .detail || {};

      const tema =
        detalle['tema'];

      if (
        tema == 'claro' ||
        tema == 'oscuro' ||
        tema == 'sistema'
      ) {
        this.aplicarTema(tema);

        return;
      }

      this.aplicarTemaGuardado();
    };

  private readonly onCambioTemaSistema =
    (): void => {

      if (
        this.obtenerTemaGuardado() ==
        'sistema'
      ) {
        this.aplicarTema('sistema');
      }
    };
}
