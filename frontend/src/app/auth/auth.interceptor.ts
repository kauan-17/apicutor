import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      // Anexa token JWT em todas as requisições
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    } else {
      // Log leve para ajudar a diagnosticar 401 por falta de token
      console.warn('[AuthInterceptor] Sem token ao chamar:', req.method, req.url);
    }
    
    return next.handle(req).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          const currentUrl = this.router.url || '/';
          if (err.status === 401 && !req.url.includes('/auth/login')) {
            const currentUrl = this.router.url || '/';
            // Não redireciona se estiver no Home (página pública)
            if (currentUrl.startsWith('/home')) {
              console.warn('[AuthInterceptor] 401 no Home — exibindo Home sem dados.');
            } else {
              const returnUrl = currentUrl;
              this.authService.logout?.();
              this.router.navigate(['/login'], { queryParams: { returnUrl } });
            }
          } else if (err.status === 403) {
            // Bloqueio de autorização: direciona para página de acesso negado
            if (!currentUrl.startsWith('/home')) {
              this.router.navigate(['/acesso-negado']);
            }
          }
        }
        return throwError(() => err);
      })
    );
  }
}
