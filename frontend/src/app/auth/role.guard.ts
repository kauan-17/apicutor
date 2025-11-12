import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // Primeiro, precisa estar autenticado
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return false;
    }

    // Se a rota não exigir papéis específicos, permitir
    const roles = (route.data && route.data['roles']) as string[] || [];
    if (!roles.length) {
      return true;
    }

    // Verifica se o usuário possui algum dos papéis exigidos
    const authorized = roles.some(role => this.authService.hasRole(role));
    if (!authorized) {
      this.router.navigate(['/acesso-negado']);
    }
    return authorized;
  }
}