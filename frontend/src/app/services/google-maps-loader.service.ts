import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleMapsLoaderService {
  private loadingPromise?: Promise<void>;

  load(): Promise<void> {
    if (typeof (window as any).google !== 'undefined' && (window as any).google.maps) {
      return Promise.resolve();
    }
    if (this.loadingPromise) {
      return this.loadingPromise;
    }

    const apiKey = environment.googleMapsApiKey;
    if (!apiKey) {
      console.warn('Google Maps API key não configurada em environment.googleMapsApiKey');
    }

    this.loadingPromise = new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey || ''}`;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = (err) => reject(err);
      document.head.appendChild(script);
    });

    return this.loadingPromise;
  }
}
