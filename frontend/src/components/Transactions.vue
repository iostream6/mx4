<!--  
***  2020.05.29  - Created 
***  2020.06.05  - Implemented add and basic list functionality
***  2020.06.07  - Switched to bootstrap-vue table. Implemented transaction list, filter/sort/pagination support
***  2020.06.10  - Improved add transaction implementation. Now uses transaction model in app state
-->
<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row mt-3">
            <div class="col-lg-7">
              <h3 class="text-primary float-left">Transactions</h3>
            </div>
            <div class="col-lg-5">
              <b-form inline>
                <b-input-group class="ml-4">
                  <b-form-input v-model="filter" type="search" id="filterInput" placeholder="Type to Search"></b-form-input>
                  <b-input-group-append>
                    <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
                  </b-input-group-append>
                </b-input-group>

                <b-button-group class="ml-3">
                  <b-button variant="primary" v-b-modal.add-modal>
                    <font-awesome-icon :icon="['fas', 'plus-square']" />
                  </b-button>
                  <b-button :variant="selectedRows.length != 1 ? 'secondary' : 'primary'" :disabled="selectedRows.length != 1" @click.prevent="showEditDialogLauncher(false)">
                    <font-awesome-icon :icon="['fas', 'edit']" />
                  </b-button>
                  <b-button :variant="selectedRows.length == 0 ? 'secondary' : 'primary'" :disabled="selectedRows.length == 0 " @click="deleteTransactions()">
                    <font-awesome-icon :icon="['fas', 'trash-alt']" />
                  </b-button>
                </b-button-group>
              </b-form>
            </div>

            <div class="col-lg-2"></div>
          </div>
          <hr class="bg-primary" />

          <div class="row">
            <div class="col-lg-12">
              <b-table
                ref="ttable"
                :striped="striped"
                :bordered="bordered"
                :borderless="borderless"
                :outlined="outlined"
                :selectable="selectable"
                :small="small"
                :hover="hover"
                :dark="dark"
                :fixed="fixed"
                :items="transactions"
                :fields="tableFields"
                :thead-tr-class="headerRowClass"
                :sort-by="sortBy"
                :sort-desc="sortDesc"
                :primary-key="primaryKey"
                :filter="filter"
                :filterIncludedFields="filterOn"
                :current-page="currentPage"
                :per-page="perPage"
                @row-selected="onRowSelected"
              ></b-table>
            </div>
          </div>

          <div class="row">
            <div class="col-lg-12">
              <b-pagination v-model="currentPage" :total-rows="transactions.length" :per-page="perPage" align="fill" size="sm" class="my-0"></b-pagination>
            </div>
          </div>

          <!-- 

          <div class="row">
            <div class="col-lg-12">
              <table class="table table-bordered table-striped">
                <! - -<colgroup>
                  <col span="1" style="width: 10%;" />
                  <col span="1" style="width: 10%;" />
                  <col span="1" style="width: 8%;" />
                  <col span="1" style="width: 6%;" />
                  <col span="1" style="width: 17%;" />
                  <col span="1" style="width: 6%;" />
                </colgroup> - - >
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
                  <! - -
                  <tr v-for="(t, index) in transactions" v-bind:key="index">
                    <td>{{t.date}}</td>
                    <td>{{portfolios.find(item => item.id == t.portfolioId).code}}</td>
                    <td>{{supportedInstruments.find(item => item.id == t.instrumentId).code}}</td>
                    <td>{{t.type}}</td>
                    <td>{{currencies.find(item => item.id == t.currencyId).code}}</td>
                    <td>{{t.units}}</td>
                    <! - - <td class="text-left">{{sectors.find(item => item.id == i.sectorId ).name}}</td> - ->
                    <td>
                      <a href="#" class="text-success" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(false)">
                        <font-awesome-icon :icon="['fas', 'edit']" />
                      </a> &nbsp; &nbsp; &nbsp; &nbsp;
                      <! - - Call a function that first makes a safe copy before it launches the dialog - ->
                      <a href="#" class="text-danger" v-on:click.prevent="selectedIndex=index; showEditDialogLauncher(true)">
                        <font-awesome-icon :icon="['fas', 'trash-alt']" />
                      </a>
                    </td>
                  </tr>
                  - - >
                </tbody>
              </table>
            </div>
          </div>-->

          <!-- -->
        </div>
      </main>
    </div>

    <!-- ADD TRANSACTION MODAL -->
    <div>
      <b-modal id="add-modal" @ok="validateModalForm('addButton', $event)" @hidden="cancelDialog()" centered title="Add New Transaction">
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

    <!-- EDIT TRANSACTION MODAL  -->
  </div>
</template>

<script>
import { mapState, mapActions, mapMutations } from "vuex";

//todo SPECIFY date options, see
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat
//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/toLocaleDateString

