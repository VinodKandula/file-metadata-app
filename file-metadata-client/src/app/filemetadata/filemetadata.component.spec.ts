import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilemetadataComponent } from './filemetadata.component';

describe('FilemetadataComponent', () => {
  let component: FilemetadataComponent;
  let fixture: ComponentFixture<FilemetadataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilemetadataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilemetadataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
