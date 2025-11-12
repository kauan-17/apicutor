import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

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
    
    return next.handle(req);
  }
}