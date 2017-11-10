#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { Component, OnInit } from '@angular/core';
import {${domainClassName}Service} from "../${domainObjectName.toLowerCase()}.service";
import {${domainClassName}} from "../${domainObjectName.toLowerCase()}.model";

@Component({
  selector: 'app-${domainObjectName.toLowerCase()}-list',
  templateUrl: './${domainObjectName.toLowerCase()}-list.component.html',
  styleUrls: ['./${domainObjectName.toLowerCase()}-list.component.css']
})
export class ${domainClassName}ListComponent implements OnInit {

  listOf${domainClassName}s: ${domainClassName}[];
  currentPage = 1;
  pageSize = 5;
  lastPage = 1;
  
  constructor(private ${domainObjectName}Service: ${domainClassName}Service) { }

  ngOnInit() {
    console.log('invoking ${domainClassName} Service');
    //this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
    this.${domainObjectName}Service.get${domainClassName}s(this.currentPage, this.pageSize).subscribe(
      (response) => {
        this.listOf${domainClassName}s = response.json().rows;
        this.lastPage = response.json().lastPage;
        console.log('start of results..');
        console.log(response.json().rows);
        console.log('end of results.');
        },
      (error) => { console.log(error); }
    );
  }

  nextPage() {
    this.currentPage++;
    console.log('invoking ${domainClassName} Service');
    //this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
    this.${domainObjectName}Service.get${domainClassName}s(this.currentPage, this.pageSize).subscribe(
      (response) => {
        this.listOf${domainClassName}s = response.json().rows;
        this.lastPage = response.json().lastPage;
        console.log('start of results..');
        console.log(response.json().rows);
        console.log('end of results.');
        },
      (error) => { console.log(error); }
    );
  }
  
  previousPage() {
    this.currentPage--;
    console.log('invoking ${domainClassName} Service');
    //this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
    this.${domainObjectName}Service.get${domainClassName}s(this.currentPage, this.pageSize).subscribe(
      (response) => {
        this.listOf${domainClassName}s = response.json().rows;
        this.lastPage = response.json().lastPage;
        console.log('start of results..');
        console.log(response.json().rows);
        console.log('end of results.');
        },
      (error) => { console.log(error); }
    );
  }
  
}
