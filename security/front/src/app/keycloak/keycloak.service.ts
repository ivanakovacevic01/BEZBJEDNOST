import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { User } from '../models/user.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;
  constructor(private router: Router) {} 

  get keycloak() {
    //console.log("sta je ovo"+this._keycloak)
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'SpringBootDemoKeyCloak',
        clientId: 'springboot-keycloak-demo-client'
      });
    }
    //console.log(this._keycloak)
    return this._keycloak;
  }

  private _profile: User | undefined;

  get profile(): User | undefined {
    return this._profile;
  }

  async init() {
    console.log("Authentication the user...")
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',
    });
    console.log(authenticated);

    if (authenticated) {
      console.log("User authenticated...")
      console.log(this._keycloak?.token)
      //this._profile = (await this.keycloak.loadUserInfo()) as User;
      
      //this._profile.token = this.keycloak.token || '';
      this.router.navigate(['home_page']);
    }
   console.log("Keycloak");
  }

  login() {
    return this.keycloak.login();
  }

  logout() {
    //this.keycloak.accountManagement();
    return this.keycloak.logout({redirectUri: 'https://localhost:4200'});
  }
}