//en-CA locale gives yyyy-MM-dd | https://stackoverflow.com/a/60012660
const dateFormatter = new Intl.DateTimeFormat("en-CA", {
  year: "numeric",
  month: "2-digit",
  day: "2-digit"
});

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
      headerRowClass: "bg-primary text-light",
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
      tableFields: [
        // //column that need special formatting
        {
          key: "date",
          sortable: true,
          formatter: value => {
            return dateFormatter.format(value);
          }
        },

        {
          key: "portfolioCode",
          label: "Portfolio"
          //sortable: true
        },
        {
          key: "instrumentCode",
          label: "Instrument",
          sortable: true
        },
        {
          key: "type",
          sortable: true
        },
        {
          key: "currencyCode",
          label: "Currency"
          //sortable: true
          // formatter: value => {
          //   return value.code;
          // }
        },
        {
          key: "units"
          //sortable: true
        },
        {
          key: "amountPerUnit",
          label: "Rate"
          //sortable: true
        }
      ],

      //additional table props
      primaryKey: "id",
      striped: false,
      bordered: false,
      borderless: false,
      outlined: true,
      small: true,
      hover: false,
      dark: false,
      fixed: true,
      selectable: true,
      sortBy: "date",
      sortDesc: false,
      filter: null,
      filterOn: [],
      showDeleteDialog: false,
      selectedRows: [],
      currentPage: 1,
      perPage: 30,
      pageOptions: [10, 20, 30, 50]
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "transactions", "transactionTypes", "currencies", "portfolios", "supportedInstruments"])
  },
  methods: {
    //
    //
    //
    ...mapActions({
      ensureAuthorized: "ensureAuthorized",
      addAction: "addAction",
      getAction: "getAction",
      deleteAction: "deleteAction"
      //editAction: "editInstrumentAction",
      //deleteAction: "deleteInstrumentAction"
    }),
    ...mapMutations({setAddedObject: "setAddedObject"}),
    //
    onRowSelected(items) {
      this.selectedRows = items;
    },
    //
    //
    //
    validateModalForm(sourceId, event) {
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
      if (sourceId == "addButton") {
        this.add();
      } else {
        //TODO
      }

      //document.getElementById(targetId).click();
    },
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
          //so that it can be added to the frontend transaction table
          portfolioCode: t.portfolio.code,
          currencyCode: t.currency.code,
          instrumentCode: t.instrument.code
        };
        const requestInfo = {
          data: d,
          url: `/api/transactions/${this.user.userId}`
          //list: "transactions"
        };
        this.addAction(requestInfo).then(addedResponseObject => {
          const item = {};
          for (const input of [ "type", "units", "amountPerUnit", "fees", "taxes"]) {
            item[input] = d[input];
          }

          item["id"] = addedResponseObject.data.id;

          item.date = new Date(addedResponseObject.data.date);
          //the following text attributes are helpful/needed in table filtering, hence they are used
          item.portfolioCode = t.portfolio.code;
          item.currencyCode = t.currency.code;
          item.instrumentCode = t.instrument.code;
          //
          this.setAddedObject({data: item, list: "transactions"});
          this.cancelDialog();
          this.$refs.ttable.refresh();
        });
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    // //
    // //
    // //
    deleteTransactions() {
      const thisInstanceReference = this;
      this.$bvModal
        .msgBoxConfirm("Are you sure you want to deleted all the selected transactions?", {
          title: "Please Confirm",
          //size: "sm",
          buttonSize: "sm",
          okVariant: "danger",
          okTitle: "Yes",
          cancelTitle: "No",
          footerClass: "p-2",
          hideHeaderClose: false,
          centered: true
        })
        .then(value => {
          if (value == true) {
            this.ensureAuthorized().then(function() {
              if (thisInstanceReference.authenticated == true) {
                let parameter = null;
                const selectedIndexes = [];
                for (const t of thisInstanceReference.selectedRows) {
                  if (parameter == null) {
                    parameter = t.id;
                  } else {
                    parameter = `${parameter},${t.id}`;
                  }
                  selectedIndexes.push(thisInstanceReference.transactions.indexOf(t));
                }
                const requestInfo = {
                  url: `/api/transactions/${thisInstanceReference.user.userId}/${parameter}`,
                  list: "transactions",
                  indexes: selectedIndexes
                };
                thisInstanceReference.deleteAction(requestInfo).then(function(response) {
                  console.log(`Multiple gets response: ${response}`);
                });
              } else {
                thisInstanceReference.$router.push("/");
              }
            });
          }
          //this.boxTwo = value;
        })
        .catch();
    },
    // //
    // //
    // //
    showEditDialogLauncher(isDelete) {
      console.log(`Calling Edit ${isDelete}`);
    }
  }
  // created() {
  //   if (this.transactions.length == 0) {
  //     const thisInstanceReference = this;
  //     //will updated authenticated state
  //     this.ensureAuthorized().then(function() {
  //       if (thisInstanceReference.authenticated == true) {

  //         const requestInfo = {
  //           url: `/api/transactions/${thisInstanceReference.user.userId}`,
  //           //list: "transactions",
  //           replace: true,
  //           mapper: mapperFunction
  //         };

  //         thisInstanceReference.getAction(requestInfo).then(function(response) {
  //           for (const t of response) {
  //             const item = {};
  //             for (const input of ["id", "type", "units", "amountPerUnit"]) {
  //               item[input] = t[input];
  //             }

  //             item.date = new Date(t.date);
  //             item.portfolio = thisInstanceReference.portfolios.find(item => item.id == t.portfolioId);
  //             item.currency = thisInstanceReference.currencies.find(item => item.id == t.currencyId);
  //             item.instrument = thisInstanceReference.supportedInstruments.find(item => item.id == t.instrumentId);
  //             //item.portfolio = this.portfolios.find(item => item.id == t.portfolioId).code;
  //             //
  //             thisInstanceReference.transactions.push(item);
  //           }
  //           thisInstanceReference.totalRows = thisInstanceReference.transactions.length;
  //         });
  //       } else {
  //         thisInstanceReference.$router.push("/");
  //       }
  //     });
  //   }
  // }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>



