<template>
  <div>
    <div>
      <app-nav-bar />
    </div>
    <div v-if="authenticated">
      <landing />
    </div>
    <div v-else>
      <signin/>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import AppNavBar from "../components/AppNavBar";
import Landing from "@/components/Landing.vue";
import Signin from "@/components/Signin.vue";

import { mapState } from "vuex";

export default {
  name: "LandingView",
  components: { AppNavBar, Landing, Signin
  },
  computed: {
        // isJWTValid returns a function so even though computed, it is not cached
    ...mapState(["authenticated"])
  }
};
</script>
