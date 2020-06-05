<!--  
***  2020.05.29  - Created 
***  2020.06.05  - Implemented add and basic list functionalitiy
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
              <b-button b-button variant="primary" class="float-right" v-b-modal.add-modal>
                <font-awesome-icon :icon="['fas', 'plus-square']" />&nbsp;&nbsp; Add New
              </b-button>
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
                   <td>{{portfolios.find(item => item.id == t.portfolioId).code}}</td>
                     <td>{{supportedInstruments.find(item => item.id == t.instrumentId).code}}</td>
                     <td>{{t.type}}</td>
                    <td>{{currencies.find(item => item.id == t.currencyId).code}}</td>
                    <td>{{t.units}}</td>
                    <!--<td class="text-left">{{sectors.find(item => item.id == i.sectorId ).name}}</td>-->
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
      <b-modal id="add-modal" @ok="triggerClickEvent('addButton', $event)" @hidden="cancelDialog()" centered title="Add New Transaction">
        <form>
          <!-- @reset="onReset"-->
          <!--<b-form-group label="Date:" label-for="newTransactionDatex">
            <b-form-input type="date" id="newTransactionDatex" v-model="newTransaction.taxes" class="mb-2" required></b-form-input>
          </b-form-group>-->
          <b-form-group label="Date:" label-for="newTransactionDate" :invalid-feedback="errorInfo['date']" :state="errorInfo['is-date']">
            <b-form-datepicker id="newTransactionDate" v-model="transaction.date" class="mb-2"></b-form-datepicker>
          </b-form-group>
          <div class="row">
            <div class="form-group col-md-6 text-left">
              <label for="inputPortfolio">Portfolio:</label>
              <b-form-select id="inputPortfolio" v-model="transaction.portfolio" :invalid-feedback="errorInfo['portfolio']" :state="errorInfo['is-portfolio']">
                <b-form-select-option :value="null" disabled>Select an option</b-form-select-option>
                <b-form-select-option v-for="(port, index) in portfolios" v-bind:key="index" v-bind:value="port">{{port.code}}</b-form-select-option>
              </b-form-select>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputInstrument">Instrument:</label>
              <b-form-select id="inputInstrument" v-model="transaction.instrument" :invalid-feedback="errorInfo['instrument']" :state="errorInfo['is-instrument']">
                <b-form-select-option :value="null" disabled>Select an option</b-form-select-option>
                <b-form-select-option v-for="(inst, index) in supportedInstruments" v-bind:key="index" v-bind:value="inst">{{inst.code}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-6 text-left">
              <label for="inputType">Type:</label>
              <b-form-select id="inputType" v-model="transaction.type" :options="transactionTypes" :invalid-feedback="errorInfo['type']" :state="errorInfo['is-type']">
                <template v-slot:first>
                  <b-form-select-option :value="null" disabled>Select an option</b-form-select-option>
                </template>
              </b-form-select>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputCurrency">Currency:</label>
              <b-form-select id="inputCurrency" v-model="transaction.currency" :invalid-feedback="errorInfo['currency']" :state="errorInfo['is-currency']">
                <b-form-select-option :value="null" disabled>Select an option</b-form-select-option>
                <b-form-select-option v-for="(currency, index) in currencies" v-bind:key="index" v-bind:value="currency">{{currency.code}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-6 text-left">
              <label for="inputUnits">Units:</label>
              <b-form-input id="inputUnits" type="text" v-model="transaction.units" placeholder="Enter quantity" pattern="^\d+(\.\d+)?" :invalid-feedback="errorInfo['units']" :state="errorInfo['is-units']"></b-form-input>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputAmount">Rate:</label>
              <b-form-input id="inputAmount" type="text" v-model="transaction.amountPerUnit" placeholder="Enter amount per unit" pattern="^\d+(\.\d+)?" :invalid-feedback="errorInfo['amountPerUnit']" :state="errorInfo['is-amountPerUnit']"></b-form-input>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-6 text-left">
              <label for="inputFees">Fees:</label>
              <b-form-input id="inputFees" type="text" v-model="transaction.fees" placeholder="Enter fees" pattern="^\d+(\.\d+)?" :invalid-feedback="errorInfo['fees']" :state="errorInfo['is-fees']"></b-form-input>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputTaxes">Taxes:</label>
              <b-form-input id="inputTaxes" type="text" v-model="transaction.taxes" placeholder="Enter taxes" pattern="^\d+(\.\d+)?" :invalid-feedback="errorInfo['taxes']" :state="errorInfo['is-taxes']"></b-form-input>
              <!-- pattern="^\d+(\.\d+)?" -->
            </div>
          </div>
        </form>
      </b-modal>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "Transactions",
  data() {
    return {
      transaction: {
        date: null,
        units: null,
        amountPerUnit: null,
        fees: null,
        taxes: null,
        type: null,
        //
        portfolio: null,
        currency: null,
        instrument: null
      },

      errorInfo: {
        date: null,
        portfolio: null,
        instrument: null,
        type: null,
        currency: null,
        units: null,
        amountPerUnit: null,
        fees: null,
        taxes: null,
        //
        "is-date": null,
        "is-portfolio": null,
        "isis-instrument": null,
        "is-type": null,
        "is-currency": null,
        "is-units": null,
        "is-amountPerUnit": null,
        "is-fees": null,
        "is-taxes": null,
        //
        "is-valid": false
      },

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
    triggerClickEvent(targetId, event) {
      //reset before next check
      for (const prop in this.errorInfo) {
        this.errorInfo[prop] = null;
      }
      this.errorInfo["is-valid"] = true;

      // manual validation (datepicker does not work with native browser validation)
      const inputs = ["date", "portfolio", "instrument", "type", "currency", "units", "amountPerUnit"];
      for (const input of inputs) {
        if (this.transaction[input] == null) {
          this.errorInfo[input] = `Please enter ${input}`;
          this.errorInfo[`is-${input}`] = false;
          this.errorInfo["is-valid"] = false;
        } else {
          this.errorInfo[input] = null;
          this.errorInfo[`is-${input}`] = null;
        }
      }
      for (const input of ["units", "amountPerUnit", "fees", "taxes"]) {
        if (this.transaction[input] != null) {
          if (/^\d+(\.\d+)?/.test(this.transaction[input]) == false) {
            console.log(`Inside regex match ${input}`);
            this.errorInfo[input] = `Enter valid ${input}`;
            this.errorInfo[`is-${input}`] = false;
            this.errorInfo["is-valid"] = false;
          } else {
            this.errorInfo[input] = null;
            this.errorInfo[`is-${input}`] = null;
          }
        }
      }

      if (this.errorInfo["is-valid"] == false) {
        event.preventDefault(); //only block ok click if validation issue
        return;
      }

      if (targetId == "addButton") {
        this.add();
      } else {
        //TODO
      }

      //document.getElementById(targetId).click();
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
      this.transaction = {
        date: null,
        units: null,
        amountPerUnit: null,
        fees: null,
        taxes: null,
        type: null,
        //
        
        portfolio: null,
        currency: null,
        instrument: null
        //
      };

      //reset
      for (const prop in this.errorInfo) {
        this.errorInfo[prop] = null;
      }
      this.errorInfo["is-valid"] = true;

      this.showEditDialog = false;
      this.showDeleteDialog = false;
      this.selectedIndex = -1;
    },
    //
    //
    //
    async add() {
      //e.preventDefault() - already blocked with modifier
      await this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
           const t = this.transaction;
          let d = {
            date: t.date,
            units: t.units,
            amountPerUnit: t.amountPerUnit,
            fees: t.fees,
            taxes: t.taxes,
            type: t.type,
            //
            //
            portfolioId: t.portfolio.id,
            currencyId: t.currency.id,
            instrumentId: t.instrument.id,
          };
          const requestInfo = {
            data: d,
            url: `/api/transactions/${this.user.userId}`,
            list: "transactions"
          };
          this.addAction(requestInfo);
          this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
      console.log("Called Add");
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



