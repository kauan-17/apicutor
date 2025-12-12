import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProducaoService {
  private baseUrl = `${environment.apiUrl}/producao`;
  private relatoriosUrl = `${environment.apiUrl}/relatorios`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> { return this.http.get<any[]>(this.baseUrl); }
  getById(id: number): Observable<any> { return this.http.get<any>(`${this.baseUrl}/${id}`); }
  getByColmeia(colmeiaId: number): Observable<any[]> { return this.http.get<any[]>(`${this.baseUrl}/colmeia/${colmeiaId}`); }
  create(payload: any): Observable<any> { return this.http.post<any>(this.baseUrl, payload); }
  update(id: number, payload: any): Observable<any> { return this.http.put<any>(`${this.baseUrl}/${id}`, payload); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }

  // Relatórios
  producaoMensal(apiarioId: number, ano: number, mes?: number): Observable<Record<string, number>> {
    const url = `${this.relatoriosUrl}/producao/mensal/${apiarioId}/${ano}` + (mes ? `?mes=${mes}` : '');
    return this.http.get<Record<string, number>>(url);
  }

  producaoMensalTotal(ano: number, mes?: number): Observable<Record<string, number>> {
    const url = `${this.relatoriosUrl}/producao/mensal/total/${ano}` + (mes ? `?mes=${mes}` : '');
    return this.http.get<Record<string, number>>(url);
  }
}
