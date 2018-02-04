#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { Component, OnInit } from '@angular/core';
import { ${domainClassName}Service } from '../${domainObjectName.toLowerCase()}.service';
import { ${domainClassName} } from '../${domainObjectName.toLowerCase()}.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-${domainObjectName.toLowerCase()}-edit',
  templateUrl: './${domainObjectName.toLowerCase()}-edit.component.html',
  styleUrls: ['./${domainObjectName.toLowerCase()}-edit.component.css']
})
export class ${domainClassName}EditComponent implements OnInit {
  ${domainObjectName}: ${domainClassName} = new ${domainClassName}(
  #set($index = 0)
  #foreach($key in $attrs.keySet() )
  #if($index == 0)
  '' 
  #else##
  , '' #end##
  #set($index = $index + 1)
  #end
  );

  constructor(private ${domainObjectName}Service: ${domainClassName}Service, private route: ActivatedRoute) { }

  ngOnInit() {
    // param name should match what you defined in appRoutes
    console.log('loading ${domainObjectName}:' + this.route.snapshot.params['id']);
    this.${domainObjectName}Service.get${domainClassName}(this.route.snapshot.params['id']).subscribe(
      (response) => { this.${domainObjectName} = response; },
      (error) => { console.log(error); }
    );
  }

  save${domainClassName}() {
    this.${domainObjectName}Service.save${domainClassName}(this.${domainObjectName}).subscribe(
      (response) => { this.${domainObjectName} = response; },
      (error) => { console.log(error); }
    );
  }

}
