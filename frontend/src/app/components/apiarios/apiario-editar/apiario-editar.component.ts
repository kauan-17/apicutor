import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';
import { GeocodingService } from '../../../services/geocoding.service';

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
  cep = '';
  latitude?: number;
  longitude?: number;
  descricao = '';
  feedback?: string;
  saving = false;
  buscandoCoords = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiarioHttp: ApiarioService,
    private geocoding: GeocodingService
  ) {
    this.route.params.subscribe(p => {
      const id = +p['id'];
      this.apiarioId = id || 1;
      this.carregar();
    });
  }

  get cepDigits(): string {
    return (this.cep || '').replace(/\D/g, '');
  }

  get cepValido(): boolean {
    return this.cepDigits.length === 8;
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

  buscarCoordenadasPorCEP() {
    if (!this.cepValido) { this.feedback = 'CEP inválido'; return; }
    this.buscandoCoords = true;
    this.geocoding.geocodeCep(this.cepDigits).subscribe({
      next: (res) => {
        this.latitude = res.latitude;
        this.longitude = res.longitude;
        this.buscandoCoords = false;
        this.feedback = undefined;
      },
      error: () => {
        this.buscandoCoords = false;
        this.feedback = 'Falha ao buscar latitude/longitude';
      }
    });
  }

  salvar() {
    this.saving = true;
    const payload = {
      nome: this.nome.trim(),
      localizacao: this.localizacaoStr.trim() || undefined,
      latitude: this.latitude ?? undefined,
      longitude: this.longitude ?? undefined,
      descricao: this.descricao.trim() || undefined
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
