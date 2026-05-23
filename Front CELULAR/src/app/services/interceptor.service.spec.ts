import { authInterceptor } from './interceptor.service';

describe('authInterceptor', () => {
  it('should export the interceptor function', () => {
    expect(authInterceptor).toBeDefined();
  });
});
