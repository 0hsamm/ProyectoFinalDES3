import { environment } from './environment';

describe('environment', () => {
  it('should expose the api url', () => {
    expect(environment.apiUrl).toBeDefined();
  });
});
