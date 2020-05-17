import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import 'bootstrap'; // brings in the JS bits
import 'bootstrap/dist/css/bootstrap.min.css'

// this appears to work for v-4
import 'font-awesome/css/font-awesome.min.css'

// the is is poor way for v5 with npm install @fortawesome/fontawesome-free
// import '@fortawesome/fontawesome-free/css/all.css'
// import '@fortawesome/fontawesome-free/js/all.js'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
