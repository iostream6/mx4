import Vue from 'vue'
import VueRouter from 'vue-router'
import LandingView from '../views/LandingView.vue'
import SigninView from '../views/SigninView.vue'
import MainView from '../views/MainView.vue'
//
import ErrorView from '../views/ErrorView.vue'

import dataStore from '../store';

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'LandingView',
    component: LandingView
  },
  {
    path: '/signin',
    name: 'SigninView',
    component: SigninView
  },
  {
    path: '/main',
    name: 'MainView',
    component: MainView,
    beforeEnter(to, from, next){
      if(dataStore.getters.isAuthenticated){
        next();
      }else{
        next("/signin");
      }
    }

  },
  {
    path: '/error',
    name: 'ErrorView',
    component: ErrorView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
