import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

// Definindo interfaces para os tipos
interface Apiario {
  id: number;
  nome: string;
  colmeias: number;
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
        { id: 1, nome: 'Apiário Central', colmeias: 15 },
        { id: 2, nome: 'Apiário Norte', colmeias: 12 },
        { id: 3, nome: 'Apiário Sul', colmeias: 8 }
      ];

      this.colmeias = [
        { id: 1, identificacao: 'C001', tipo: 'Langstroth', status: 'Ativa' },
        { id: 2, identificacao: 'C002', tipo: 'Langstroth', status: 'Ativa' },
        { id: 3, identificacao: 'C003', tipo: 'Top Bar', status: 'Manutenção' }
      ];

      this.producaoTotal = 245.5;
      this.carregando = false;
    }, 1000);
  }
}