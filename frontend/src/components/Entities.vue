<!--  
***  2020.05.24  - Created 
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
                    <td>{{getCurrencyName(i.currencyId)}}</td>
                    <td class="text-left">{{getSectorName(i.sectorId)}}</td>
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
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
                  <button type="submit" class="btn btn-primary">Save</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters, mapMutations, mapActions } from "vuex";

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

      instruments: [],
      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      selectedIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "currencies", "entities", "isBasicAdminDataGotten", "supportedInstruments", "instrumentTypes", "instrumentDomiciles", "sectors"]),
    // isJWTValid returns a function so even though computed, it is not cached
    ...mapGetters(["isJWTValid"])
  },
  methods: {
    //
    //
    //
    ...mapActions({
      refreshTokenAction: "refreshTokenAction",
      getBasicAdminDataAction: "getBasicAdminDataAction",
      addAction: "addInstrumentAction"
      //editAction: "editPortfolioAction",
      //deleteAction: "deletePortfolioAction"
    }),
    //
    //
    //
    ...mapMutations(["clearAuthentication"]),
    //
    //
    //
    cancelDialog() {
      this.newInstrument = {
        code: null,
        entityIndex: null,
        description: null,
        typeIndex: 0,
        domicileIndex: 1,
        currencyIndex: 3,
        sectorIndex: null
      };

      this.showAddDialog = false;
      this.showEditDialog = false;
      this.showDeleteDialog = false;

      //this.safeEditInfo = { portfolio: { name: null } };
      this.selectedIndex = -1;
    },
    //
    //
    //
    async add() {
      // //e.preventDefault() - already blocked with modifier
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
    },
    //
    //
    //
    getCurrencyName(currencyId) {
      let dataIndex = 0;

      for (; dataIndex < this.currencies.length; dataIndex++) {
        if (this.currencies[dataIndex].id == currencyId) {
          return this.currencies[dataIndex].code;
        }
      }
      return "unknown";
    },
    //
    //
    //
    getSectorName(sectorId) {
      let dataIndex = 0;

      for (; dataIndex < this.sectors.length; dataIndex++) {
        if (this.sectors[dataIndex].id == sectorId) {
          return this.sectors[dataIndex].name;
        }
      }
      return "unknown";
    }
  },
  created() {
    let loggedIn = this.isJWTValid(new Date());
    if (!loggedIn) {
      this.refreshTokenAction();
      //if after refresh attempt, still not valid? Force signin
      loggedIn = this.isJWTValid(new Date());
      if (!loggedIn) {
        this.clearAuthentication();
        this.$router.push("/");
      }
    }
    //load entities if needed
    if (loggedIn && !this.isBasicAdminDataGotten) {
      this.getBasicAdminDataAction();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>

