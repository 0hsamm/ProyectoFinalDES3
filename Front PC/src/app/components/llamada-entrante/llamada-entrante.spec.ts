import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LlamadaEntrante } from './llamada-entrante';

describe('LlamadaEntrante', () => {
  let component: LlamadaEntrante;
  let fixture: ComponentFixture<LlamadaEntrante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LlamadaEntrante],
    }).compileComponents();

    fixture = TestBed.createComponent(LlamadaEntrante);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
