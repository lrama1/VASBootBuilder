#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import {Injectable, OnInit} from '@angular/core';
import { $domainClassName } from './${domainObjectName}.model';


@Injectable()
export class ${domainClassName}Service implements OnInit{
  ${domainObjectName}s: ${domainClassName}[] = [];

  ngOnInit() {
  }

  saveNew${domainClassName}(${domainObjectName}ToSave: ${domainClassName}) {
    this.${domainObjectName}s.push(${domainObjectName}ToSave);
  }

  get${domainClassName} (id: string) {
    for (const ${domainObjectName} of this.${domainObjectName}s){
      if (${domainObjectName}.$domainClassIdAttributeName === id){
        return ${domainObjectName};
      }
    }
  }

  getAll${domainClassName}s () {
    return this.${domainObjectName}s;
  }
}
