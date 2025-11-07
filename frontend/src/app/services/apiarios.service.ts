import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

export interface Localizacao {
  latitude: number;
  longitude: number;
  endereco?: string;
  cidade?: string;
  estado?: string;
}

export interface Apiario {
  id: number;
  nome: string;
  proprietario?: string;
  localizacao: Localizacao;
  totalColmeias: number;
  colmeiasAtivas: number;
  colmeiasInativas: number;
  ultimaInspecao?: string; // ISO date
}

export interface Colmeia {
  id: number;
  codigo: string;
  rainhaIdentificada?: boolean;
  forca?: 'Fraca' | 'Média' | 'Forte';
  status?: 'Ativa' | 'Inativa' | 'Manutenção';
  producaoUltimoMesKg?: number;
}

export interface Inspecao {
  id: number;
  data: string; // ISO date
  responsavel: string;
  observacoes?: string;
  colmeiasVerificadas: number;
}

export interface ProducaoResumo {
  apiarioId: number;
  anoAtualKg: number;
  mesAtualKg: number;
  mediaKgPorColmeia: number;
  melhorMes?: string;
}

export interface Tarefa {
  id: number;
  titulo: string;
  prazo?: string; // ISO date
  status: 'Pendente' | 'Em andamento' | 'Concluída';
}

export interface Alerta {
  id: number;
  tipo: 'Clima' | 'Sanitário' | 'Manutenção' | 'Produção';
  mensagem: string;
  nivel: 'Info' | 'Atenção' | 'Crítico';
}

export interface Clima {
  temperaturaC: number;
  umidade: number; // %
  condicao: string; // Ex: Ensolarado, Nublado, Chuva
}

@Injectable({ providedIn: 'root' })
export class ApiariosService {
  // Mock de dados para demonstração
  private apiarios: Apiario[] = [
    {
      id: 1,
      nome: 'Apiário Central',
      proprietario: 'Fazenda Doce Mel',
      localizacao: {
        latitude: -23.55052,
        longitude: -46.633308,
        endereco: 'Estrada das Flores, 123',
        cidade: 'São Paulo',
        estado: 'SP'
      },
      totalColmeias: 24,
      colmeiasAtivas: 22,
      colmeiasInativas: 2,
      ultimaInspecao: new Date().toISOString()
    },
    {
      id: 2,
      nome: 'Apiário do Vale',
      proprietario: 'Cooperativa Mel Bom',
      localizacao: {
        latitude: -22.9068,
        longitude: -43.1729,
        cidade: 'Rio de Janeiro',
        estado: 'RJ'
      },
      totalColmeias: 15,
      colmeiasAtivas: 14,
      colmeiasInativas: 1,
      ultimaInspecao: new Date(Date.now() - 86400000).toISOString()
    }
  ];

  private colmeiasPorApiario: Record<number, Colmeia[]> = {
    1: [
      { id: 101, codigo: 'A-01', rainhaIdentificada: true, forca: 'Forte', status: 'Ativa', producaoUltimoMesKg: 8.4 },
      { id: 102, codigo: 'A-02', rainhaIdentificada: true, forca: 'Média', status: 'Ativa', producaoUltimoMesKg: 6.2 },
      { id: 103, codigo: 'A-03', rainhaIdentificada: false, forca: 'Fraca', status: 'Inativa', producaoUltimoMesKg: 0 }
    ],
    2: [
      { id: 201, codigo: 'V-01', rainhaIdentificada: true, forca: 'Média', status: 'Ativa', producaoUltimoMesKg: 5.1 },
      { id: 202, codigo: 'V-02', rainhaIdentificada: true, forca: 'Forte', status: 'Ativa', producaoUltimoMesKg: 7.9 }
    ]
  };

  private inspecoesPorApiario: Record<number, Inspecao[]> = {
    1: [
      { id: 1, data: new Date().toISOString(), responsavel: 'João', observacoes: 'Sem sinais de infestação.', colmeiasVerificadas: 12 },
      { id: 2, data: new Date(Date.now() - 7 * 86400000).toISOString(), responsavel: 'Ana', observacoes: 'Aplicado alimentador em 5 colmeias.', colmeiasVerificadas: 10 }
    ],
    2: [
      { id: 3, data: new Date(Date.now() - 2 * 86400000).toISOString(), responsavel: 'Carlos', observacoes: 'Reforço em caixas V-01 e V-02.', colmeiasVerificadas: 8 }
    ]
  };

  private tarefasPorApiario: Record<number, Tarefa[]> = {
    1: [
      { id: 1, titulo: 'Instalar nova colmeia A-04', prazo: new Date(Date.now() + 3 * 86400000).toISOString(), status: 'Pendente' },
      { id: 2, titulo: 'Trocar quadros danificados A-03', status: 'Em andamento' }
    ],
    2: [
      { id: 3, titulo: 'Inspeção sanitária', prazo: new Date(Date.now() + 5 * 86400000).toISOString(), status: 'Pendente' }
    ]
  };

