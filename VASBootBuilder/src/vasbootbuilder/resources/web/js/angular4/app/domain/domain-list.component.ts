#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { Component, OnInit } from '@angular/core';
import {${domainClassName}Service} from "../${domainObjectName}.service";
import {${domainClassName}} from "../${domainObjectName}.model";

@Component({
  selector: 'app-${domainObjectName}-list',
  templateUrl: './${domainObjectName}-list.component.html',
  styleUrls: ['./${domainObjectName}-list.component.css']
})
export class ${domainClassName}ListComponent implements OnInit {

  listOf${domainClassName}s: ${domainClassName}[];
  constructor(private ${domainObjectName}Service: ${domainClassName}Service) { }

  ngOnInit() {
    console.log('invoking ${domainClassName} Service');
    this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
  }

}
