import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';

@Component({
  selector: 'app-apiario-editar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './apiario-editar.component.html',
  styleUrls: ['./apiario-editar.component.css']
})
export class ApiarioEditarComponent {
  apiarioId!: number;
  nome = '';
  proprietario = '';
  localizacaoStr = '';
  latitude?: number;
  longitude?: number;
  descricao = '';
  feedback?: string;
  saving = false;

  constructor(private route: ActivatedRoute, private router: Router, private apiarioHttp: ApiarioService) {
    this.route.params.subscribe(p => {
      const id = +p['id'];
      this.apiarioId = id || 1;
      this.carregar();
    });
  }

  carregar() {
    this.apiarioHttp.getApiario(this.apiarioId).subscribe((a: any) => {
      if (!a) return;
      this.nome = a.nome;
      this.localizacaoStr = a.localizacao || '';
      this.latitude = a.latitude ?? undefined;
      this.longitude = a.longitude ?? undefined;
      this.descricao = a.descricao || '';
    });
  }

  salvar() {
    this.saving = true;
    const payload = {
      nome: this.nome.trim(),
      localizacao: this.localizacaoStr.trim() || null,
      latitude: this.latitude ?? null,
      longitude: this.longitude ?? null,
      descricao: this.descricao.trim() || null
    };

    this.apiarioHttp.updateApiario(this.apiarioId, payload).subscribe({
      next: () => {
        this.feedback = 'Apiário atualizado com sucesso.';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/apiarios', this.apiarioId]), 600);
      },
      error: () => {
        this.feedback = 'Falha ao atualizar apiário.';
        this.saving = false;
      }
    });
  }

  cancelar() {
    this.router.navigate(['/apiarios', this.apiarioId]);
  }
}