<template>
  <div>
    <div>
      <h3>Places</h3>
      <ul>
        <li v-if="user.username==null">
          <router-link to="/signin" >Login</router-link>|
        </li>
        <li>
          <router-link to="/error">Error</router-link>|
        </li>
      </ul>
            <p>Home page here</p>
    </div>
  </div>
</template>

<script>

import { mapState } from "vuex";

export default {
  name: "Home",
  computed: {
    ...mapState(["user", "jwt"])
  },
  props: {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
