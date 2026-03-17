import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface GeocodingResult {
  cep: string;
  endereco?: string;
  latitude: number;
  longitude: number;
  displayName?: string;
}

@Injectable({ providedIn: 'root' })
export class GeocodingService {
  constructor(private http: HttpClient) {}

  geocodeCep(cep: string): Observable<GeocodingResult> {
    return this.http.get<GeocodingResult>(`${environment.apiUrl}/geocoding/cep/${encodeURIComponent(cep)}`);
  }
}

