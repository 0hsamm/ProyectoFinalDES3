import { Mensaje } from './mensaje';

describe('Mensaje', () => {
  it('should allow a typed object', () => {
    const mensaje: Mensaje = {};

    expect(mensaje).toBeDefined();
  });
});
