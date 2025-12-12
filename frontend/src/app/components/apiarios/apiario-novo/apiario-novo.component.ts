import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';
import * as L from 'leaflet';
import { AfterViewInit, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-apiario-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './apiario-novo.component.html',
  styleUrls: ['./apiario-novo.component.css']
})
export class ApiarioNovoComponent implements AfterViewInit {
  nome = '';
  proprietario = '';
  endereco = '';
  setor = '';
  complemento = '';
  cidade = '';
  estado = '';
  descricao = '';
  cep = '';
  latitude: number | null = null;
  longitude: number | null = null;
  feedback?: string;
  saving = false;
  leafReady = false;
  mapCenter: { lat: number; lng: number } = { lat: -23.55052, lng: -46.633308 };
  mapZoom = 12;
  markerPosition: { lat: number; lng: number } | null = null;
  leafletMap?: L.Map;
  leafletMarker?: L.Marker;
  @ViewChild('leafletNovoMap') leafletDiv?: ElementRef<HTMLDivElement>;

  constructor(private router: Router, private apiarioHttp: ApiarioService) {}

  ngAfterViewInit(): void {
    // Inicializa mapa com centro padrão
    setTimeout(() => this.initLeafletNovoMap(), 0);
  }

  private getAddressString(): string {
    return [this.endereco, this.setor, this.complemento, this.cidade, this.estado].filter(Boolean).join(', ').trim();
  }

  buscarEnderecoPorCEP() {
    const cep = (this.cep || '').replace(/\D/g, '');
    if (!cep || cep.length !== 8) { this.feedback = 'CEP inválido'; return; }
    fetch(`https://viacep.com.br/ws/${cep}/json/`)
      .then(r => r.json())
      .then((data: any) => {
        if (data?.erro) { this.feedback = 'CEP não encontrado'; return; }
        this.endereco = data?.logradouro || this.endereco;
        this.setor = data?.bairro || this.setor;
        this.cidade = data?.localidade || this.cidade;
        this.estado = data?.uf || this.estado;
        this.complemento = data?.complemento || this.complemento;
        this.feedback = undefined;
      })
      .catch(() => { this.feedback = 'Falha ao consultar CEP'; });
  }

  mostrarNoMapa() {
    const lat = Number(this.latitude);
    const lng = Number(this.longitude);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) { this.feedback = 'Latitude e longitude são obrigatórias'; return; }
    this.mapCenter = { lat, lng };
    this.markerPosition = { lat, lng };
    this.mapZoom = 15;
    if (!this.leafletMap) this.initLeafletNovoMap();
    if (this.leafletMap) this.leafletMap.setView([lat, lng], this.mapZoom);
    if (this.leafletMarker) {
      this.leafletMarker.setLatLng([lat, lng]);
    } else if (this.leafletMap) {
      const icon = L.icon({
        iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
        iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
        shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
        iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
      });
      this.leafletMarker = L.marker([lat, lng], { icon }).addTo(this.leafletMap);
    }
    this.feedback = undefined;
  }

  private initLeafletNovoMap(): void {
    const container = this.leafletDiv?.nativeElement || document.getElementById('leafletNovoMap');
    if (!container) return;
    if (this.leafletMap) {
      this.leafletMap.remove();
      this.leafletMap = undefined;
    }
    const { lat, lng } = this.mapCenter;
    this.leafletMap = L.map(container).setView([lat, lng], this.mapZoom);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.leafletMap);
    const icon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });
    if (this.markerPosition) {
      this.leafletMarker = L.marker([this.markerPosition.lat, this.markerPosition.lng], { icon }).addTo(this.leafletMap);
    }
    this.leafReady = true;
  }

  salvar() {
    this.saving = true;
    const payload = {
      nome: this.nome.trim(),
      localizacao: [this.endereco, this.setor, this.complemento, this.cidade, this.estado].filter(Boolean).join(', '),
      descricao: this.descricao.trim() || undefined
    };
    const lat = Number(this.latitude);
    const lng = Number(this.longitude);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) { this.feedback = 'Latitude e longitude são obrigatórias'; this.saving = false; return; }
    (payload as any).latitude = lat;
    (payload as any).longitude = lng;

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
