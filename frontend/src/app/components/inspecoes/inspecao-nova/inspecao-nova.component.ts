import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiariosService } from '../../../services/apiarios.service';

@Component({
  selector: 'app-inspecao-nova',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './inspecao-nova.component.html',
  styleUrls: ['./inspecao-nova.component.css']
})
export class InspecaoNovaComponent {
  apiarioId!: number;
  responsavel = '';
  data = new Date().toISOString().substring(0, 10);
  observacoes = '';
  colmeiasVerificadas = 0;
  feedback?: string;

  constructor(private route: ActivatedRoute, private router: Router, private apiariosService: ApiariosService) {
    this.route.params.subscribe(p => {
      const id = +p['id'];
      this.apiarioId = id || 1;
    });
  }

  salvar() {
    const payload = {
      data: this.data,
      responsavel: this.responsavel || 'Usuário',
      observacoes: this.observacoes,
      colmeiasVerificadas: this.colmeiasVerificadas || 0
    };
    this.apiariosService.createInspecao(this.apiarioId, payload).subscribe({
      next: () => {
        this.feedback = 'Inspeção registrada com sucesso.';
        setTimeout(() => this.router.navigate(['/apiarios', this.apiarioId]), 600);
      },
      error: () => {
        this.feedback = 'Falha ao registrar inspeção.';
        setTimeout(() => (this.feedback = undefined), 3000);
      }
    });
  }

  cancelar() {
    this.router.navigate(['/apiarios', this.apiarioId]);
  }
}