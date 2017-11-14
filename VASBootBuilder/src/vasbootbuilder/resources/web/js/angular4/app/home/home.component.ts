#set($domainObjectNameL = ${domainClassName.toLowerCase()})
import { Component, OnInit } from '@angular/core';
import {${domainClassName}ListComponent } from "../${domainObjectNameL}/${domainObjectNameL}-list/${domainObjectNameL}-list.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
