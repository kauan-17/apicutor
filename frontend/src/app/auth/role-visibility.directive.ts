import { Directive, Input, TemplateRef, ViewContainerRef, OnChanges, SimpleChanges } from '@angular/core';
import { AuthService } from './auth.service';

@Directive({
  selector: '[appHasRoleAny]',
  standalone: true
})
export class RoleVisibilityDirective implements OnChanges {
  @Input('appHasRoleAny') roles: string[] = [];

  private isVisible = false;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['roles']) {
      this.updateView();
    }
  }

  private updateView(): void {
    const userRoles = this.authService.getRoles();
    const required = (this.roles || []).map(r => (r || '').toUpperCase().startsWith('ROLE_') ? (r || '').toUpperCase() : `ROLE_${(r || '').toUpperCase()}`);
    const allowed = required && required.length > 0
      ? required.some(role => userRoles.includes(role))
      : true;

    if (allowed && !this.isVisible) {
      this.viewContainer.clear();
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.isVisible = true;
    } else if (!allowed && this.isVisible) {
      this.viewContainer.clear();
      this.isVisible = false;
    } else if (!allowed && !this.isVisible) {
      this.viewContainer.clear();
    }
  }
}