import Vue from 'vue'
import VueRouter from 'vue-router'
import LandingView from '../views/LandingView.vue'
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
    path: '/main',
    name: 'MainView',
    component: MainView,
    beforeEnter(to, from, next){
      if(dataStore.getters.isJWTValid(new Date())){
        next();
      }else{
        next("/");
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
