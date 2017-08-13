#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${domainClassName}EditComponent } from './${domainObjectName}-edit.component';

describe('${domainClassName}EditComponent', () => {
  let component: ${domainClassName}EditComponent;
  let fixture: ComponentFixture<${domainClassName}EditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${domainClassName}EditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${domainClassName}EditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
