#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import {Injectable, OnInit} from '@angular/core';
import { $domainClassName } from './${domainObjectName}.model';
import {Http} from '@angular/http';

@Injectable()
export class ${domainClassName}Service implements OnInit{
  ${domainObjectName}s: ${domainClassName}[] = [];

  constructor(private http: Http) {}

  ngOnInit() {
  }

  saveNew${domainClassName}(${domainObjectName}ToSave: ${domainClassName}) {
    this.${domainObjectName}s.push(${domainObjectName}ToSave);
  }

  get${domainClassName} (id: string) {
    return this.http.get('/${projectName}/${domainObjectName}/' + id);
  }

  getAll${domainClassName}s () {
    //return this.${domainObjectName}s;
    return this.http.get('/${projectName}/${domainObjectName}s?page=1&per_page=3');
  }
}
