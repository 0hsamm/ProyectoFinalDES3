import { Conversacion } from './conversacion';

describe('Conversacion', () => {
  it('should allow a typed object', () => {
    const conversacion: Conversacion = {};

    expect(conversacion).toBeDefined();
  });
});