  private alertasPorApiario: Record<number, Alerta[]> = {
    1: [
      { id: 1, tipo: 'Clima', mensagem: 'Previsão de chuva forte amanhã.', nivel: 'Atenção' },
      { id: 2, tipo: 'Sanitário', mensagem: 'Monitorar varroa na colmeia A-03.', nivel: 'Info' }
    ],
    2: [
      { id: 3, tipo: 'Produção', mensagem: 'Alta produção no mês atual.', nivel: 'Info' }
    ]
  };

  getApiarios(): Observable<Apiario[]> {
    return of(this.apiarios).pipe(delay(300));
  }

  getApiario(id: number): Observable<Apiario | undefined> {
    const a = this.apiarios.find(x => x.id === id);
    return of(a).pipe(delay(200));
  }

  getColmeias(apiarioId: number): Observable<Colmeia[]> {
    return of(this.colmeiasPorApiario[apiarioId] ?? []).pipe(delay(300));
  }

  getInspecoes(apiarioId: number): Observable<Inspecao[]> {
    return of(this.inspecoesPorApiario[apiarioId] ?? []).pipe(delay(250));
  }

  createInspecao(apiarioId: number, input: {
    data: string;
    responsavel: string;
    observacoes?: string;
    colmeiasVerificadas: number;
  }): Observable<Inspecao> {
    if (!this.inspecoesPorApiario[apiarioId]) {
      this.inspecoesPorApiario[apiarioId] = [];
    }
    const novo: Inspecao = {
      id: Math.max(0, ...this.inspecoesPorApiario[apiarioId].map(i => i.id)) + 1,
      data: input.data,
      responsavel: input.responsavel,
      observacoes: input.observacoes,
      colmeiasVerificadas: input.colmeiasVerificadas
    };
    this.inspecoesPorApiario[apiarioId] = [novo, ...this.inspecoesPorApiario[apiarioId]];
    const apiario = this.apiarios.find(a => a.id === apiarioId);
    if (apiario) {
      apiario.ultimaInspecao = input.data;
    }
    return of(novo).pipe(delay(250));
  }

  getResumoProducao(apiarioId: number): Observable<ProducaoResumo> {
    const colmeias = this.colmeiasPorApiario[apiarioId] ?? [];
    const total = colmeias.reduce((s, c) => s + (c.producaoUltimoMesKg ?? 0), 0);
    const media = colmeias.length ? +(total / colmeias.length).toFixed(2) : 0;
    const resumo: ProducaoResumo = {
      apiarioId,
      anoAtualKg: +(total * 10).toFixed(1), // mock
      mesAtualKg: +total.toFixed(1),
      mediaKgPorColmeia: media,
      melhorMes: 'Agosto'
    };
    return of(resumo).pipe(delay(200));
  }

  getTarefas(apiarioId: number): Observable<Tarefa[]> {
    return of(this.tarefasPorApiario[apiarioId] ?? []).pipe(delay(200));
  }

  getAlertas(apiarioId: number): Observable<Alerta[]> {
    return of(this.alertasPorApiario[apiarioId] ?? []).pipe(delay(200));
  }

  getClima(apiarioId: number): Observable<Clima> {
    // Simulação simples conforme localização
    const base = this.apiarios.find(a => a.id === apiarioId);
    const temp = base?.localizacao?.cidade === 'São Paulo' ? 26 : 28;
    return of({ temperaturaC: temp, umidade: 62, condicao: 'Ensolarado' }).pipe(delay(150));
  }

  createApiario(input: {
    nome: string;
    proprietario?: string;
    localizacao: Localizacao;
    totalColmeias?: number;
  }): Observable<Apiario> {
    const nextId = Math.max(0, ...this.apiarios.map(a => a.id)) + 1;
    const novo: Apiario = {
      id: nextId,
      nome: input.nome,
      proprietario: input.proprietario,
      localizacao: input.localizacao,
      totalColmeias: input.totalColmeias ?? 0,
      colmeiasAtivas: input.totalColmeias ?? 0,
      colmeiasInativas: 0,
      ultimaInspecao: undefined
    };

    this.apiarios.push(novo);
    this.colmeiasPorApiario[nextId] = [];
    this.inspecoesPorApiario[nextId] = [];
    this.tarefasPorApiario[nextId] = [];
    this.alertasPorApiario[nextId] = [];

    return of(novo).pipe(delay(300));
  }

  updateApiario(id: number, input: {
    nome: string;
    proprietario?: string;
    localizacao: Localizacao;
    totalColmeias?: number;
  }): Observable<Apiario> {
    const idx = this.apiarios.findIndex(a => a.id === id);
    if (idx === -1) {
      return of(undefined as unknown as Apiario).pipe(delay(200));
    }
    const atual = this.apiarios[idx];
    const atualizado: Apiario = {
      ...atual,
      nome: input.nome,
      proprietario: input.proprietario,
      localizacao: input.localizacao,
      totalColmeias: input.totalColmeias ?? atual.totalColmeias,
      // Mantém contagens atuais; ajustes de colmeias seriam gerenciados em outra tela
      colmeiasAtivas: atual.colmeiasAtivas,
      colmeiasInativas: atual.colmeiasInativas
    };
    this.apiarios[idx] = atualizado;
    return of(atualizado).pipe(delay(250));
  }
}