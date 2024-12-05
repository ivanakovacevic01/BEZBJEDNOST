import { Component, OnInit } from '@angular/core';
import { AuthService } from './login/auth.service';
import { Router } from '@angular/router';
import { KeycloakService } from './keycloak/keycloak.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent{
  title = 'front';
  constructor(private router:Router,private authService:AuthService,private keycloakService:KeycloakService){}

  onLogin(){
    this.router.navigate(['login'])
  }
  onLogout(){
    /*localStorage.removeItem("jwt")
    localStorage.removeItem("refreshToken")
    this.router.navigate(['login'])*/
    return this.keycloakService.keycloak.logout({redirectUri: 'https://localhost:4200'});
  }
  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }
  isEmployee(): boolean {
    const userRoles = this.authService.getUserRoles();
    return userRoles !== null && userRoles.includes('ROLE_EMPLOYEE');
  }
  isClient(): boolean {
    const userRoles = this.authService.getUserRoles();
    return userRoles !== null && userRoles.includes('ROLE_CLIENT');
  }

  isAdmin(): boolean {
    const userRoles = this.authService.getUserRoles();
    return userRoles !== null && userRoles.includes('ROLE_ADMIN');
  }
}
