<!--  
***  2020.05.16  - Created | Broker list capability
***  2020.05.17  - Added broker edit/delete capability
***  2020.05.26  - Updated Modal forms to correctly use footer
***  2020.05.27  - Updated to use more compact centralized authorization checks
***  2020.05.30  - Use FA 5.0  and simplified/centralised add/edit/delete actions for domain objects
-->
<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row mt-3">
            <div class="col-lg-9">
              <h3 class="text-primary float-left">Brokers</h3>
            </div>
            <div class="col-lg-3">
              <!--<button class="btn btn-primary float-right" v-on:click="showAddBrokerDialog = true"> -->
              <button class="btn btn-primary float-right" v-on:click="showAddBrokerDialog=true">
                <font-awesome-icon :icon="['fas', 'plus-square']" />&nbsp;&nbsp; Add New
              </button>
            </div>
          </div>
          <hr class="bg-primary" />
          <!--<div class="alert alert-danger" v-if="errorMsg">Error message</div>
          <div class="alert alert-success" v-if="successMsg">Sucess message</div>-->
          <!-- List of broker records -->
          <div class="row">
            <div class="col-lg-12">
              <table class="table table-bordered table-striped">
                <colgroup>
                  <col span="1" style="width: 92%;" />
                  <col span="1" style="width: 8%;" />
                </colgroup>
                <thead>
                  <tr class="bg-primary text-light">
                    <th class="text-left">Name</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(brkr, index) in brokers" v-bind:key="index">
                    <!-- <td class="text-left">{{brkr.name}}</td> -->
                    <td class="text-left">{{brkr.name}}</td>
                    <!-- <router-link class="nav-link" to="/main">App</router-link> -->
                    <td>
                      <!-- <a href="#" class="text-success" v-on:click.prevent="broker=brkr; showEditBrokerDialog=true">  Some state issues due to other things -->
                      <!-- <a href="#" class="text-success" v-on:click.prevent="selectedBrokerIndex=index"> -->
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-success" v-on:click.prevent="selectedBrokerIndex=index; showEditBrokerDialogLauncher(false)">
                        <font-awesome-icon :icon="['fas', 'edit']" />
                      </a> &nbsp; &nbsp; &nbsp; &nbsp;
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-danger" v-on:click.prevent="selectedBrokerIndex=index; showEditBrokerDialogLauncher(true)">
                        <font-awesome-icon :icon="['fas', 'trash-alt']" />
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
      <!-- ADD BROKER MODAL -->
      <div id="overlay" v-if="showAddBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Add New Broker</h4>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="addBroker()">
                <div class="form-group">
                  <input v-model="newBroker.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
                </div>
                <!-- We could trigger the event on this directly but we want footer/validation so we show the footer, hide this
                and delegate back to this button (submit) to enable inbuilt validation-->
                <div class="form-group" hidden>
                  <button id="addButton" class="btn btn-primary btn-block btn-lg" type="submit">Add</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <!-- we want to have the nornal footer buttons, the add option in the footer will trigger click
              event in the actual form submit button. Declaring the footer inside the form will avoid this but will
              render and a short hr footer
              -->
              <button type="button" class="btn btn-secondary" v-on:click="cancelBrokerDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('addButton')">Add</button>
            </div>
          </div>
        </div>
      </div>

      <!-- EDIT BROKER MODAL -->
      <div id="overlay" v-if="showEditBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Edit Broker</h4>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="editBroker(false)">
                <div class="form-group">
                  <input v-model="safeEditBrokerInfo.data.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
                </div>
                <!-- We could trigger the event on this directly but we want footer/validation so we show the footer, hide this
                and delegate back to this button (submit) to enable inbuilt validation-->
                <div class="form-group" hidden>
                  <button id="editButton" class="btn btn-primary btn-block btn-lg" type="submit">Edit</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <!-- we want to have the nornal footer buttons, the add option in the footer will trigger click
              event in the actual form submit button. Declaring the footer inside the form will avoid this but will
              render and a short hr footer
              -->
              <button type="button" class="btn btn-secondary" v-on:click="cancelBrokerDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('editButton')">Save</button>
            </div>
          </div>
        </div>
      </div>

      <!-- DELETE BROKER MODAL -->
      <div id="overlay" v-if="showDeleteBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Delete Broker</h4>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <div class="form-group">
                <!--<input v-model="safeEditBrokerInfo.broker.name" class="form-control form-control-lg" readonly required minlength="2" autofocus /> -->
                <div class="alert alert-danger text-left">This action will delete '{{safeEditBrokerInfo.data.name}}'</div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="cancelBrokerDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="editBroker(true)">Delete</button>
            </div>
          </div>
        </div>
      </div>

      <!-- DELETE PRECLUSION MODAL -->
      <div id="overlay" v-if="showDeleteRejectionDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Message</h4>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <div class="form-group">
                <!--<input v-model="safeEditBrokerInfo.broker.name" class="form-control form-control-lg" readonly required minlength="2" autofocus /> -->
                <div class="alert alert-warning text-left">This action cannot be completed because there is at least one active portfolio (e.g. {{safeEditBrokerInfo.activePortfolioName}}) associated with this broker.</div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-primary" v-on:click="cancelBrokerDialog">Cancel</button>
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
  name: "Brokerages",
  data() {
    return {
      showAddBrokerDialog: false,
      showEditBrokerDialog: false,
      showDeleteBrokerDialog: false,
      showDeleteRejectionDialog: false,
      newBroker: { name: null },
      safeEditBrokerInfo: { data: { name: null } },
      selectedBrokerIndex: -1,

      showAddPortfolioDialog: false,
      selectedPortfolioIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "brokers", "authenticated", "portfolios"])
  },
  methods: {
    ...mapActions(["addAction", "editAction", "deleteAction", "ensureAuthorized"]),
    //
    //
    //
    cancelBrokerDialog() {
      this.newBroker = { name: null };

      this.showAddBrokerDialog = false;
      this.showEditBrokerDialog = false;
      this.showDeleteBrokerDialog = false;
      (this.showDeleteRejectionDialog = false), (this.safeEditBrokerInfo = { data: { name: null } });
      this.selectedBrokerIndex = -1;
    },
    triggerClickEvent(targetId) {
      document.getElementById(targetId).click();
    },
    //
    //
    //
    async addBroker() {
      //e.preventDefault() - already blocked with modifier
      await this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        const requestInfo = {
          data: {
            name: this.newBroker.name,
            userId: this.user.userId
          },
          url: `/api/brokers`,
          list: "brokers"
        };

        this.addAction(requestInfo);
        this.cancelBrokerDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    //
    //
    //
    async editBroker(isDelete) {
      // //e.preventDefault() - already blocked with modifier
      await this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        if (isDelete) {
          this.safeEditBrokerInfo.url = `/api/broker/${this.user.userId}/${this.safeEditBrokerInfo.data.id}`;
          this.deleteAction(this.safeEditBrokerInfo);
        } else {
          this.safeEditBrokerInfo.url = `/api/broker/${this.user.userId}`;
          this.editAction(this.safeEditBrokerInfo);
        }
        this.cancelBrokerDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    //
    //
    //
    showEditBrokerDialogLauncher(isDelete) {
      //make a safe copy of the selected broker - this avoids:
      // a) Messing with the Vuex state directly if we use the currently rendered object (rather than its safe copy)
      // b) Ensure that if a user cancels the edit mid-way, we don't have to worry about restoring a bound object
      const selectedBroker = this.brokers[this.selectedBrokerIndex];
      this.safeEditBrokerInfo = {
        data: {
          id: selectedBroker.id,
          name: selectedBroker.name,
          userId: selectedBroker.userId
        },
        index: this.selectedBrokerIndex,
        length: 1,
        //url: set later
        list: "brokers"
      };
      // is delete allowed?
      //
      let associatedPortfolio = this.portfolios.find(item => item.brokerId == selectedBroker.id);

      if (isDelete) {
        if (associatedPortfolio) {
          this.safeEditBrokerInfo.activePortfolioName = associatedPortfolio.name;
          this.showDeleteRejectionDialog = true;
        } else {
          this.showDeleteBrokerDialog = true;
        }
      } else {
        this.showEditBrokerDialog = true;
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>