import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Llamada } from './llamada';

describe('Llamada', () => {
  let component: Llamada;
  let fixture: ComponentFixture<Llamada>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Llamada],
    }).compileComponents();

    fixture = TestBed.createComponent(Llamada);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
