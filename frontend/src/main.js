/**
 * 2020.05.30  - Upgradded to font-awesome 5.0
 */

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import 'bootstrap'; // brings in the JS bits
import 'bootstrap/dist/css/bootstrap.min.css'

// this works for v-4 with npm install font-awesome
//import 'font-awesome/css/font-awesome.min.css'



///////////////////////////////////// FA - V5 config
// this is for v5 per the guidance @
//     https://github.com/FortAwesome/vue-fontawesome (read all)
//     https://stackoverflow.com/a/50053488
//     https://stackoverflow.com/a/60556436
//
// npm install  @fortawesome/fontawesome-svg-core
// npm install  @fortawesome/free-solid-svg-icons
// npm install  @fortawesome/vue-fontawesome
//
//

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'

//A. import any desirable "solid style" icon (they are free in v5)
import { faSignInAlt, faUserPlus, faAngleLeft, faAngleDown, faTachometerAlt, faChartArea, faTable,
  faColumns, faBookOpen, faPlusSquare, faEdit,faTrashAlt, faCalendarAlt, faBriefcase, faHryvnia, faCoins,
  faChess, faChessPawn, faFont /* for actions table column */, 
  /* transaction types */ faHandHoldingMedical, faHandHoldingUsd, faHandHoldingWater } from '@fortawesome/free-solid-svg-icons'
//alternatively, import all solid icons
//import solid from "@fortawesome/fontawesome-free-solid";

//B. import any desirable "brands style" icon (they are free in v5)
//import { faFontAwesome } from '@fortawesome/free-brands-svg-icons'
//alternatively, import all brands icons
//import solid from "@fortawesome/fontawesome-free-brands";

//C. Note that in addition to the above, there are a limited number of FREE Regular icons available
// to import this, would (1) require npm install  @fortawesome/free-regular-svg-icons AND 
// (2) the required regular style icon import


// add the imported icons to the library
library.add(faSignInAlt, faUserPlus, faAngleLeft, faAngleDown, faTachometerAlt, faChartArea, faTable,
  faColumns, faBookOpen, faPlusSquare, faEdit,faTrashAlt, faCalendarAlt, faBriefcase, faHryvnia, faCoins,
  faChess, faChessPawn, faFont /* for actions table column */,
  /* transaction types */ faHandHoldingMedical, faHandHoldingUsd, faHandHoldingWater)
// you can also do library.add(solid) if you imported the entire style icons set above

Vue.component('font-awesome-icon', FontAwesomeIcon)

//////////////////////////////////// END FA - V5 config


Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
