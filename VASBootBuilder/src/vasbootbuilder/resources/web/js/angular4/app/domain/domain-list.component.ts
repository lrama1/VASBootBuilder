#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { Component, OnInit, Input } from '@angular/core';
import {${domainClassName}Service} from '../${domainObjectName.toLowerCase()}.service';
import {${domainClassName}} from '../${domainObjectName.toLowerCase()}.model';
import {LazyLoadEvent} from 'primeng/primeng';
import { Router} from '@angular/router';

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
  totalRecords = 0;
  // attribute that can be set by the parent component
  @Input() titleToDisplay: string;
  
  constructor(private ${domainObjectName}Service: ${domainClassName}Service, private router: Router) { }

  ngOnInit() {
    console.log('invoking ${domainClassName} Service');
    //this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
    this.${domainObjectName}Service.get${domainClassName}s(this.currentPage, this.pageSize).subscribe(
      (response) => {
        this.listOf${domainClassName}s = response.rows;
        this.lastPage = response.lastPage;
        this.totalRecords = response.totalRecords;
        },
      (error) => { console.log(error); }
    );
  }
  
  editRow(${domainObjectName}: ${domainClassName}){
    this.router.navigateByUrl('/${domainObjectName}/' + $domainObjectName.${domainClassIdAttributeName});
  }

  loadPage(event: LazyLoadEvent) {
    const page = (event.first / event.rows) + 1;
    console.log('invoking ${domainClassName} Service');
    //this.listOf${domainClassName}s = this.${domainObjectName}Service.getAll${domainClassName}s();
    this.${domainObjectName}Service.get${domainClassName}s(page, event.rows).subscribe(
      (response) => {
        this.listOf${domainClassName}s = response.rows;
        this.lastPage = response.lastPage;
        this.totalRecords = response.totalRecords;
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
        this.listOf${domainClassName}s = response.rows;
        this.lastPage = response.lastPage;
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
        this.listOf${domainClassName}s = response.rows;
        this.lastPage = response.lastPage;
        },
      (error) => { console.log(error); }
    );
  }
  
}
