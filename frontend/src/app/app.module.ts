import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './home/home.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthInterceptor } from './auth/auth.interceptor';
import { AuthGuard } from './auth/auth.guard';
import { ApiariosComponent } from './components/apiarios/apiarios.component';
import { ColmeiasComponent } from './components/colmeias/colmeias.component';
import { ProducaoComponent } from './components/producao/producao.component';
import { RelatoriosComponent } from './components/relatorios/relatorios.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ApiariosComponent,
    ColmeiasComponent,
    ProducaoComponent,
    RelatoriosComponent,
    RouterModule.forRoot([
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
      { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
      { path: 'apiarios', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'apiarios/:id', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'colmeias', component: ColmeiasComponent, canActivate: [AuthGuard] },
      { path: 'producao', component: ProducaoComponent, canActivate: [AuthGuard] },
      { path: 'relatorios', component: RelatoriosComponent, canActivate: [AuthGuard] },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: '**', redirectTo: '/dashboard' }
    ])
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }