import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { MainLayouts } from './layouts/main-layouts/main-layouts';
import { Home } from './pages/home/home';
import { Tasks } from './pages/tasks/tasks';
import { Departments } from './pages/departments/departments';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'home',
    component: MainLayouts,
    children: [
      {
        path: '',
        component: Home,
      },
      {
        path: 'tasks',
        component: Tasks,
      },
      {
        path: 'departments',
        component: Departments,
      },
    ],
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
