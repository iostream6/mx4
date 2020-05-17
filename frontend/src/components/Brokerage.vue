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
                      <a href="#" class="text-success" v-on:click.prevent="selectedBrokerIndex=index">
                        <i class="fa fa-edit"></i>
                      </a> &nbsp; &nbsp;
                      <a href="#" class="text-danger">
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
              <form v-on:submit.prevent="addBroker">
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
              <form v-on:submit.prevent="editBroker">
                <div class="form-group">
                  <input v-model="brokerToEdit.name" class="form-control form-control-lg" placeholder="Broker name" required minlength="2" autofocus />
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
      brokerToEdit: { name: null },
      selectedBrokerIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "brokers"])
  },
  methods: {
    ...mapActions(["addBrokerAction", "getBrokersAction", "editBrokerAction"]),
    cancelBrokerDialog() {
      this.newBroker = { name: null };
      this.showAddBrokerDialog = false;

      this.showEditBrokerDialog = false;
      this.brokerToEdit = { name: null };
      this.selectedBrokerIndex = -1;
    },
    async addBroker() {
      //e.preventDefault() - already blocked with modifier

      const data = {
        name: this.broker.name,
        userId: this.user.userId
      };
      this.addBrokerAction(data);

      this.cancelBrokerDialog();
    },
    async editBroker() {
      //create a safe copy of the selected broker (avoid screwing with vuex state and also in case the user cancels)
      this.brokerToEdit = { name: this.brokers[this.selectedBrokerIndex].name };
      console.log("Yah");
      console.log(this.brokers[this.selectedBrokerIndex]);

      this.showEditBrokerDialog=true

      //this.editBrokerAction(this.broker);

      //this.cancelBrokerDialog();
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