<!--  
***  2020.05.31  - Use FA 5.0  and centralised logout action
***  2020.06.04  - Switched to bootstrap-vue based navbar
-->

<template>
  <div>
    <b-navbar toggleable="lg" type="dark" variant="primary">
      <b-navbar-toggle target="nav-collapse-group"></b-navbar-toggle>
      <b-navbar-brand tag="h1">BootstrapMX4</b-navbar-brand>
      <b-collapse id="nav-collapse-group" is-nav>
        <b-navbar-nav class="ml-auto">
          <!-- class ensures right aligned nav items -->
          <b-nav-item to="/">Home</b-nav-item>
          <b-nav-item v-if="authenticated" to="/main">App</b-nav-item>
          <b-nav-item-dropdown v-if="authenticated" right>
            <template v-slot:button-content>
             <font-awesome-icon :icon="['fas', 'user']" />
            </template>
            <b-dropdown-item href="#">Settings</b-dropdown-item>
            <b-dropdown-item href="#">Activity Log</b-dropdown-item>
            <b-dropdown-divider></b-dropdown-divider>
            <b-dropdown-item href="#" v-on:click.prevent="handleSignout">Logout</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "AppNavBar",
  computed: {
    ...mapState(["user", "authenticated"])
  },
  props: {},
  methods: {
    ...mapActions(["ensureAuthorized", "signoutAction"]),
    async handleSignout() {
      await this.ensureAuthorized(); //will updated authenticated state
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