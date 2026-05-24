import { Usuario } from './usuario';

describe('Usuario', () => {
  it('should allow a typed object', () => {
    const usuario: Usuario = {
      usuario: 'demo',
      correo: 'demo@example.com',
      nombrePersona: 'Demo User'
    };

    expect(usuario).toBeDefined();
  });
});
