import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { Replace } from './../../shared';
import {SessionService} from "../../services/session/session.service";
import {Observable} from "rxjs/Observable";
import {Router} from "@angular/router";
import {Session} from "../../services/session/session";

@Component({
  selector: 'admin-header',
  template: `
    <header class="admin-header navbar">
      <ng-template [ngIf]="mobileSidebarToggler != false">
        <button class="navbar-toggler d-lg-none" type="button" sidebarToggler>
          <span class="navbar-toggler-icon"></span>
        </button>
      </ng-template>
      <ng-template [ngIf]="navbarBrand || navbarBrandFull || navbarBrandMinimized">
        <a class="navbar-brand" href="#">
          <img *ngIf="navbarBrand"
               [src]="imgSrc(navbarBrand)"
               [attr.width]="imgWidth(navbarBrand)"
               [attr.height]="imgHeight(navbarBrand)"
               [attr.alt]="imgAlt(navbarBrand)"
               class="navbar-brand">
          <img *ngIf="navbarBrandFull"
               [src]="imgSrc(navbarBrandFull)"
               [attr.width]="imgWidth(navbarBrandFull)"
               [attr.height]="imgHeight(navbarBrandFull)"
               [attr.alt]="imgAlt(navbarBrandFull)"
               class="navbar-brand-full">
          <img *ngIf="navbarBrandMinimized"
               [src]="imgSrc(navbarBrandMinimized)"
               [attr.width]="imgWidth(navbarBrandMinimized)"
               [attr.height]="imgHeight(navbarBrandMinimized)"
               [attr.alt]="imgAlt(navbarBrandMinimized)"
               class="navbar-brand-minimized">
        </a>
      </ng-template>
      <ng-template [ngIf]="sidebarToggler != false">
        <button class="navbar-toggler d-md-down-none" type="button" [sidebarToggler]="sidebarToggler">
          <span class="navbar-toggler-icon"></span>
        </button>
      </ng-template>
      
      <ul class="nav navbar-nav ml-auto">
        <!--
        <li class="nav-item d-md-down-none">
          <a class="nav-link" href="#"><i class="icon-bell"></i><span class="badge badge-pill badge-danger">5</span></a>
        </li>
        <li class="nav-item d-md-down-none">
          <a class="nav-link" href="#"><i class="icon-list"></i></a>
        </li>
        <li class="nav-item d-md-down-none">
          <a class="nav-link" href="#"><i class="icon-location-pin"></i></a>
        </li>
        -->
        
        <li *ngIf="isLoggedIn() | async" class="nav-item dropdown" dropdown>
          <a href="#" class="nav-link" dropdownToggle (click)="false" aria-controls="basic-link-dropdown">
            <span class="navbar-user-full-name float-right d-md-down-none">{{ userFullName() | async}}</span>
            <span class="navbar-user-icon float-right"></span>
          </a>

          <div id="basic-link-dropdown" class="dropdown-menu dropdown-menu-right dropdown-menu-user" *dropdownMenu aria-labelledby="simple-dropdown">
            <a class="dropdown-item" href="#"><i class="fas fa-user-circle"></i> Profile</a>
            <a class="dropdown-item" href="#"><i class="fas fa-cogs"></i> Settings</a>
            <a class="dropdown-item" href="#" (click)="logout()"><i class="fas fa-sign-out-alt"></i> Logout</a>
          </div>
        </li>

        <li *ngIf="!(isLoggedIn() | async)" class="nav-item">
          <a class="nav-link" (click)="login()">
            <span class="navbar-login float-right d-md-down-none">Login</span>
            <span class="navbar-login-icon float-right"></span>
          </a>
        </li>
      </ul>
    </header>
  `
})
export class AdminHeaderComponent implements OnInit {

  @Input() fixed: boolean;

  @Input() navbarBrand: any;
  @Input() navbarBrandFull: any;
  @Input() navbarBrandMinimized: any;

  @Input() sidebarToggler: any;
  @Input() mobileSidebarToggler: any;

  constructor(private el: ElementRef, private router: Router, private sessionService: SessionService) {}

  ngOnInit() {
    Replace(this.el);
    this.isFixed(this.fixed);
  }

  isFixed(fixed: boolean): void {
    if (this.fixed) { document.querySelector('body').classList.add('admin-header-fixed'); }
  }

  imgSrc(brand: any): void {
    return brand.src ? brand.src : '';
  }

  imgWidth(brand: any): void {
    return brand.width ? brand.width : 'auto';
  }

  imgHeight(brand: any): void {
    return brand.height ? brand.height : 'auto';
  }

  imgAlt(brand: any): void {
    return brand.alt ? brand.alt : '';
  }

  breakpoint(breakpoint: any): void {
    console.log(breakpoint);
    return breakpoint ? breakpoint : '';
  }

  isLoggedIn(): Observable<boolean> {

    return this.sessionService.getSession().map((session : (Session | null)) => {

      if (session) {
        return true;
      }
      else {
        return false;
      }
    });
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.sessionService.logout();

    this.router.navigate(['/']);
  }

  userFullName(): Observable<string> {
    return this.sessionService.getSession().map((session: Session) => {

      if (session) {
        return session.username;
      }
      else {
        return '';
      }
    });
  }
}















// import {Component, OnInit} from '@angular/core';
// import {SessionService} from "../../../services/session/session.service";
// import {Observable} from "rxjs/Observable";
//
//
// @Component({
//   selector: 'admin-header',
//   templateUrl: './admin-header.component.html'
// })
// export class AdminHeaderComponent implements OnInit {
//
//   public showLogin = false;
//
//
//   public constructor(private sessionService: SessionService) {
//
//   }
//
//   ngOnInit(): void {
//   }
//
//
//   public isLoggedIn(): Observable<boolean> {
//
//     return Observable.of(true);
//
//
//     //console.log('Invoking AdminHeaderComponent::isLoggedIn()');
//
//     //return Session.getSession().isLoggedIn();
//
//     //return false;
//   }
//
//
//
//
// }
