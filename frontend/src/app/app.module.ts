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
import { InspecaoDetalheComponent } from './components/inspecoes/inspecao-detalhe/inspecao-detalhe.component';
import { ApiarioNovoComponent } from './components/apiarios/apiario-novo/apiario-novo.component';
import { ApiarioEditarComponent } from './components/apiarios/apiario-editar/apiario-editar.component';
import { FuncionarioCadastroComponent } from './components/funcionarios/funcionario-cadastro/funcionario-cadastro.component';
import { ColmeiaNovaComponent } from './components/colmeias/colmeia-nova/colmeia-nova.component';
import { ColmeiaEditarComponent } from './components/colmeias/colmeia-editar/colmeia-editar.component';
import { FuncionariosListaComponent } from './components/funcionarios/funcionarios-lista/funcionarios-lista.component';

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
    InspecaoDetalheComponent,
    ApiarioNovoComponent,
    ApiarioEditarComponent,
    ApiariosComponent,
    ColmeiasComponent,
    ColmeiaNovaComponent,
    ColmeiaEditarComponent,
    ProducaoComponent,
    RelatoriosComponent,
    FuncionarioCadastroComponent,
    FuncionariosListaComponent,
    RouterModule.forRoot([
      { path: '', redirectTo: '/home', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
      { path: 'home', component: HomeComponent },
      { path: 'acesso-negado', component: AcessoNegadoComponent },
      { path: 'apiarios', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'apiarios/novo', component: ApiarioNovoComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'apiarios/:id', component: ApiariosComponent, canActivate: [AuthGuard] },
      { path: 'apiarios/:id/editar', component: ApiarioEditarComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'apiarios/:id/inspecao/nova', component: InspecaoNovaComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_FUNCIONARIO', 'ROLE_ADMIN', 'APICULTOR', 'FUNCIONARIO', 'ADMIN'] } },
      { path: 'inspecoes', loadComponent: () => import('./components/inspecoes').then(m => m.InspecoesComponent), canActivate: [AuthGuard] },
      { path: 'inspecoes/:id', component: InspecaoDetalheComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_FUNCIONARIO', 'ROLE_ADMIN', 'APICULTOR', 'FUNCIONARIO', 'ADMIN'] } },
      { path: 'funcionarios/cadastrar', component: FuncionarioCadastroComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'funcionarios', component: FuncionariosListaComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'colmeias', component: ColmeiasComponent, canActivate: [AuthGuard] },
      { path: 'colmeias/nova', component: ColmeiaNovaComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'colmeias/:id/editar', component: ColmeiaEditarComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ROLE_APICULTOR', 'ROLE_ADMIN', 'APICULTOR', 'ADMIN'] } },
      { path: 'colmeias/:id/inspecoes', loadComponent: () => import('./components/inspecoes').then(m => m.InspecoesComponent), canActivate: [AuthGuard] },
      { path: 'producao', component: ProducaoComponent, canActivate: [AuthGuard] },
      { path: 'relatorios', component: RelatoriosComponent, canActivate: [AuthGuard] },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: '**', redirectTo: '/dashboard' }
    ], { anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' })
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
