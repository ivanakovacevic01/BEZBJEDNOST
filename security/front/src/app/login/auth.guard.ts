import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { KeycloakService } from '../keycloak/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  tokenService = inject(KeycloakService);
  router = inject(Router);

  constructor(private authService: AuthService) {}
  
  canActivate(): boolean {
    // if (this.authService.isAuthenticated()) {
    //   return true; // Korisnik je autentifikovan, dozvoli pristup
    // } else {
    //   this.router.navigate(['/login']); // Preusmeri na stranicu za prijavu
    //   return false; // Ne dozvoli pristup
    // }
    if (this.tokenService.keycloak.isTokenExpired()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  
  }
}