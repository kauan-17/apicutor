import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiarioService {
  private apiUrl = `${environment.apiUrl}/apiarios`;

  constructor(private http: HttpClient) { }

  getApiarios(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getApiario(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createApiario(apiario: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, apiario);
  }

  updateApiario(id: number, apiario: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, apiario);
  }

  deleteApiario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}