<!--  
***  2020.05.20  - Created 
***  2020.05.27  - Updated to use more compact centralized authorization checks
***  2020.05.28  - Improved implementation (based on Entities.vue), removing need for extra  attribute mapping fields
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
                    <td class="text-left">{{brokers.find(item => item.id == ptfl.brokerId).name}}</td>
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
              <h4 class="modal-title" id="exampleModalLabel">Add New Portfolio</h4>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="add()">
                <div class="form-group text-left">
                  <label for="inputPortfolioCode">Portfolio code:</label>
                  <input id="inputPortfolioCode" v-model="newPortfolio.code" class="form-control form-control-lg" required minlength="2" autofocus />
                </div>
                <div class="form-group text-left">
                  <label for="inputPortfolioName">Portfolio name:</label>
                  <input id="inputPortfolioName" v-model="newPortfolio.name" class="form-control form-control-lg" required minlength="2" autofocus />
                </div>
                <div class="form-group text-left">
                  <label for="inputPortfolioBroker">Broker:</label>
                  <select id="inputPortfolioBroker" class="form-control" v-model="newPortfolio.brokerIndex" required>
                    <option v-for="(broker, index) in brokers" v-bind:key="index" v-bind:value="index">{{broker.name}}</option>
                  </select>
                </div>
                <div class="form-group" hidden>
                  <button id="portfolioAddButton" class="btn btn-primary btn-block btn-lg" type="submit">Add</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('portfolioAddButton')">Save</button>
            </div>
          </div>
        </div>
      </div>

      <!-- EDIT PORTFOLIO MODAL -->
      <div id="overlay" v-if="showEditDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Edit Portfolio</h4>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="edit(false)">
                <div class="form-group text-left">
                  <label for="inputPortfolioCode">Portfolio code:</label>
                  <input id="inputPortfolioCode" v-model="safeEditInfo.portfolio.code" class="form-control form-control-lg" required minlength="2" autofocus />
                </div>
                <div class="form-group text-left">
                  <label for="inputPortfolioName">Portfolio name:</label>
                  <input id="inputPortfolioName" v-model="safeEditInfo.portfolio.name" class="form-control form-control-lg" required minlength="2" autofocus />
                </div>
                <div class="form-group text-left">
                  <label for="inputPortfolioBroker">Broker:</label>
                  <select id="inputPortfolioBroker" class="form-control" v-model="safeEditInfo.brokerIndex" required>
                    <option v-for="(broker, index) in brokers" v-bind:key="index" v-bind:value="index">{{broker.name}}</option>
                  </select>
                </div>
                <div class="form-group" hidden>
                  <button id="portfolioEditButton" class="btn btn-primary btn-block btn-lg" type="submit">Save</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('portfolioEditButton')">Save</button>
            </div>
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
                  <div class="alert alert-danger text-left">This action will delete '{{safeEditInfo.portfolio.name}}'</div>
                </div>
                <div class="form-group" hidden>
                  <button id="portfolioDeleteButton" class="btn btn-primary btn-block btn-lg" type="submit">Save</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('portfolioDeleteButton')">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "Portfolios",
  data() {
    return {
      newPortfolio: { name: null, brokerIndex: null },
      safeEditInfo: { brokerIndex: null },

      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      selectedIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "brokers", "portfolios"])
  },
  methods: {
    //
    //
    //
    triggerClickEvent(targetId) {
      document.getElementById(targetId).click();
    },
    //
    //
    //
    ...mapActions({
      refreshTokenAction: "refreshTokenAction",
      addAction: "addPortfolioAction",
      editAction: "editPortfolioAction",
      deleteAction: "deletePortfolioAction",
      ensureAuthorized: "ensureAuthorized"
    }),
    //
    //
    //
    cancelDialog() {
      this.newPortfolio = { name: null, brokerIndex: null };

      this.showAddDialog = false;
      this.showEditDialog = false;
      this.showDeleteDialog = false;

      this.safeEditInfo = { brokerIndex: null };
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
          code: selectedPortfolio.code
          //
          //
          //will be populated layer, after UI closed
          // brokerId: selectedPortfolio.brokerId,
        },
        brokerIndex: this.brokers.findIndex(item => item.id == selectedPortfolio.brokerId),
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

      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        const data = {
          name: this.newPortfolio.name,
          code: this.newPortfolio.code,
          brokerId: this.brokers[this.newPortfolio.brokerIndex].id
        };
        this.addAction(data);
        this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    //
    //
    //
    async edit(isDelete) {
      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        //map ui selections to persistence model attributes
        this.safeEditInfo.portfolio.brokerId = this.brokers[this.safeEditInfo.brokerIndex].id;
        if (isDelete) {
          this.deleteAction(this.safeEditInfo);
        } else {
          this.editAction(this.safeEditInfo);
        }
        this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    }
  }
  // created() {

  // }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>