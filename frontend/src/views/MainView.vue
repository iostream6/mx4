<template>
  <div class="sb-nav-fixed">
    <app-nav-bar />
    <div id="layoutSidenav">
      <!-- The left side nav pane -->
      <div id="layoutSidenav_nav">
        <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
          <div class="sb-sidenav-menu">
            <div class="nav">
              <div class="sb-sidenav-menu-heading">Core</div>
              <a class="nav-link" href="#" v-on:click="activeComponent='dashboard'">
                <div class="sb-nav-link-icon">
                  <i class="fa fa-tachometer"></i>
                </div>Dashboard
              </a>

              <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseLayouts" aria-expanded="false" aria-controls="collapseLayouts">
                <div class="sb-nav-link-icon">
                  <i class="fa fa-columns"></i>
                </div>Management
                <div class="sb-sidenav-collapse-arrow">
                  <i class="fa fa-angle-down"></i>
                </div>
              </a>
              <div class="collapse" id="collapseLayouts" aria-labelledby="headingOne" data-parent="#sidenavAccordion">
                <nav class="sb-sidenav-menu-nested nav">
                  <a class="nav-link" href="#" v-on:click="activeComponent='brokerages'">Brokerages</a>
                  <a class="nav-link" href="#" v-on:click="activeComponent='portfolios'">Portfolios</a>
                  <a class="nav-link" href="#" v-on:click="activeComponent='transactions'">Transactions</a>
                </nav>
              </div>
              <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePages" aria-expanded="false" aria-controls="collapsePages">
                <div class="sb-nav-link-icon">
                  <i class="fa fa-book-open"></i>
                </div>Admin
                <div class="sb-sidenav-collapse-arrow">
                  <i class="fa fa-angle-down"></i>
                </div>
              </a>
              <div v-if="isAdminUser" class="collapse" id="collapsePages" aria-labelledby="headingOne" data-parent="#sidenavAccordion">
                <nav class="sb-sidenav-menu-nested nav">
                  <a class="nav-link" href="#" v-on:click="activeComponent='entities'">Entities</a>
                </nav>
              </div>
              <div class="sb-sidenav-menu-heading">Addons</div>
              <a class="nav-link" href="charts.html">
                <div class="sb-nav-link-icon">
                  <i class="fa fa-chart-area"></i>
                </div>Charts
              </a>
              <a class="nav-link" href="tables.html">
                <div class="sb-nav-link-icon">
                  <i class="fa fa-table"></i>
                </div>Tables
              </a>
            </div>
          </div>
        </nav>
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