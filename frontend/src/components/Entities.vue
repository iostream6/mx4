<!--  
***  2020.05.24  - Created 
***  2020.05.27  - Implemented 
-->
<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row"></div>
          <div class="row mt-3">
            <div class="col-lg-9">
              <h3 class="text-primary float-left">Instruments</h3>
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
                  <col span="1" style="width: 5%;" />
                  <col span="1" style="width: 50%;" />
                  <col span="1" style="width: 8%;" />
                  <col span="1" style="width: 6%;" />
                  <col span="1" style="width: 17%;" />
                  <col span="1" style="width: 6%;" />
                </colgroup>
                <thead>
                  <tr class="bg-primary text-light">
                    <th class="text-center">Code</th>
                    <th class="text-left">Description</th>
                    <th class="text-center">Type</th>
                    <th class="text-center">Currency</th>
                    <th class="text-center">Sector</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(i, index) in supportedInstruments" v-bind:key="index">
                    <td>{{i.code}}</td>
                    <td class="text-left">{{i.description}}</td>
                    <td>{{i.type}}</td>
                    <td>{{currencies[currencies.findIndex(item => item.id == i.currencyId)].code}}</td>
                    <td class="text-left">{{sectors[sectors.findIndex(item => item.id == i.sectorId )].name}}</td>
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
      <!-- ADD INSTRUMENT MODAL -->
      <div id="overlay" v-if="showAddDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Add New Instrument</h4>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="add()">
                <div class="form-group text-left">
                  <label for="inputEntity">Entity:</label>
                  <select id="inputEntity" class="form-control" v-model="newInstrument.entityIndex" required>
                    <option v-for="(entity, index) in entities" v-bind:key="index" v-bind:value="index">{{entity.name}}</option>
                  </select>
                </div>
                <div class="form-group text-left">
                  <label for="inputDescription">Description:</label>
                  <input id="inputDescription" v-model="newInstrument.description" class="form-control" required minlength="2" autofocus />
                </div>
                <div class="row">
                  <div class="form-group col-md-6 text-left">
                    <label for="inputType">Type:</label>
                    <select id="inputType" class="form-control" v-model="newInstrument.typeIndex" required>
                      <option v-for="(typ, index) in instrumentTypes" v-bind:key="index" v-bind:value="index">{{typ}}</option>
                    </select>
                  </div>
                  <div class="form-group col-md-6 text-left">
                    <label for="inputCurrency">Currency:</label>
                    <select id="inputCurrency" class="form-control" v-model="newInstrument.currencyIndex" required>
                      <option v-for="(currency, index) in currencies" v-bind:key="index" v-bind:value="index">{{currency.code}}</option>
                    </select>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-md-6 text-left">
                    <label for="inputCode">Code:</label>
                    <input id="inputCode" v-model="newInstrument.code" class="form-control" required autofocus />
                  </div>
                  <div class="form-group col-md-6 text-left">
                    <!--<label for="inputValue">Value:</label>
                    <input id="inputValue" v-model="newInstrument.value" class="form-control" required pattern="^\d+(\.\d+)?" minlength="2" autofocus />-->
                    <label for="inputDomicile">Domicile:</label>
                    <select id="inputDomicile" class="form-control" v-model="newInstrument.domicileIndex" required>
                      <option v-for="(domicile, index) in instrumentDomiciles" v-bind:key="index" v-bind:value="index">{{domicile.name}}</option>
                    </select>
                  </div>
                </div>
                <div class="form-group text-left">
                  <label for="inputSector">Sector:</label>
                  <select id="inputSector" class="form-control" v-model="newInstrument.sectorIndex" required>
                    <option v-for="(sector, index) in sectors" v-bind:key="index" v-bind:value="index">{{sector.name}}</option>
                  </select>
                </div>
                <!-- Note that footer inside modal body Unlike in Brokerages component the hr here is shorter, but still okay-->
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
                  <button type="submit" class="btn btn-primary">Save</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <!-- EDIT INSTRUMENT MODAL -->
      <div id="overlay" v-if="showEditDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Edit Instrument</h4>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="edit(false)">
                <div class="form-group text-left">
                  <label for="inputEntity">Entity:</label>
                  <select id="inputEntity" class="form-control" v-model="safeEditInfo.entityIndex" required>
                    <option v-for="(entity, index) in entities" v-bind:key="index" v-bind:value="index">{{entity.name}}</option>
                  </select>
                </div>
                <div class="form-group text-left">
                  <label for="inputDescription">Description:</label>
                  <input id="inputDescription" v-model="safeEditInfo.instrument.description" class="form-control" required minlength="2" autofocus />
                </div>
                <div class="row">
                  <div class="form-group col-md-6 text-left">
                    <label for="inputType">Type:</label>
                    <select id="inputType" class="form-control" v-model="safeEditInfo.instrument.type" required>
                      <option v-for="(typ, index) in instrumentTypes" v-bind:key="index" v-bind:value="typ">{{typ}}</option>
                    </select>
                  </div>
                  <div class="form-group col-md-6 text-left">
                    <label for="inputCurrency">Currency:</label>
                    <select id="inputCurrency" class="form-control" v-model="safeEditInfo.currencyIndex" required>
                      <option v-for="(currency, index) in currencies" v-bind:key="index" v-bind:value="index">{{currency.code}}</option>
                    </select>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-md-6 text-left">
                    <label for="inputCode">Code:</label>
                    <input id="inputCode" v-model="safeEditInfo.instrument.code" class="form-control" required autofocus />
                  </div>
                  <div class="form-group col-md-6 text-left">
                    <!--<label for="inputValue">Value:</label>
                    <input id="inputValue" v-model="newInstrument.value" class="form-control" required pattern="^\d+(\.\d+)?" minlength="2" autofocus />-->
                    <label for="inputDomicile">Domicile:</label>
                    <select id="inputDomicile" class="form-control" v-model="safeEditInfo.domicileIndex" required>
                      <option v-for="(domicile, index) in instrumentDomiciles" v-bind:key="index" v-bind:value="index">{{domicile.name}}</option>
                    </select>
                  </div>
                </div>
                <div class="form-group text-left">
                  <label for="inputSector">Sector:</label>
                  <select id="inputSector" class="form-control" v-model="safeEditInfo.sectorIndex" required>
                    <option v-for="(sector, index) in sectors" v-bind:key="index" v-bind:value="index">{{sector.name}}</option>
                  </select>
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
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('editButton')">Save</button>
            </div>
          </div>
        </div>
      </div>

      <!-- DELETE BROKER MODAL -->
      <div id="overlay" v-if="showDeleteDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Delete Instrument</h4>
              <button type="button" class="close" v-on:click="cancelDialog" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body p-4">
              <form v-on:submit.prevent="edit(true)">
                <div class="form-group">
                  <!--<input v-model="safeEditBrokerInfo.broker.name" class="form-control form-control-lg" readonly required minlength="2" autofocus /> -->
                  <div class="alert alert-danger text-left">This action will delete '{{safeEditInfo.instrument.description}}'</div>
                </div>
                <div class="form-group" hidden>
                  <button id="deleteButton" class="btn btn-primary btn-block btn-lg" type="submit">Delete</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('deleteButton')">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

