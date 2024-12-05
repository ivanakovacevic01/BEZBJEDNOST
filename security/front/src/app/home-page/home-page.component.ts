import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppComponent } from '../app.component';
import { AuthService } from '../login/auth.service';
import { UserTokenState } from '../models/tokenState.model';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../dialog/dialog.component';
import { KeycloakService } from '../keycloak/keycloak.service';


@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class HomePageComponent implements OnInit,OnDestroy{
  private intervalId: any;
  public expired: boolean = false;

  logout() {
   /* localStorage.removeItem('jwt');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['login']);*/
    return this.keycloakService.keycloak.logout({redirectUri: 'https://localhost:4200'});
  }

  constructor(private router:Router,private authService:AuthService,private dialog:MatDialog,private keycloakService:KeycloakService){}
  ngOnInit(): void {
    this.intervalId = setInterval(() => {
      this.checkExpiration();
    }, 5000);
  }
  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
  checkExpiration() {
    this.expired = this.isExpired();
    if(this.expired)
      alert("To continue, refresh your token.")
  }


  userTokenState:UserTokenState={
    accesstoken:"",
    refreshtoken:""
  }

  onRegistrationRequests() {
    this.router.navigate(['registration-requests-overview']);
  }

  onRegisterAdminForm() {
    this.router.navigate(['admin-registration']);
  }
  onRegisterEmployeeForm() {
    this.router.navigate(['employee-registration']);
  }
  onEmployeesView() {
    this.router.navigate(['employees-overview']);
  }
  onClientsView() {
    this.router.navigate(['clients-overview']);
  }
  onEmployeeProfileView() {
    this.router.navigate(['employee-profile']);
  }
  onAdminProfileView() {
    this.router.navigate(['admin-profile']);
  }
  onClientProfileView() {
    this.router.navigate(['client-profile']);
  }
  onCreateAdvertisementRequest() {
    this.router.navigate(['create-advertisement-request']);
  }
  onAdvertisementRequestView() {
    this.router.navigate(['advertisement-request']);
  }
  onAdvertisementsView() {
    this.router.navigate(['advertisements']);
  }



  onRegenerateAccessToken():void{
    this.userTokenState.accesstoken=localStorage.getItem("jwt")||""
    this.userTokenState.refreshtoken=localStorage.getItem("refreshToken")||""


    this.authService.regenereateAccessToken(this.userTokenState);
    this.expired = false;
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

  onEmployeeProfile(){
    this.router.navigate(['employee-profile'])
  }

  onAdminProfile(){
    this.router.navigate(['admin-profile'])
  }

  onClientProfile(){
    this.router.navigate(['client-profile'])

  }

  isExpired(): boolean {
    const token=this.authService.getAuthToken()
    if(token){
      const exp=this.parseJwt(token).exp;
      const expirationTimestamp = parseInt(exp, 10) * 1000; // Pretvaranje iz sekundi u milisekunde
      console.log(expirationTimestamp);
      const now=new Date().getTime();
      console.log(now)
      if(now>expirationTimestamp)
        return true;
      else
        return false;
    }

    return true;

  }

  private parseJwt(token: string): any {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  }
  onPermissionManage(){
    this.router.navigate(['permission-manage'])

  }

  onVPN(){
    this.authService.getVpnMessage().subscribe(data=>{
      console.log(data.message)
      const dialogRef = this.dialog.open(DialogComponent, {
        width: '500px',
        data: { message: data.message }
      });
    })
  }

  onClientsManagment(){
    this.router.navigate(['clients-blocking'])

  }
}
