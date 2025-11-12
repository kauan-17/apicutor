import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthService } from './auth.service';

@Directive({
  selector: '[appHasRole]'
})
export class HasRoleDirective {
  private hasView = false;
  private requiredRoles: string[] = [];

  constructor(
    private tpl: TemplateRef<any>,
    private vcr: ViewContainerRef,
    private auth: AuthService
  ) {}

  @Input() set appHasRole(roleOrRoles: string | string[]) {
    this.requiredRoles = Array.isArray(roleOrRoles) ? roleOrRoles : [roleOrRoles];
    this.updateView();
  }

  private updateView() {
    const userRoles = this.auth.getRoles();
    const allowed = this.requiredRoles.some(r => userRoles.includes(r));
    if (allowed && !this.hasView) {
      this.vcr.createEmbeddedView(this.tpl);
      this.hasView = true;
    } else if (!allowed && this.hasView) {
      this.vcr.clear();
      this.hasView = false;
    }
  }
}