// import * as utils from "./../utils";

export default {
  name: "Entities",
  data() {
    return {
      newInstrument: {
        code: null,
        entityIndex: null,
        description: null,
        typeIndex: 0,
        domicileIndex: 0,
        currencyIndex: 0,
        sectorIndex: null
      },
      safeEditInfo: {
        instrument: {
          id: null,
          code: null,
          description: null,
          type: null
          //
          //entityId: //  countryId: //   currencyId: //  sectorId: null
        },
        entityIndex: null,
        domicileIndex: null,
        currencyIndex: null,
        sectorIndex: null
      },

      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      selectedIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "currencies", "entities", "isBasicAdminDataGotten", "supportedInstruments", "instrumentTypes", "instrumentDomiciles", "sectors"])
  },
  methods: {
    //getListIndexById: utils.getListIndexById,
    //
    //
    triggerClickEvent(targetId) {
      document.getElementById(targetId).click();
    },
    //
    //
    //
    ...mapActions({
      getBasicAdminDataAction: "getBasicAdminDataAction",
      ensureAuthorized: "ensureAuthorized",
      addAction: "addInstrumentAction",
      editAction: "editInstrumentAction",
      deleteAction: "deleteInstrumentAction"
    }),
    //
    //
    //
    cancelDialog() {
      this.newInstrument = {
        code: null,
        entityIndex: null,
        description: null,
        typeIndex: 0,
        domicileIndex: 0,
        currencyIndex: 0,
        sectorIndex: null
      };

      this.showAddDialog = false;
      this.showEditDialog = false;
      this.showDeleteDialog = false;
      this.selectedIndex = -1;

      this.safeEditInfo = {
        instrument: {
          id: null,
          code: null,
          description: null,
          type: null
          //
          //entityId: //  countryId: //   currencyId: //  sectorId: null
        },
        entityIndex: null,
        domicileIndex: null,
        currencyIndex: null,
        sectorIndex: null
      };
    },
    //
    //
    //
    async add() {
      // //e.preventDefault() - already blocked with modifier
      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        const instr = this.newInstrument;

        let data = {
          type: this.instrumentTypes[instr.typeIndex],
          currencyId: this.currencies[instr.currencyIndex].id,
          code: this.newInstrument.code,
          entityId: this.entities[instr.entityIndex].id,
          countryId: this.instrumentDomiciles[instr.domicileIndex].code,
          sectorId: this.sectors[instr.sectorIndex].id,
          description: instr.description
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
      // e.preventDefault() - already blocked with modifier

      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        //map ui selections to persistence model attributes
        this.safeEditInfo.instrument.currencyId = this.currencies[this.safeEditInfo.currencyIndex].id;
        this.safeEditInfo.instrument.entityId = this.entities[this.safeEditInfo.entityIndex].id;
        this.safeEditInfo.instrument.countryId = this.instrumentDomiciles[this.safeEditInfo.domicileIndex].code;
        this.safeEditInfo.instrument.sectorId = this.sectors[this.safeEditInfo.sectorIndex].id;

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
    },
    //
    //
    //
    showEditDialogLauncher(isDelete) {
      //make a safe copy of the selected object - this avoids:
      // a) Messing with the Vuex state directly if we use the currently rendered object (rather than its safe copy)
      // b) Ensure that if a user cancels the edit mid-way, we don't have to worry about restoring a bound object
      const selectedObject = this.supportedInstruments[this.selectedIndex];

      this.safeEditInfo = {
        instrument: {
          id: selectedObject.id,
          code: selectedObject.code,
          description: selectedObject.description,
          type: selectedObject.type
          //
          //
          //will be populated layer, after UI closed
          // currencyId: //  entityId: // countryId: // sectorId:
        },
        entityIndex: this.entities.findIndex(item => item.id == selectedObject.entityId),
        domicileIndex: this.instrumentDomiciles.findIndex(domicile => domicile.code == selectedObject.countryId),
        currencyIndex: this.currencies.findIndex(item => item.id == selectedObject.currencyId),
        sectorIndex: this.sectors.findIndex(item => item.id == selectedObject.sectorId),
        //
        index: this.selectedIndex
      };
      if (isDelete) {
        this.showDeleteDialog = true;
      } else {
        //trigger the modal Edit dialog
        this.showEditDialog = true;
      }
    }
  },
  created() {
    if (!this.isBasicAdminDataGotten) {
      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        this.getBasicAdminDataAction();
      } else {
        this.$router.push("/");
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>

