import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { JwtAuthenticationRequest } from '../models/jwtAuthenticationRequest.model';
import { Router } from '@angular/router';
import { ProfileService } from '../profile/profile.service';
import { VerificationRequestDto } from '../models/verificationRequest.model';
import { UserTokenState } from '../models/tokenState.model';
import { PasswordlessAuthenticationRequest } from '../models/passwordlessAuthenticationRequest.model';
import { PasswordlessLoginRequest } from '../models/changePasswordRequest.model';
import { PasswordChangeDto } from '../models/resetPassword.model';
import { KeycloakService } from '../keycloak/keycloak.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  request:JwtAuthenticationRequest={
    email:"",
    password:""
  };

  verificationRequest:VerificationRequestDto={
    email:"",
    code:""
  };

  passwordChangeRequest:PasswordlessLoginRequest={
    email:""
  };

  qrcode:string;
  visibility:boolean=true;
  pvisibility:boolean=false;
  email:string;

  constructor(private authService: AuthService,private router:Router,private profileService:ProfileService,private ss: KeycloakService) {}


  async ngOnInit(): Promise<void> {
    await this.ss.init();
    await this.ss.login();
  }

 /* login(): void {
    console.log(this.request)
    this.verificationRequest.email=this.request.email;
    this.authService.login(this.request).subscribe((data:UserTokenState)=>{
      console.log(data)
      this.qrcode=data.secretImageUri||"";
      this.visibility=false;
    /*  this.profileService.getUser(this.request.email).subscribe({
        next: (result:any) => {
          if(result.passwordChanged || result.roles[0].name=='ROLE_CLIENT'){
            this.router.navigate(['home_page'])
          }
          else
            this.router.navigate(['change-password'])
        },
      })*/
      
    /*},
    err => {
      if (err.status === 401) {
        alert("Bad credentials.");
      } 

    });
  }*/

  verify():void{
    this.authService.verify(this.verificationRequest).subscribe(data=>{
      console.log(data)
      this.profileService.getUser(this.request.email).subscribe({
        next: (result:any) => {
          if(result.passwordChanged || result.roles[0].name=='ROLE_CLIENT'){
            this.router.navigate(['home_page'])
          }
          else
            this.router.navigate(['change-password'])
        },
      })
      
    },
    err => {
      if (err.status === 401) {
        alert("Bad credentials.");
      } 

    });
  }

  onRegisterNewClient(){
    this.router.navigate(['registration'])
  }

  onPasswordlessLogin(){
    this.router.navigate(['passwordless_login'])
  }

  onResetPassword(){
    this.pvisibility=true;
  }

  change(){
    this.passwordChangeRequest.email=this.email
    this.authService.resetPassword(this.passwordChangeRequest).subscribe()
    alert("Check email")
    this.pvisibility=false;


  }
}
