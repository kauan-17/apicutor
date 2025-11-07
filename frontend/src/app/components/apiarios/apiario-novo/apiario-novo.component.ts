import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';

@Component({
  selector: 'app-apiario-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './apiario-novo.component.html',
  styleUrls: ['./apiario-novo.component.css']
})
export class ApiarioNovoComponent {
  nome = '';
  proprietario = '';
  endereco = '';
  cidade = '';
  estado = '';
  latitude?: number;
  longitude?: number;
  descricao = '';
  feedback?: string;
  saving = false;

  constructor(private router: Router, private apiarioHttp: ApiarioService) {}

  salvar() {
    this.saving = true;
    const payload = {
      nome: this.nome.trim(),
      localizacao: [this.endereco, this.cidade, this.estado].filter(Boolean).join(', '),
      latitude: this.latitude ?? null,
      longitude: this.longitude ?? null,
      descricao: this.descricao.trim() || null
    };

    this.apiarioHttp.createApiario(payload).subscribe({
      next: (novo) => {
        this.feedback = 'Apiário criado com sucesso.';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/apiarios', novo.id]), 600);
      },
      error: () => {
        this.feedback = 'Falha ao criar apiário.';
        this.saving = false;
      }
    });
  }

  cancelar() {
    this.router.navigate(['/dashboard']);
  }
}