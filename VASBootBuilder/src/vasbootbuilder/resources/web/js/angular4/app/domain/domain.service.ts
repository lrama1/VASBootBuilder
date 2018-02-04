#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import {Injectable, OnInit} from '@angular/core';
import { $domainClassName } from './${domainObjectName.toLowerCase()}.model';
import {HttpClient} from '@angular/common/http';
import {ListWrapper} from '../app.listwrapper';


@Injectable()
export class ${domainClassName}Service implements OnInit{
  ${domainObjectName}s: ${domainClassName}[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
  }

  save${domainClassName}(${domainObjectName}ToSave: ${domainClassName}) {
    this.${domainObjectName}s.push(${domainObjectName}ToSave);
    return this.http.put<${domainClassName}>('/${projectName}/${domainObjectName.toLowerCase()}/' + ${domainObjectName}ToSave.${domainClassIdAttributeName}, ${domainObjectName}ToSave);
  }

  get${domainClassName} (id: string) {
    return this.http.get<${domainClassName}>('/${projectName}/${domainObjectName.toLowerCase()}/' + id);
  }

  get${domainClassName}s (page: number, pageSize: number) {
    //return this.${domainObjectName}s;
    return this.http.get<ListWrapper<${domainClassName}>>('/${projectName}/${domainObjectName.toLowerCase()}s?page=' + page + '&per_page=' + pageSize);
  }
}
