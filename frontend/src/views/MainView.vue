<!--  
***  2020.05.30  - Use FA 5.0
***  2020.05.31  - Updated to use bootstrap-vue
-->

<template>
  <div>
    <app-nav-bar />
    <div id="layoutSidenav">
      <!-- The left side nav pane -->
      <div id="layoutSidenav_nav">
        <b-nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
          <div class="sb-sidenav-menu">
            <div class="nav">
              <b-link class="nav-link" href="#" v-on:click="activeComponent='dashboard'">
                <div class="sb-nav-link-icon">
                  <font-awesome-icon :icon="['fas', 'tachometer-alt']" />
                </div>Dashboard
              </b-link>
              <!-- Mgmt Menu -->
              <b-link class="nav-link" href="#" v-b-toggle.accordion-mgmt>
                <div class="sb-nav-link-icon">
                  <font-awesome-icon :icon="['fas', 'columns']" />
                </div>Management
                <div class="sb-sidenav-collapse-arrow">
                  <font-awesome-icon :icon="['fas', 'angle-down']" />
                </div>
              </b-link>
              <!-- -->
              <div>
                <b-nav class="sb-sidenav-menu-nested nav">
                  <b-collapse id="accordion-mgmt" visible accordion="sidenavAccordion" role="tabpanel">
                    <b-link class="nav-link" href="#" v-on:click="activeComponent='brokerages'">Brokerages</b-link>
                    <b-link class="nav-link" href="#" v-on:click="activeComponent='portfolios'">Portfolios</b-link>
                    <b-link class="nav-link" href="#" v-on:click="activeComponent='transactions'">Transactions</b-link>
                  </b-collapse>
                </b-nav>
              </div>
              <!-- end MGMT menu -->
              <!-- Mgmt Menu -->
              <b-link class="nav-link" href="#" v-b-toggle.accordion-admin v-if="isAdminUser"> 
                <div class="sb-nav-link-icon">
                  <font-awesome-icon :icon="['fas', 'book-open']" />
                </div>Admin
                <div class="sb-sidenav-collapse-arrow">
                  <font-awesome-icon :icon="['fas', 'angle-down']" />
                </div>
              </b-link>
              <!-- -->
              <div v-if="isAdminUser">
                <b-nav class="sb-sidenav-menu-nested nav">
                  <b-collapse id="accordion-admin" accordion="sidenavAccordion" role="tabpanel">
                    <b-link class="nav-link" href="#" v-on:click="activeComponent='entities'">Entities</b-link>
                  </b-collapse>
                </b-nav>
              </div>
              <!-- end MGMT menu -->
            </div>
          </div>
        </b-nav>
      </div>

      <!-- THE MAIN AREA CONTENT PLACEHOLDERS - THE CONTENT THAT IS SHOWN DEPENDS ON THE ACTIVE link (captured by The main area content  place holders -->
      <!-- Main area content for Dashboard link -->
      <dashboard v-show="activeComponent==='dashboard'" />

      <!-- Main area content for Brokerage link -->
      <brokerages v-if="activeComponent==='brokerages'" />

      <!-- Main area content for Portfolio link -->
      <portfolios v-if="activeComponent==='portfolios'" />

      <!-- Main area content for Transactions link -->
      <transactions v-if="activeComponent==='transactions'" />

      <!-- Main area content for Entities link -->
      <entities v-if="activeComponent==='entities'" />
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

import AppNavBar from "../components/AppNavBar";
import Dashboard from "../components/Dashboard";
import Brokerages from "../components/Brokerages";
import Portfolios from "../components/Portfolios";
import Transactions from "../components/Transactions";
import Entities from "../components/Entities";

export default {
  name: "MainView",
  components: {
    AppNavBar,
    Dashboard,
    Brokerages,
    Portfolios,
    Transactions,
    Entities
  },
  data() {
    return { activeComponent: "dashboard" };
  },
  computed: {
    ...mapState(["isAdminUser"])
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>