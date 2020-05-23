<!--  
***  2020.05.20  - Created 
***  
-->
<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row"></div>
          <div class="row mt-3">
            <div class="col-lg-9">
              <h3 class="text-primary float-left">Portfolios</h3>
            </div>
            <div class="col-lg-3">
              <button class="btn btn-primary float-right" v-on:click="showAddDialog=true">
                <i class="fa fa-plus-square"></i>
                &nbsp;&nbsp; Add New
              </button>
            </div>
          </div>
          <hr class="bg-primary" />
          <div class="row">
            <div class="col-lg-12">
              <table class="table table-bordered table-striped">
                <colgroup>
                  <col span="1" style="width: 12%;" />
                  <col span="1" style="width: 40%;" />
                  <col span="1" style="width: 40%;" />
                  <col span="1" style="width: 8%;" />
                </colgroup>
                <thead>
                  <tr class="bg-primary text-light">
                    <th class="text-left">Code</th>
                    <th class="text-left">Name</th>
                    <th class="text-left">Broker</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(ptfl, index) in portfolios" v-bind:key="index">
                    <td class="text-left">{{ptfl.code}}</td>
                    <td class="text-left">{{ptfl.name}}</td>
                    <td class="text-left">{{ptfl.brokerName}}</td>
                    <td>
                      <a href="#" class="text-success" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(false)">
                        <i class="fa fa-edit"></i>
                      </a> &nbsp;| &nbsp;
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-danger" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(true)">
                        <i class="fa fa-trash"></i>
                      </a>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>

    <div>
      <!-- Modals see https://getbootstrap.com/docs/4.0/components/modal/-->
      <!-- ADD PORTFOLIO MODAL -->
      <div id="overlay" v-if="showAddDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Add New Portfolio</h5>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="add()">
                <div class="form-group">
                  <input v-model="newPortfolio.code" class="form-control form-control-lg" placeholder="Portfolio code" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <input v-model="newPortfolio.name" class="form-control form-control-lg" placeholder="Portfolio name" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <select class="custom-select" v-model="newPortfolio.index" required>
                    <option value>Select broker</option>
                    <option v-for="(broker, index) in brokers" v-bind:key="index" v-bind:value="index">{{broker.name}}</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary btn-block btn-lg" type="submit">Add</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <!-- EDIT PORTFOLIO MODAL -->
      <div id="overlay" v-if="showEditDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Edit Portfolio</h5>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="edit(false)">
                <div class="form-group">
                  <input v-model="safeEditInfo.portfolio.code" class="form-control form-control-lg" placeholder="Portfolio code" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <input v-model="safeEditInfo.portfolio.name" class="form-control form-control-lg" placeholder="Portfolio name" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <button class="btn btn-primary btn-block btn-lg" type="submit">Save</button>
                </div>
              </form>
            </div>
            <!--<div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="showAddBrokerDialog= false">Close</button>
              <button type="button" class="btn btn-primary">Save changes</button>
            </div>-->
          </div>
        </div>
      </div>

      <!-- DELETE PORTFOLIO MODAL -->
      <div id="overlay" v-if="showDeleteDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Delete Portfolio</h5>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="edit(true)">
                <div class="form-group">
                  <input v-model="safeEditInfo.portfolio.name" class="form-control form-control-lg" placeholder="Portfolio name" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <button class="btn btn-primary btn-block btn-lg" type="submit">Save</button>
                </div>
              </form>
            </div>
            <!--<div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="showAddBrokerDialog= false">Close</button>
              <button type="button" class="btn btn-primary">Save changes</button>
            </div>-->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions, mapGetters, mapMutations } from "vuex";

export default {
  name: "Portfolios",
  data() {
    return {
      newPortfolio: { name: null, index: "" },
      safeEditInfo: { portfolio: { name: null } },

      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      selectedIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "brokers", "portfolios"]),
    // isJWTValid returns a function so even though computed, it is not cached
    ...mapGetters(["isJWTValid"])
  },
  methods: {
    ...mapActions({
      refreshTokenAction: "refreshTokenAction",
      addAction: "addPortfolioAction",
      editAction: "editPortfolioAction",
      deleteAction: "deletePortfolioAction"
    }),
    ...mapMutations(["clearAuthentication"]),
    //
    //
    //
    cancelDialog() {
      this.newPortfolio = { name: null, index: null };

      this.showAddDialog = false;
      this.showEditDialog = false;
      this.showDeleteDialog = false;

      this.safeEditInfo = { portfolio: { name: null } };
      this.selectedIndex = -1;
    },
    //
    //
    //
    showEditDialogLauncher(isDelete) {
      //make a safe copy of the selected portfolio - this avoids:
      // a) Messing with the Vuex state directly if we use the currently rendered object (rather than its safe copy)
      // b) Ensure that if a user cancels the edit mid-way, we don't have to worry about restoring a bound object
      const selectedPortfolio = this.portfolios[this.selectedIndex];
      this.safeEditInfo = {
        portfolio: {
          id: selectedPortfolio.id,
          name: selectedPortfolio.name,
          code: selectedPortfolio.code,
          brokerId: selectedPortfolio.brokerId,
          brokerName: selectedPortfolio.brokerName
        },
        index: this.selectedIndex
      };
      if (isDelete) {
        this.showDeleteDialog = true;
      } else {
        this.showEditDialog = true;
      }
    },
    //
    //
    //
    async add() {
      //e.preventDefault() - already blocked with modifier
      if (!this.isJWTValid(new Date())) {
        this.refreshTokenAction();
        //if after refresh attempt, still not valid? Force signin
        if (!this.isJWTValid(new Date())) {
          this.cancelDialog();
          this.clearAuthentication();
          this.$router.push("/");
          return;
        }
      }

      const data = {
        name: this.newPortfolio.name,
        code: this.newPortfolio.code,
        brokerId: this.brokers[this.newPortfolio.index].id,
        brokerName: this.brokers[this.newPortfolio.index].name
      };
      console.log(data);
      this.addAction(data);
      this.cancelDialog();
    },
    //
    //
    //
    async edit(isDelete) {
      if (isDelete) {
        this.deleteAction(this.safeEditInfo);
      } else {
        this.editAction(this.safeEditInfo);
      }
      this.cancelDialog();
    }
  }
  // created() {

  // }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>