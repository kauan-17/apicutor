import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InspecaoService } from '../../services/inspecao.service';

@Component({
  selector: 'app-inspecoes',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './inspecoes.component.html',
  styleUrls: []
})
export class InspecoesComponent {
  loading = true;
  error?: string;
  inspecoes: any[] = [];

  constructor(private service: InspecaoService) {
    this.carregar();
  }

  carregar(): void {
    this.loading = true; this.error = undefined;
    this.service.listAll().subscribe({
      next: (res) => { this.inspecoes = res ?? []; this.loading = false; },
      error: () => { this.error = 'Falha ao carregar inspeções'; this.loading = false; }
    });
  }
}
