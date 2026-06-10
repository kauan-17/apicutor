import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class InsumoService {
  private baseUrl = `${environment.apiUrl}/insumos`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> { return this.http.get<any[]>(this.baseUrl); }
  getById(id: number): Observable<any> { return this.http.get<any>(`${this.baseUrl}/${id}`); }
  getByApiario(apiarioId: number): Observable<any[]> { return this.http.get<any[]>(`${this.baseUrl}/apiario/${apiarioId}`); }
  create(payload: any): Observable<any> { return this.http.post<any>(this.baseUrl, payload); }
  update(id: number, payload: any): Observable<any> { return this.http.put<any>(`${this.baseUrl}/${id}`, payload); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}
