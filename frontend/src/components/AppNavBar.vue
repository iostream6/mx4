<!--  
***  2020.05.31  - Use FA 5.0  and centralised logout action
-->

<template>
  <div>
    <!-- Navigation -->
    <nav class="sb-topnav navbar navbar-expand-lg navbar-dark bg-primary static-top">
      <div class="container">
        <a class="navbar-brand" href="#">Start Bootstrap</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
          <ul class="navbar-nav ml-auto">
            <li class="nav-item active">
              <router-link class="nav-link" to="/">
                Home
                <span class="sr-only">(current)</span>
              </router-link>
            </li>
            <li class="nav-item" v-if="authenticated">
              <router-link class="nav-link" to="/main">App</router-link>
            </li>
            <li class="nav-item dropdown" v-if="authenticated">
              <a class="nav-link dropdown-toggle" id="userDropdown" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <font-awesome-icon :icon="['fas', 'user']" />
              </a>
              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                <a class="dropdown-item" href="#">Settings</a>
                <a class="dropdown-item" href="#">Activity Log</a>
                <div class="dropdown-divider"></div>
                <a class="dropdown-item" href="signout" v-on:click="handleSignout">Logout</a>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "AppNavBar",
  computed: {
    ...mapState(["server", "user", "authenticated", "jwt"])
  },
  props: {},
  methods: {
    ...mapActions(["ensureAuthorized", "signoutAction"]),
    async handleSignout(e) {
      e.preventDefault();
      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        this.signoutAction().then(result => {
          if (result == true) {
            this.$router.push("/");
          }
        });
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>