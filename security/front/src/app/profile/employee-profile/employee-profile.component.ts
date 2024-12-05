import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { ProfileService } from '../profile.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/login/auth.service';
import { Address } from 'src/app/models/address.model';

@Component({
  selector: 'app-employee-profile',
  templateUrl: './employee-profile.component.html',
  styleUrls: ['./employee-profile.component.css'],
})
export class EmployeeProfileComponent implements OnInit, OnChanges {
  constructor(
    private service: ProfileService,
    private authService: AuthService
  ) {}
  email: string = '';

  ngOnInit(): void {
    this.email = this.authService.getUser().email;
    this.service.getEmployee(this.email).subscribe({
      next: (result: User) => {
        this.user = result;
        console.log(this.user);
      },
      error:(error) => {
        if (error.status === 401) {
          alert(
            "Unauthorized. Maybe you should refresh your access token. "
          );
        }
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {}
  onEditMode() {
    this.editMode = true;
    this.userForm.reset();
    console.log(this.user);
    this.userForm.patchValue(this.user);
    this.addressForm.patchValue(this.user.address);
    this.userForm.patchValue({
      confirmPassword: '',
      password: '',
    });
  }
  address: Address = {
    city: '',
    country: '',
    street: '',
    streetNumber: '',
  };
  user: User = {
    id: 0,
    email: '',
    password: '',
    firstName: '',
    username: '',
    lastName: '',
    address: this.address,
    phone: '',
    blocked:false
  };

  editMode = false;
  addressForm = new FormGroup({
    city: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[A-Z][A-Za-z ]*$/),
    ]),
    country: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[A-Z][A-Za-z ]*$/),
    ]),
    street: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[A-Z][A-Za-z ]*$/),
    ]),
    streetNumber: new FormControl('', []),
  });

  userForm = new FormGroup({
    password: new FormControl(''),
    confirmPassword: new FormControl('', [Validators.required]),
    firstName: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[A-Z][A-Za-z ]*$/),
    ]),
    lastName: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[A-Z][A-Za-z ]*$/),
    ]),

    phone: new FormControl('', [
      Validators.required,
      Validators.pattern(/^[0-9]{9,10}$/),
    ]),
  });

  updateProfile(): void {
    var hasPermission = false;
    var roles=this.authService.getUserRoles();
    console.log(roles);
    roles?.forEach((p) => {
      if (p == 'ROLE_EMPLOYEE') {
        hasPermission = true;
      }
    });
    if(hasPermission) {
    if (this.isInputValid) {
      var oldUsername = this.user.username;
      const address: Address = {
        city: this.addressForm.value.city || '',
        country: this.addressForm.value.country || '',
        street: this.addressForm.value.street || '',
        streetNumber: this.addressForm.value.streetNumber || '',
      };
      const user: User = {
        id: this.user!.id,
        email: this.user!.email,
        password: this.userForm.value.password || '',
        firstName: this.userForm.value.firstName || '',
        username: this.user.username || '',
        lastName: this.userForm.value.lastName || '',
        address: address,
        phone: this.userForm.value.phone || '',
        blocked:false
      };
      if (
        (this.userForm.value.confirmPassword == '' ||
          this.userForm.value.confirmPassword == undefined ||
          this.userForm.value.confirmPassword == null) &&
        (this.userForm.value.password == '' ||
          this.userForm.value.password == undefined ||
          this.userForm.value.password == null)
      ) {
        user.password = this.user.password;
        console.log(user.password);
        console.log(this.user.password);
      } else if (!this.isPasswordInvalid && !this.isConfirmPasswordInvalid) {
        user.password = this.userForm.value.password || '';
      }
      var hashPassword = true;
      if (
        this.userForm.value.password == '' &&
        this.userForm.value.confirmPassword == ''
      ) {
        user.password = this.user.password;
        hashPassword = false;
      }
      console.log(user.password);
      console.log(hashPassword);
      this.service
        .updateEmployee(user, hashPassword, 'ROLE_EMPLOYEE')
        .subscribe({
          next: (result: User) => {
            if (result == null) alert('Please enter valid data.');
            else {
              this.user = result;
              this.editMode = false;
            }
          },
          error:(error) => {
            if (error.status === 401) {
              alert(
                "Unauthorized. Maybe you should refresh your access token. "
              );
            }
          }
        });
    } else alert('Please enter valid data.');
  }
  else
    alert("No permission.")
  }

  get isUsernameInvalid(): boolean {
    const usernameControl = this.userForm.get('username');
    return (
      !!usernameControl &&
      usernameControl.invalid &&
      (usernameControl.dirty || usernameControl.touched)
    );
  }

  get isNameInvalid(): boolean {
    const nameControl = this.userForm.get('firstName');
    return (
      !!nameControl &&
      nameControl.invalid &&
      (nameControl.dirty || nameControl.touched)
    );
  }

  get isSurnameInvalid(): boolean {
    const surnameControl = this.userForm.get('lastName');
    return (
      !!surnameControl &&
      surnameControl.invalid &&
      (surnameControl.dirty || surnameControl.touched)
    );
  }

  get isCityInvalid(): boolean {
    const cityControl = this.userForm.get('city');
    return (
      !!cityControl &&
      cityControl.invalid &&
      (cityControl.dirty || cityControl.touched)
    );
  }

  get isCountryInvalid(): boolean {
    const countryControl = this.userForm.get('country');
    return (
      !!countryControl &&
      countryControl.invalid &&
      (countryControl.dirty || countryControl.touched)
    );
  }

  get isStreetInvalid(): boolean {
    const streetControl = this.userForm.get('street');
    return (
      !!streetControl &&
      streetControl.invalid &&
      (streetControl.dirty || streetControl.touched)
    );
  }

  get isNumberInvalid(): boolean {
    const numberControl = this.userForm.get('phone');
    return (
      !!numberControl &&
      numberControl.invalid &&
      (numberControl.dirty || numberControl.touched)
    );
  }

  get isPasswordInvalid(): boolean {
    const passwordControl = this.userForm.get('password');
    return (
      !!passwordControl &&
      passwordControl.invalid &&
      (passwordControl.dirty || passwordControl.touched)
    );
  }

  get isConfirmPasswordInvalid(): boolean {
    const passwordControl = this.userForm.get('password');
    const confirmPasswordControl = this.userForm.get('confirmPassword');
    return (
      !!passwordControl &&
      !!confirmPasswordControl &&
      passwordControl.value !== confirmPasswordControl.value
    );
  }

  get isInputValid(): boolean {
    return (
      !this.isNameInvalid &&
      !this.isSurnameInvalid &&
      !this.isUsernameInvalid &&
      !this.isNumberInvalid &&
      !this.isCityInvalid &&
      !this.isCountryInvalid &&
      !this.isStreetInvalid
    );
  }
}
