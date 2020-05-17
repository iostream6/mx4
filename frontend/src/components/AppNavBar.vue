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
            <li class="nav-item" v-if="user.username">
              <router-link class="nav-link" to="/main">App</router-link>
            </li>
            <li class="nav-item dropdown" v-if="user.username">
              <a class="nav-link dropdown-toggle" id="userDropdown" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fa fa-user fa-fw"></i>
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
import { mapState, mapGetters, mapMutations } from "vuex";

export default {
  name: "AppNavBar",
  computed: {
    ...mapState(["server", "user", "jwt"]),
    ...mapGetters(["getAuthenticatedAxios", "isAuthenticated"])
  },
  props: {},
  methods: {
    ...mapMutations(["clearAuthentication"]),
    async handleSignout(e) {
      e.preventDefault();
      if (this.isAuthenticated) {
        //prepare the validated data
        let signoutData = {
          token: this.jwt
        };
        try {
          const axiosResponse = await this.getAuthenticatedAxios.post(
            `${this.server}/signout`,
            signoutData
          );
          if (axiosResponse.data["success"]) {
            this.clearAuthentication();
            this.$router.push("/");
          }
        } catch (error) {
          //     //todo forward to page!!!
          //     console("Response !!!");
          //     console.error(
        }
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>