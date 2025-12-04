import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';
import { GoogleMapsModule } from '@angular/google-maps';
import { GoogleMapsLoaderService } from '../../../services/google-maps-loader.service';

@Component({
  selector: 'app-apiario-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, GoogleMapsModule],
  templateUrl: './apiario-novo.component.html',
  styleUrls: ['./apiario-novo.component.css']
})
export class ApiarioNovoComponent {
  nome = '';
  proprietario = '';
  endereco = '';
  cidade = '';
  estado = '';
  descricao = '';
  feedback?: string;
  saving = false;
  mapsReady = false;
  mapCenter: { lat: number; lng: number } = { lat: -23.55052, lng: -46.633308 }; // São Paulo default
  mapZoom = 12;
  markerPosition: { lat: number; lng: number } | null = null;

  constructor(private router: Router, private apiarioHttp: ApiarioService, private mapsLoader: GoogleMapsLoaderService) {
    this.mapsLoader.load().then(() => {
      this.mapsReady = true;
    }).catch(() => {
      this.feedback = 'Não foi possível carregar o Google Maps. Verifique a chave de API.';
    });
  }

  private getAddressString(): string {
    return [this.endereco, this.cidade, this.estado].filter(Boolean).join(', ').trim();
  }

  mostrarNoMapa() {
    if (!this.mapsReady) return;
    const address = this.getAddressString();
    if (!address) {
      this.feedback = 'Informe endereço, cidade e estado para visualizar no mapa.';
      return;
    }
    const gmaps = (window as any).google && (window as any).google.maps;
    if (!gmaps) return;
    const geocoder = new gmaps.Geocoder();
    geocoder.geocode({ address }, (results: any, status: any) => {
      if (status === 'OK' && results && results[0] && results[0].geometry) {
        const loc = results[0].geometry.location;
        const lat = typeof loc.lat === 'function' ? loc.lat() : loc.lat;
        const lng = typeof loc.lng === 'function' ? loc.lng() : loc.lng;
        this.mapCenter = { lat, lng };
        this.markerPosition = { lat, lng };
        this.mapZoom = 15;
        this.feedback = undefined;
      } else {
        this.feedback = 'Endereço não encontrado. Ajuste os dados e tente novamente.';
      }
    });
  }

  salvar() {
    this.saving = true;
    const payload = {
      nome: this.nome.trim(),
      localizacao: [this.endereco, this.cidade, this.estado].filter(Boolean).join(', '),
      descricao: this.descricao.trim() || undefined
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
    this.router.navigate(['/apiarios']);
  }
}
