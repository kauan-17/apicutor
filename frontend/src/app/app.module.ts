import { NgModule } from '@angular/core';
import { Component } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { RoleGuard } from './auth/role.guard';
import { RoleVisibilityDirective } from './auth/role-visibility.directive';

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
import { InspecaoNovaComponent } from './components/inspecoes/inspecao-nova/inspecao-nova.component';
import { ApiarioNovoComponent } from './components/apiarios/apiario-novo/apiario-novo.component';
import { ApiarioEditarComponent } from './components/apiarios/apiario-editar/apiario-editar.component';
import { FuncionarioCadastroComponent } from './components/funcionarios/funcionario-cadastro/funcionario-cadastro.component';

@Component({
  selector: 'app-acesso-negado',
  standalone: true,
  template: `
    <div class="container py-5">
      <div class="alert alert-warning" role="alert">
        <h4 class="alert-heading">Acesso negado</h4>
        <p>Você não tem permissão para acessar esta funcionalidade.</p>
        <hr>
        <a routerLink="/dashboard" class="btn btn-primary">Voltar ao Dashboard</a>
      </div>
    </div>
  `
})
export class AcessoNegadoComponent {}

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
    RoleVisibilityDirective,
    InspecaoNovaComponent,
    ApiarioNovoComponent,
    ApiarioEditarComponent,
    ApiariosComponent,
    ColmeiasComponent,
    ProducaoComponent,
    RelatoriosComponent,
    FuncionarioCadastroComponent,
    RouterModule.forRoot([
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
      { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
      { path: 'acesso-negado', component: AcessoNegadoComponent },
      { path: 'apiarios', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'apiarios/novo', component: ApiarioNovoComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'apiarios/:id', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'apiarios/:id/editar', component: ApiarioEditarComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'apiarios/:id/inspecao/nova', component: InspecaoNovaComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_FUNCIONARIO', 'ROLE_ADMIN', 'APICULTOR', 'FUNCIONARIO', 'ADMIN'] } },
      { path: 'funcionarios/cadastrar', component: FuncionarioCadastroComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
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