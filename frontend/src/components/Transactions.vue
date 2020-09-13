<!--  
***  2020.05.29  - Created 
***  2020.06.05  - Implemented add and basic list functionality
***  2020.06.07  - Switched to bootstrap-vue table. Implemented transaction list, filter/sort/pagination support
***  2020.06.10  - Improved add transaction implementation. Now uses transaction model in app state
***  2020.06.11  - Implemented edit transaction, using the add transaction harness/forms.
***  2020.07.04  - Transaction provisional attribute now correctly populated by frontend add/edit UI
***  2020.07.05  - Added transaction clone functionality
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
                  <b-form-input v-model="tableProps.filter" type="search" id="filterInput" placeholder="Type to Search"></b-form-input>
                  <b-input-group-append>
                    <b-button :disabled="!tableProps.filter" @click="filter = ''">Clear</b-button>
                  </b-input-group-append>
                </b-input-group>

                <b-button-group class="ml-3">
                  <b-button variant="primary" v-b-modal.t-modal>
                    <font-awesome-icon :icon="['fas', 'plus-square']" />
                  </b-button>
                  <b-button :variant="selectedRows.length != 1 ? 'secondary' : 'primary'" :disabled="selectedRows.length != 1" @click.prevent="prepareEditDialog(false);$bvModal.show('t-modal')">
                    <font-awesome-icon :icon="['fas', 'edit']" />
                  </b-button>
                  <b-button :variant="selectedRows.length != 1 ? 'secondary' : 'primary'" :disabled="selectedRows.length != 1" @click.prevent="prepareEditDialog(true);$bvModal.show('t-modal')">
                    <font-awesome-icon :icon="['fas', 'copy']" />
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
                :tbody-tr-class="rowClass"
                :striped="tableProps.striped"
                :bordered="tableProps.bordered"
                :borderless="tableProps.borderless"
                :outlined="tableProps.outlined"
                :selectable="tableProps.selectable"
                :small="tableProps.small"
                :hover="tableProps.hover"
                :dark="tableProps.dark"
                :fixed="tableProps.fixed"
                :items="transactions"
                :fields="tableFields"
                :thead-tr-class="tableProps.headerRowClass"
                :sort-by="tableProps.sortBy"
                :sort-desc="tableProps.sortDesc"
                :primary-key="tableProps.primaryKey"
                :filter="tableProps.filter"
                :filterIncludedFields="tableProps.filterOn"
                :current-page="tableProps.currentPage"
                :per-page="tableProps.perPage"
                @row-selected="onRowSelected"
              ></b-table>
            </div>
          </div>

          <div class="row">
            <div class="col-lg-12">
              <b-pagination v-model="tableProps.currentPage" :total-rows="transactions.length" :per-page="tableProps.perPage" align="fill" size="sm" class="my-0"></b-pagination>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- ADD/EDIT TRANSACTION MODAL  -->
    <div>
      <b-modal id="t-modal" @ok="validateModalForm($event)" @hidden="cancelDialog()" centered v-bind:title="transaction.id == null ? 'Add New Transaction'  : 'Edit Transaction'">
        <form>
          <b-form-group label="Date:" label-for="transactionDate" :invalid-feedback="errorInfo['date']" :state="errorInfo['is-date']">
            <b-form-datepicker id="transactionDate" v-model="transaction.date" class="mb-2"></b-form-datepicker>
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
          <div class="row">
            <div class="form-group col-md-12 text-left">
              <b-form-checkbox id="optValidated" v-model="transaction.provisional">Input data is provisional</b-form-checkbox>
            </div>
          </div>
        </form>
      </b-modal>
    </div>
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
        provisional: true,
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
      showDeleteDialog: false,
      selectedRows: [],
      //additional table props
      tableProps: {
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
        headerRowClass: "bg-primary text-light",
        currentPage: 1,
        perPage: 30,
        pageOptions: [10, 20, 30, 50]
      }
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "transactions", "transactionTypes", "currencies", "portfolios", "supportedInstruments"])
  },
  methods: {
    //
    //
    //
    rowClass(item, type) {
      if (!item || type !== "row") return;
      if (item.provisional) return "text-danger";
    },
    //
    //
    //
    ...mapActions({
      ensureAuthorized: "ensureAuthorized",
      addAction: "addAction",
      getAction: "getAction",
      deleteAction: "deleteAction",
      editAction: "editAction"
    }),
    ...mapMutations({ setAddedObject: "setAddedObject", setEditedObject: "setEditedObject" }),
    //
    onRowSelected(items) {
      this.selectedRows = items;
    },
    //
    //
    //
    validateModalForm(event) {
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
      if (this.transaction.id == null) {
        this.addTransaction();
      } else {
        this.editTransaction();
      }
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
        provisional: true,
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
    async addTransaction() {
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
          provisional: t.provisional,
          //
          //
          portfolioId: t.portfolio.id,
          currencyId: t.currency.id,
          instrumentId: t.instrument.id
        };
        const requestInfo = {
          data: d,
          url: `/api/transactions/${this.user.userId}`
          //list: "transactions"
        };
        this.addAction(requestInfo).then(addedResponseObject => {
          const item = {};
          for (const input of ["type", "units", "amountPerUnit", "fees", "taxes", "provisional"]) {
            item[input] = d[input];
          }

          item["id"] = addedResponseObject.data.id;

          item.date = new Date(addedResponseObject.data.date);
          //the following text attributes are helpful/needed in table filtering, hence they are used
          item.portfolioCode = t.portfolio.code;
          item.currencyCode = t.currency.code;
          item.instrumentCode = t.instrument.code;
          //
          this.setAddedObject({ data: item, list: "transactions" });
          this.cancelDialog();
          this.$refs.ttable.refresh();
        });
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    async editTransaction() {
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
          provisional: t.provisional,
          //
          //
          portfolioId: t.portfolio.id,
          currencyId: t.currency.id,
          instrumentId: t.instrument.id,
          id: t.id
        };
        const requestInfo = {
          data: d,
          url: `/api/transaction/${this.user.userId}/${d.id}`
          //list: "transactions",
          //index: this.transactions.indexOf(t)
        };
        const result = await this.editAction(requestInfo);
        if (result == true) {
          //update the table with saved data
          const item = {};
          for (const input of ["id", "type", "units", "amountPerUnit", "fees", "taxes", "provisional"]) {
            item[input] = t[input];
          }
          item.date = new Date(t.date);
          //the following text attributes are helpful/needed in table filtering, hence they are used
          item.portfolioCode = t.portfolio.code;
          item.currencyCode = t.currency.code;
          item.instrumentCode = t.instrument.code;
          //
          this.setEditedObject({ data: item, list: "transactions", index: this.transactions.findIndex(item => item.id == t.id) });
          this.cancelDialog();
          this.$refs.ttable.refresh();
        }
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
    prepareEditDialog(isClone) {
      // let parameter = null;
      const selectedTransaction = this.selectedRows[0];
      const se = this.transaction;
      for (const input of ["date", "type", "units", "amountPerUnit", "fees", "taxes", "provisional"]) {
        se[input] = selectedTransaction[input];
      }

      if(!isClone){
        //this is edit mode of selected. If clone mode, we dont want to copy id as a new transaction should be created.
        se.id = selectedTransaction.id;
      }

      //
      se.portfolio = this.portfolios.find(item => item.code == selectedTransaction.portfolioCode);
      se.currency = this.currencies.find(item => item.code == selectedTransaction.currencyCode);
      se.instrument = this.supportedInstruments.find(item => item.code == selectedTransaction.instrumentCode);
    },

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



