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
                  <a class="nav-link" href="#" v-on:click="activeComponent='brokerage'">Brokerages</a>
                  <a class="nav-link" href="layout-sidenav-light.html">Transactions</a>
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
              <div class="collapse" id="collapsePages" aria-labelledby="headingTwo" data-parent="#sidenavAccordion">
                <nav class="sb-sidenav-menu-nested nav accordion" id="sidenavAccordionPages">
                  <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#pagesCollapseAuth" aria-expanded="false" aria-controls="pagesCollapseAuth">
                    Users
                    <div class="sb-sidenav-collapse-arrow">
                      <i class="fa fa-angle-down"></i>
                    </div>
                  </a>
                  <div class="collapse" id="pagesCollapseAuth" aria-labelledby="headingOne" data-parent="#sidenavAccordionPages">
                    <nav class="sb-sidenav-menu-nested nav">
                      <a class="nav-link" href="login.html">Login</a>
                      <a class="nav-link" href="register.html">Register</a>
                      <a class="nav-link" href="password.html">Forgot Password</a>
                    </nav>
                  </div>
                  <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#pagesCollapseError" aria-expanded="false" aria-controls="pagesCollapseError">
                    Error
                    <div class="sb-sidenav-collapse-arrow">
                      <i class="fa fa-angle-down"></i>
                    </div>
                  </a>
                  <div class="collapse" id="pagesCollapseError" aria-labelledby="headingOne" data-parent="#sidenavAccordionPages">
                    <nav class="sb-sidenav-menu-nested nav">
                      <a class="nav-link" href="401.html">401 Page</a>
                      <a class="nav-link" href="404.html">404 Page</a>
                      <a class="nav-link" href="500.html">500 Page</a>
                    </nav>
                  </div>
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
      <dashboard v-show="activeComponent==='dashboard'"/>


      <!-- Main area content for Brokerage link -->
      <brokerage v-if="activeComponent==='brokerage'"/>
      
    </div>
  </div>
</template>

<script>
// import { mapState } from "vuex";

import AppNavBar from "../components/AppNavBar";
import Dashboard from "../components/Dashboard";
import Brokerage from "../components/Brokerage";

export default {
  name: "MainView",
  components: { AppNavBar, Dashboard, Brokerage},
  data() {
    return { activeComponent: "dashboard" };
  },
  computed: {
    //...mapState(["httpErrorInfo"])
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>