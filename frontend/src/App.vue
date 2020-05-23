<template>
  <div id="app">
    <router-view />
  </div>
</template>


<script>
import { mapActions } from "vuex";

export default {
  methods: {
    ...mapActions(["reloadStateAction"])
  },
  created() {
    //console.log("App Created ^ Reloading state");
    this.reloadStateAction();
  }

};
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
