import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        tap((response: any) => {
          if (response.token) {
            localStorage.setItem(this.tokenKey, response.token);
            // Armazenar roles vindas da resposta e/ou do token (união, normalizadas)
            const rolesFromResponse: string[] = response.roles
              ? (Array.isArray(response.roles)
                  ? (response.roles as string[])
                  : typeof response.roles === 'string'
                    ? (response.roles as string)
                        .split(',')
                        .map((r: string) => r.trim())
                        .filter((r: string) => !!r)
                    : [])
              : [];

            const payload = this.decodeJwtPayload(response.token);
            const rolesFromToken: string[] = payload?.roles
              ? (Array.isArray(payload.roles)
                  ? (payload.roles as string[])
                  : typeof payload.roles === 'string'
                    ? (payload.roles as string)
                        .split(',')
                        .map((r: string) => r.trim())
                        .filter((r: string) => !!r)
                    : [])
              : [];

            const union = Array.from(new Set([...rolesFromResponse, ...rolesFromToken]))
              .map((r: string) => this.normalizeRole(r));
            if (union.length) {
              localStorage.setItem('auth_roles', JSON.stringify(union));
            }
          }
        })
      );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, userData);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('auth_roles');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private normalizeRole(role: string): string {
    const r = (role || '').trim().toUpperCase();
    return r.startsWith('ROLE_') ? r : `ROLE_${r}`;
  }

  private decodeJwtPayload(token: string): any | null {
    try {
      const base64Url = token.split('.')[1];
      if (!base64Url) return null;
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
      const json = atob(padded);
      return JSON.parse(json);
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  getCurrentUser(): any {
    const token = this.getToken();
    if (token) {
      const payload = this.decodeJwtPayload(token);
      if (!payload) return null;
      const rolesFromToken = payload.roles
        ? (Array.isArray(payload.roles)
            ? (payload.roles as string[])
            : typeof payload.roles === 'string'
              ? payload.roles.split(',').map((r: string) => r.trim()).filter((r: string) => !!r)
              : [])
        : [];
      const rolesFromStorage = this.getStoredRoles();
      const normalized = Array.from(new Set([...rolesFromStorage, ...rolesFromToken]))
        .map((r: string) => this.normalizeRole(r));
      const apiarioId = this.extractApiarioIdFromPayload(payload);
      return { username: payload.sub, roles: normalized, apiarioId };
    }
    return null;
  }

  getRoles(): string[] {
    const fromStorage = this.getStoredRoles();
    const token = this.getToken();
    const fromToken = token ? this.extractRolesFromToken(token) : [];
    return Array.from(new Set([...fromStorage, ...fromToken]))
      .map((r: string) => this.normalizeRole(r));
  }

  hasRole(role: string): boolean {
    const target = this.normalizeRole(role);
    const roles = this.getRoles().map((r: string) => this.normalizeRole(r));
    return roles.includes(target);
  }

  private getStoredRoles(): string[] {
    const rolesStr = localStorage.getItem('auth_roles');
    if (!rolesStr) return [];
    try {
      const parsed = JSON.parse(rolesStr);
      return Array.isArray(parsed) ? parsed.map((r: string) => this.normalizeRole(r)) : [];
    } catch {
      return [];
    }
  }

  private extractRolesFromToken(token: string): string[] {
    const payload = this.decodeJwtPayload(token);
    if (!payload?.roles) return [];
    if (Array.isArray(payload.roles)) return (payload.roles as string[]);
    if (typeof payload.roles === 'string') {
      return payload.roles
        .split(',')
        .map((r: string) => r.trim())
        .filter((r: string) => !!r);
    }
    return [];
  }

  private extractApiarioIdFromPayload(payload: any): number | null {
    if (!payload) return null;
    const candidates = [
      payload.apiarioId,
      payload.apiario_id,
      payload.apiaryId,
      payload.apiary_id,
      payload.apiario?.id,
      payload.apiary?.id
    ];
    for (const c of candidates) {
      const n = typeof c === 'string' ? parseInt(c, 10) : c;
      if (typeof n === 'number' && !isNaN(n)) return n;
    }
    return null;
  }
}