import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FuncionarioCreateDTO {
  nome: string;
  username: string;
  email: string;
  password: string;
  apiarioId: number;
}

@Injectable({ providedIn: 'root' })
export class FuncionarioService {
  private baseUrl = `${environment.apiUrl}/funcionarios`;

  constructor(private http: HttpClient) {}

  private authOptions(): { headers?: HttpHeaders } {
    const token = localStorage.getItem('auth_token');
    if (token) {
      return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
    }
    return {};
  }

  create(dto: FuncionarioCreateDTO): Observable<any> {
    return this.http.post(this.baseUrl, dto, this.authOptions());
  }

  listByApiario(apiarioId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}?apiarioId=${apiarioId}`, this.authOptions());
  }

  getMe(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/me`, this.authOptions());
  }
}