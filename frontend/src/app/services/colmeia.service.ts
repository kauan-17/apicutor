import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ColmeiaService {
  private apiUrl = `${environment.apiUrl}/colmeias`;

  constructor(private http: HttpClient) { }

  getColmeias(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getColmeiasByApiario(apiarioId: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/apiarios/${apiarioId}/colmeias`);
  }

  getColmeia(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createColmeia(colmeia: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, colmeia);
  }

  updateColmeia(id: number, colmeia: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, colmeia);
  }

  deleteColmeia(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}