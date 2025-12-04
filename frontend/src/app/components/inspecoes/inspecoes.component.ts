import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { InspecaoService } from '../../services/inspecao.service';

@Component({
  selector: 'app-inspecoes',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './inspecoes.component.html',
  styleUrls: []
})
export class InspecoesComponent implements OnInit {
  loading = true;
  items: any[] = [];
  colmeiaId?: number;

  constructor(private route: ActivatedRoute, private service: InspecaoService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      this.colmeiaId = idParam ? Number(idParam) : undefined;
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    const obs = this.colmeiaId ? this.service.getByColmeia(this.colmeiaId) : this.service.getAll();
    obs.subscribe({ next: (res) => { this.items = res ?? []; this.loading = false; }, error: () => { this.items = []; this.loading = false; } });
  }
}

