import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { KeycloakService } from '../keycloak/keycloak.service';

@Injectable({
  providedIn: 'root',
})
export class PeopleService {
  constructor(private http: HttpClient,private keycloakService:KeycloakService) {}

  getEmployees(): Observable<any> {
    const jwtToken = this.keycloakService.keycloak.token;

    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<any>(environment.apiHost + 'user/employees/',options);
  }
  getClients(): Observable<any> {
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<any>(environment.apiHost + 'user/clients/',options);
  }
}
