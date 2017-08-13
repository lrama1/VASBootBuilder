#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${domainClassName}ListComponent } from './${domainObjectName}-list.component';

describe('${domainClassName}ListComponent', () => {
  let component: ${domainClassName}ListComponent;
  let fixture: ComponentFixture<${domainClassName}ListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${domainClassName}ListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${domainClassName}ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
