import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-apiarios',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './apiarios.component.html',
  styleUrls: ['./apiarios.component.css']
})
export class ApiariosComponent {
  selectedId?: number;

  constructor(private route: ActivatedRoute) {
    this.route.params.subscribe(params => {
      const id = params['id'];
      this.selectedId = id ? +id : undefined;
    });
  }
}