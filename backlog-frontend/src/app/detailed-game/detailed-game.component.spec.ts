import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailedGameComponent } from './detailed-game.component';

describe('DetailedGameComponent', () => {
  let component: DetailedGameComponent;
  let fixture: ComponentFixture<DetailedGameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailedGameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailedGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
