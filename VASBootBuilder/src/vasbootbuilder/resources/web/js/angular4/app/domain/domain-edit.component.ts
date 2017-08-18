#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { Component, OnInit } from '@angular/core';
import { ${domainClassName}Service } from '../${domainObjectName}.service';
import { ${domainClassName} } from '../${domainObjectName}.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-${domainObjectName}-edit',
  templateUrl: './${domainObjectName}-edit.component.html',
  styleUrls: ['./${domainObjectName}-edit.component.css']
})
export class ${domainClassName}EditComponent implements OnInit {
  ${domainObjectName}: ${domainClassName};

  constructor(private ${domainObjectName}Service: ${domainClassName}Service, private route: ActivatedRoute) { }

  ngOnInit() {
    // param name should match what you defined in appRoutes
    console.log('loading ${domainObjectName}:' + this.route.snapshot.params['id']);
    this.${domainObjectName}Service.get${domainClassName}(this.route.snapshot.params['id']).subscribe(
      (response) => { this.${domainObjectName} = response.json(); },
      (error) => { console.log(error); }
    );
  }

  save${domainClassName}() {
    this.${domainObjectName}Service.saveNew${domainClassName}(this.${domainObjectName});
  }

}
