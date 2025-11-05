import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

// Definindo interfaces para os tipos
interface Apiario {
  id: number;
  nome: string;
  colmeias: number;
  localizacao?: string;
}

interface Colmeia {
  id: number;
  identificacao: string;
  tipo: string;
  status: string;
}

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  apiarios: Apiario[] = [];
  colmeias: Colmeia[] = [];
  producaoTotal = 0;
  carregando = true;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    // Simulando dados para demonstração
    setTimeout(() => {
      this.apiarios = [
        { id: 1, nome: 'Apiário Central', colmeias: 15, localizacao: 'Área Central, Fazenda São João' },
        { id: 2, nome: 'Apiário Norte', colmeias: 12, localizacao: 'Zona Norte, Sítio Verde' },
        { id: 3, nome: 'Apiário Sul', colmeias: 8, localizacao: 'Região Sul, Chácara Flora' }
      ];

      this.colmeias = [
        { id: 1, identificacao: 'C001', tipo: 'Langstroth', status: 'ATIVA' },
        { id: 2, identificacao: 'C002', tipo: 'Langstroth', status: 'ATIVA' },
        { id: 3, identificacao: 'C003', tipo: 'Top Bar', status: 'EM_OBSERVACAO' }
      ];

      this.producaoTotal = 245.5;
      this.carregando = false;
    }, 1000);
  }

  getActiveColmeiasCount(): number {
    return this.colmeias.filter(c => c.status === 'ATIVA').length;
  }

  // trackBy helpers para *ngFor
  trackByApiario(_index: number, apiario: Apiario | undefined): number | undefined {
    return apiario?.id;
  }

  trackByColmeia(_index: number, colmeia: Colmeia | undefined): number | undefined {
    return colmeia?.id;
  }
}