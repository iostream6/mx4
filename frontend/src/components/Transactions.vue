<!--  
***  2020.05.29  - Created 
-->

<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row"></div>
          <div class="row mt-3">
            <div class="col-lg-9">
              <h3 class="text-primary float-left">Transactions</h3>
            </div>
            <div class="col-lg-3">
              <button class="btn btn-primary float-right" v-on:click="showAddDialog=true">
                <font-awesome-icon :icon="['fas', 'plus-square']" />&nbsp;&nbsp; Add New
              </button>
            </div>
          </div>
          <hr class="bg-primary" />
          <div class="row">
            <div class="col-lg-12">
              <table class="table table-bordered table-striped">
                <!--<colgroup>
                  <col span="1" style="width: 10%;" />
                  <col span="1" style="width: 10%;" />
                  <col span="1" style="width: 8%;" />
                  <col span="1" style="width: 6%;" />
                  <col span="1" style="width: 17%;" />
                  <col span="1" style="width: 6%;" />
                </colgroup>-->
                <thead>
                  <tr class="bg-primary text-light">
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'calendar-alt']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'briefcase']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'chess-pawn']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'chess']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'hryvnia']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'coins']" />
                    </th>
                    <th class="text-center">
                      <font-awesome-icon :icon="['fas', 'font']" />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(t, index) in transactions" v-bind:key="index">
                    <td>{{t.date}}</td>
                    <!--<td>{{t.description}}</td>
                    <td>{{i.type}}</td>
                    <td>{{currencies.find(item => item.id == i.currencyId).code}}</td>
                    <td class="text-left">{{sectors.find(item => item.id == i.sectorId ).name}}</td>-->
                    <td>
                      <a href="#" class="text-success" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(false)">
                        <font-awesome-icon :icon="['fas', 'edit']" />
                      </a> &nbsp; &nbsp; &nbsp; &nbsp;
                      <!-- Call a function that first makes a safe copy before it launches the dialog-->
                      <a href="#" class="text-danger" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(true)">
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
      <!-- ADD TRANSACTION MODAL -->
      <div id="overlay" v-if="showAddDialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="exampleModalLabel">Add New Transaction</h4>
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
                <!-- We could trigger the event on this directly but we want footer/validation so we show the footer, hide this
                and delegate back to this button (submit) to enable inbuilt validation-->
                <div class="form-group" hidden>
                  <button id="addButton" class="btn btn-primary btn-block btn-lg" type="submit">Edit</button>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <!-- we want to have the nornal footer buttons, the add option in the footer will trigger click
              event in the actual form submit button. Declaring the footer inside the form will avoid this but will
              render and a short hr footer
              -->
              <button type="button" class="btn btn-secondary" v-on:click="cancelDialog">Cancel</button>
              <button type="button" class="btn btn-primary" v-on:click="triggerClickEvent('addButton')">Save</button>
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
  name: "Entities",
  data() {
    return {
      newTransaction: {
        date: null,
        units: null,
        amountPerUnit: null,
        fees: null,
        taxes: null,

        typeIndex: null,
        portfolioIndex: null,
        currencyIndex: null,
        instrumentIndex: null
      },
      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      selectedIndex: -1
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "transactions", "transactionTypes", "currencies", "portfolios", "supportedInstruments"])
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
      ensureAuthorized: "ensureAuthorized",
      addAction: "addAction"
      //editAction: "editInstrumentAction",
      //deleteAction: "deleteInstrumentAction"
    }),
    //
    //
    //
    cancelDialog() {
      this.newTransaction = {
        date: null,
        units: null,
        amountPerUnit: null,
        fees: null,
        taxes: null,

        typeIndex: null,
        portfolioIndex: null,
        currencyIndex: null,
        instrumentIndex: null
      };

      this.showAddDialog = false;
      this.showEditDialog = false;
      this.showDeleteDialog = false;
      this.selectedIndex = -1;
    },
    //
    //
    //
    add() {
      //e.preventDefault() - already blocked with modifier
      this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        const t = this.newTransaction;

        let d = {
          date: t.date,
          units: t.units,
          amountPerUnit: t.amountPerUnit,
          fees: t.fees,
          taxes: t.taxes,
          //
          //
          portfolioId: this.portfolios[t.portfolioIndex].id,
          currencyId: this.currencies[t.currencyIndex].id,
          instrumentId: this.supportedInstruments[t.instrumentIndex].id,
          type: this.transactionTypes[t.typeIndex]
        };

        const requestInfo = {
          data: d,
          url: `/api/transactions/${this.user.userId}`,
          list: 'transactions'
        };

        this.addAction(requestInfo);
        this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    }
    // //
    // //
    // //
    // edit(isDelete) {},
    // //
    // //
    // //
    // showEditDialogLauncher(isDelete) {

    // }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>



