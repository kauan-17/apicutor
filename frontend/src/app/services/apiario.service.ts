import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Apiario {
  id: number;
  nome: string;
  localizacao?: string;
  latitude?: number;
  longitude?: number;
  descricao?: string;
  totalColmeias?: number;
  colmeias?: any[];
}

export interface Tarefa {
  id: number;
  titulo: string;
  prazo?: string;
  status: 'Pendente' | 'Em andamento' | 'Concluída';
}

@Injectable({ providedIn: 'root' })
export class ApiarioService {
  private baseUrl = `${environment.apiUrl}/apiarios`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Apiario[]> {
    return this.http.get<Apiario[]>(this.baseUrl);
  }

  // Compat: mantem assinatura antiga usada em componentes existentes
  getApiarios(): Observable<Apiario[]> {
    return this.getAll();
  }

  getById(id: number): Observable<Apiario> {
    return this.http.get<Apiario>(`${this.baseUrl}/${id}`);
  }

  // Compat: nomes anteriores possivelmente usados em componentes
  getApiario(id: number): Observable<Apiario> {
    return this.getById(id);
  }

  create(payload: Partial<Apiario>): Observable<Apiario> {
    return this.http.post<Apiario>(this.baseUrl, payload);
  }

  createApiario(payload: Partial<Apiario>): Observable<Apiario> {
    return this.create(payload);
  }

  update(id: number, payload: Partial<Apiario>): Observable<Apiario> {
    return this.http.put<Apiario>(`${this.baseUrl}/${id}`, payload);
  }

  updateApiario(id: number, payload: Partial<Apiario>): Observable<Apiario> {
    return this.update(id, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  deleteApiario(id: number): Observable<void> {
    return this.delete(id);
  }

  // Tarefas (backend)
  getTarefas(apiarioId: number): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(`${environment.apiUrl}/tarefas`, { params: { apiarioId } as any });
  }

  createTarefa(input: { apiarioId: number; titulo: string; prazo?: string; status?: 'Pendente' | 'Em andamento' | 'Concluída' }): Observable<Tarefa> {
    return this.http.post<Tarefa>(`${environment.apiUrl}/tarefas`, input);
  }
}
