import { authGuard } from './guard.service';

describe('authGuard', () => {
  it('should export the guard function', () => {
    expect(authGuard).toBeDefined();
  });
});
