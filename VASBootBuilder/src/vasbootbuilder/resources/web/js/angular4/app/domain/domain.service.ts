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

  save${domainClassName}(${domainObjectName}ToSave: ${domainClassName}) {
    this.${domainObjectName}s.push(${domainObjectName}ToSave);
    return this.http.put('/${projectName}/${domainObjectName}/' + ${domainObjectName}ToSave.${domainClassIdAttributeName}, ${domainObjectName}ToSave);
  }

  get${domainClassName} (id: string) {
    return this.http.get('/${projectName}/${domainObjectName}/' + id);
  }

  get${domainClassName}s (page: number, pageSize: number) {
    //return this.${domainObjectName}s;
    return this.http.get('/${projectName}/${domainObjectName}s?page=' + page + '&per_page=' + pageSize);
  }
}
