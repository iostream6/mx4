<!--  
***  2020.05.16  - Created | Broker list capability
***  2020.05.17  - Added broker edit/delete capability
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
                <i class="fa fa-plus-square"></i>
                &nbsp;&nbsp; Add New
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
                    <td class="text-left">{{brkr.name}}</td>
                    <td>
                      <!-- <a href="#" class="text-success" v-on:click.prevent="broker=brkr; showEditBrokerDialog=true">  Some state issues due to other things -->
                      <!-- <a href="#" class="text-success" v-on:click.prevent="selectedBrokerIndex=index"> -->
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-success" v-on:click.prevent="selectedBrokerIndex=index; showEditBrokerDialogLauncher(false)">
                        <i class="fa fa-edit"></i>
                      </a> &nbsp;| &nbsp;
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-danger" v-on:click.prevent="selectedBrokerIndex=index; showEditBrokerDialogLauncher(true)">
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
      <!-- ADD BROKER MODAL -->
      <div id="overlay" v-if="showAddBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Add New Broker</h5>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="addBroker()">
                <div class="form-group">
                  <input v-model="newBroker.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
                </div>
                <div class="form-group">
                  <button class="btn btn-primary btn-block btn-lg" type="submit">Add</button>
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

      <!-- EDIT BROKER MODAL -->
      <div id="overlay" v-if="showEditBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Edit Broker</h5>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="editBroker(false)">
                <div class="form-group">
                  <input v-model="safeEditBrokerInfo.broker.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
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

      <!-- DELETE BROKER MODAL -->
      <div id="overlay" v-if="showDeleteBrokerDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">Delete Broker</h5>
              <button type="button" class="close" v-on:click="cancelBrokerDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="editBroker(true)">
                <div class="form-group">
                  <input v-model="safeEditBrokerInfo.broker.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
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
import { mapState, mapActions } from "vuex";

export default {
  name: "Brokerage",
  data() {
    return {
      showAddBrokerDialog: false,
      showEditBrokerDialog: false,
      showDeleteBrokerDialog: false,
      newBroker: { name: null },
      safeEditBrokerInfo: { broker: { name: null } },
      selectedBrokerIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "brokers"])
  },
  methods: {
    ...mapActions(["addBrokerAction", "getBrokersAction", "editBrokerAction", "deleteBrokerAction"]),
    //
    //
    //
    cancelBrokerDialog() {
      this.newBroker = { name: null };

      this.showAddBrokerDialog = false;
      this.showEditBrokerDialog = false;
      this.showDeleteBrokerDialog = false;

      this.safeEditBrokerInfo = { broker: { name: null } };
      this.selectedBrokerIndex = -1;
    },
    //
    //
    //
    async addBroker() {
      //e.preventDefault() - already blocked with modifier

      const data = {
        name: this.broker.name,
        userId: this.user.userId
      };
      this.addBrokerAction(data);

      this.cancelBrokerDialog();
    },
    //
    //
    //
    async editBroker(isDelete) {
      if (isDelete) {
        this.deleteBrokerAction(this.safeEditBrokerInfo);
      } else {
        this.editBrokerAction(this.safeEditBrokerInfo);
      }
      this.cancelBrokerDialog();
    },
    //
    //
    //
    showEditBrokerDialogLauncher(isDelete) {
      //make a safe copy of the selected broker - this avoids:
      // a) Messing with the Vuex state directly if we use the currently rendered object (rather than its safe copy)
      // b) Ensure that if a user cancels the edit mid-way, we don't have to worry about restoring a bount object
      const selectedBroker = this.brokers[this.selectedBrokerIndex];
      this.safeEditBrokerInfo = {
        broker: {
          id: selectedBroker.id,
          name: selectedBroker.name,
          userId: selectedBroker.userId
        },
        index: this.selectedBrokerIndex
      };
      if (isDelete) {
        this.showDeleteBrokerDialog = true;
      } else {
        //trigger the modal Edit dialog
        this.showEditBrokerDialog = true;
      }
    }
  },
  created() {
    this.getBrokersAction();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>