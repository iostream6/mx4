<template>
  <div id="layoutError">
    <div id="layoutError_content">
      <main>
        <div class="container">
          <div class="row justify-content-center">
            <div class="col-lg-6">
              <div class="text-center mt-4">
                <h1 class="display-1">{{httpErrorInfo.code}}</h1>
                <p class="lead">{{httpErrorInfo.message}}</p>
                <router-link to="/dashboard" class="btn btn-sm bg-primary text-white">
                  <i class="fa fa-arrow-left mr-1"></i>Return to Dashboard
                </router-link>
                
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <div id="layoutError_footer">
      <footer class="py-4 bg-light mt-auto">
        <div class="container-fluid">
          <div class="d-flex align-items-center justify-content-between small">
            <div class="text-muted">Copyright &copy; Alamander Foundary</div>
            <div>
              <a href="#">Privacy Policy</a>
              &middot;
              <a href="#">Terms &amp; Conditions</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
//import { mapState } from "vuex";

export default {
  name: "Error",
  computed: {
    //...mapState(["httpErrorInfo"])
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#layoutError {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
#layoutError #layoutError_content {
  min-width: 0;
  flex-grow: 1;
}
#layoutError #layoutError_footer {
  min-width: 0;
}
</style>
