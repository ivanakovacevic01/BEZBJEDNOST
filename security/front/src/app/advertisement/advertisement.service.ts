import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AdvertisementRequest } from '../models/advertisement-request.model';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Advertisement } from '../models/advertisement.model';
import { AsyncAction } from 'rxjs/internal/scheduler/AsyncAction';
import { HttpHeaders } from '@angular/common/http';
import { KeycloakService } from '../keycloak/keycloak.service';
@Injectable({
  providedIn: 'root',
})
export class AdvertisementService {
  constructor(private http: HttpClient,private keycloakService:KeycloakService) {}
  createAdvertisementRequest(
    request: AdvertisementRequest
  ): Observable<AdvertisementRequest> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.post<AdvertisementRequest>(
      environment.apiHost + 'advertisementRequest/creating/' + request.clientId,
      request,
      options
    );
  }
  getAllRequests(): Observable<AdvertisementRequest[]> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<AdvertisementRequest[]>(
      environment.apiHost + 'advertisementRequest',
      options
    );
  }
  getRequestById(id: number): Observable<AdvertisementRequest> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<AdvertisementRequest>(
      environment.apiHost + 'advertisementRequest/' + id,
      options
    );
  }
  createAdvertisement(
    request: Advertisement,
    id: number,
    requestId: number
  ): Observable<Advertisement> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.post<Advertisement>(
      environment.apiHost + 'advertisement/creating/' + id + '/' + requestId,
      request,
      options
    );
  }
  getClientId(id: number): Observable<number> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<number>(
      environment.apiHost + 'advertisementRequest/clientId/' + id,
      options
    );
  }
  getAllAdvertisements(): Observable<Advertisement[]> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<Advertisement[]>(
      environment.apiHost + 'advertisement',
      options
    );
  }
  getAllAdvertisementsByUserId(id: number): Observable<Advertisement[]> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<Advertisement[]>(
      environment.apiHost + 'advertisement/gettingByClient/' + id,
      options
    );
  }

  rejectAdvertisementRequest(
    request: AdvertisementRequest
  ): Observable<AdvertisementRequest> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.put<any>(
      environment.apiHost + 'advertisementRequest/reject',
      request,
      options
    );
  }

  advClick(id: number, packageType: String): Observable<String> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<String>(
      environment.apiHost + 'advertisement/visiting/' + id + '/' + packageType,
      options
    );
  }

  getAdvertisementById(id: number): Observable<Advertisement> {
    //const jwtToken = localStorage.getItem('jwt');
    const jwtToken = this.keycloakService.keycloak.token;
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + jwtToken,
    });

    const options = {
      headers: headers,
    };
    return this.http.get<Advertisement>(
      environment.apiHost + 'advertisement/' + id,
      options
    );
  }
}
