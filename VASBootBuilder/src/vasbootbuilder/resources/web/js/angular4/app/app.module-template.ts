#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule} from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { APP_BASE_HREF} from '@angular/common';
import { HttpModule } from '@angular/http';

import { ButtonModule, DataTableModule, PanelModule, SharedModule } from 'primeng/primeng';
import { BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { ${domainClassName}ListComponent } from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}-list/${domainObjectName.toLowerCase()}-list.component';
import { ${domainClassName}EditComponent } from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}-edit/${domainObjectName.toLowerCase()}-edit.component';
import {${domainClassName}Service} from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}.service';
import { HomeComponent } from './home/home.component';

const appRoutes: Routes = [
  { path: '', component: HomeComponent},
  { path: '${domainObjectName}s', component: ${domainClassName}ListComponent},
  { path: '${domainObjectName}/:id', component: ${domainClassName}EditComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    ${domainClassName}ListComponent,
    ${domainClassName}EditComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    HttpModule,
    DataTableModule,
    SharedModule,
    BrowserAnimationsModule,
    PanelModule,
    ButtonModule
  ],
  providers: [{provide: APP_BASE_HREF, useValue: '/${projectName}'}, ${domainClassName}Service],
  bootstrap: [AppComponent]
})
export class AppModule { }
