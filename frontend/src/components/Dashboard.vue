<!--  
***  2020.06.05 Now uses centralized functions for enforcing/restoring authourization
-->

<template>
  <div id="layoutSidenav_content">
    <main>
      <div class="container-fluid">
        <ol class="breadcrumb mt-4 mb-4">
          <li class="breadcrumb-item active">Dashboard</li>
        </ol>
        <!-- The card row -->
        <div class="row">
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary text-white mb-4">
              <div class="card-body">Primary Card</div>
              <div class="card-footer d-flex align-items-center justify-content-between">
                <a class="small text-white stretched-link" href="#">View Details</a>
                <div class="small text-white">
                  <i class="fas fa-angle-right"></i>
                </div>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-warning text-white mb-4">
              <div class="card-body">Warning Card</div>
              <div class="card-footer d-flex align-items-center justify-content-between">
                <a class="small text-white stretched-link" href="#">View Details</a>
                <div class="small text-white">
                  <i class="fas fa-angle-right"></i>
                </div>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-success text-white mb-4">
              <div class="card-body">Success Card</div>
              <div class="card-footer d-flex align-items-center justify-content-between">
                <a class="small text-white stretched-link" href="#">View Details</a>
                <div class="small text-white">
                  <i class="fas fa-angle-right"></i>
                </div>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-danger text-white mb-4">
              <div class="card-body">Danger Card</div>
              <div class="card-footer d-flex align-items-center justify-content-between">
                <a class="small text-white stretched-link" href="#">View Details</a>
                <div class="small text-white">
                  <i class="fas fa-angle-right"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "Dashboard",
  computed: {
    ...mapState(["isBasicDataGotten", "authenticated"])
  },
  methods: {
    ...mapActions(["getBasicDataAction", "ensureAuthorized"]),
  },
  mounted() {
    if (!this.isBasicDataGotten) {
      const thisInstanceReference = this;
      //   //will updated authenticated state
      this.ensureAuthorized().then(function() {
        if (thisInstanceReference.authenticated == true) {
          thisInstanceReference.getBasicDataAction();
        } else {
          thisInstanceReference.$router.push("/");
        }
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>