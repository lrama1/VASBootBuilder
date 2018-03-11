#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule} from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { APP_BASE_HREF} from '@angular/common';

import { ButtonModule, DataTableModule, PanelModule, SharedModule } from 'primeng/primeng';
import { BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { ${domainClassName}ListComponent } from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}-list/${domainObjectName.toLowerCase()}-list.component';
import { ${domainClassName}EditComponent } from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}-edit/${domainObjectName.toLowerCase()}-edit.component';
import {${domainClassName}Service} from './${domainObjectName.toLowerCase()}/${domainObjectName.toLowerCase()}.service';
import { HomeComponent } from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {MyLoginInterceptor} from './app.login.interceptor';

const appRoutes: Routes = [  
  { path: '${domainObjectName}s', component: ${domainClassName}ListComponent},
  { path: '${domainObjectName}/:id', component: ${domainClassName}EditComponent},
  { path: '', component: HomeComponent}
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
    HttpClientModule,
    RouterModule.forRoot(appRoutes,  { useHash: true }),    
    DataTableModule,
    SharedModule,
    BrowserAnimationsModule,
    PanelModule,
    ButtonModule
  ],
  providers: [/*{provide: APP_BASE_HREF, useValue: '/${projectName}'},*/{ provide: HTTP_INTERCEPTORS, useClass: MyLoginInterceptor, multi: true }, ${domainClassName}Service],
  bootstrap: [AppComponent]
})
export class AppModule { }
