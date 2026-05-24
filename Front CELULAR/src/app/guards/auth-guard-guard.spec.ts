import { authGuard } from './auth-guard-guard';

describe('authGuard', () => {
  it('should export the guard function', () => {
    expect(authGuard).toBeDefined();
  });
});
