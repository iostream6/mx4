<!--  
***  2020.05.24  - Created 
***  2020.05.27  - Implemented 
***  2020.05.30  - Use FA 5.0  and simplified/centralised add/edit/delete actions for domain objects
***  2020.09.12  - Switch table to Bootstrap-vue component and assign allignment a & widths using https://github.com/bootstrap-vue/bootstrap-vue/pull/531
***                Now supports Entity (add) and Instruments (add/edit).
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
              <b-button-group class="ml-3 float-right">
                <b-button variant="primary" v-b-modal.x-modal>
                  <font-awesome-icon :icon="['fas', 'plus-square']" />
                </b-button>
                <b-button :variant="selectedRows.length != 1 || entityMode ? 'secondary' : 'primary'" :disabled="selectedRows.length != 1 || entityMode" @click.prevent="makeSafeFormData();$bvModal.show('x-modal')">
                  <font-awesome-icon :icon="['fas', 'edit']" />
                </b-button>
                <b-button :variant='secondary' :disabled="true" @click="deleteInstruments()">
                  <font-awesome-icon :icon="['fas', 'trash-alt']" />
                </b-button>
              </b-button-group>

              <b-button-group class="ml-3 float-right">
                <b-button :variant="entityMode ? 'secondary' : 'primary'" :disabled="entityMode" @click.prevent="switchMode()">
                  <font-awesome-icon :icon="['fas', 'chess-pawn']" />
                </b-button>
                <b-button :variant="entityMode==false ? 'secondary' : 'primary'" :disabled="!entityMode" @click.prevent="switchMode()">
                  <font-awesome-icon :icon="['fas', 'chess-queen']" />
                </b-button>
              </b-button-group>
            </div>
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
                :items="supportedInstruments"
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
              <!--<table class="table table-bordered table-striped">
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
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(i, index) in supportedInstruments" v-bind:key="index">
                    <td>{{i.code}}</td>
                    <td class="text-left">{{i.description}}</td>
                    <td>{{i.type}}</td>
                    <td>{{currencies.find(item => item.id == i.currencyId).code}}</td>
                    <td class="text-left">{{sectors.find(item => item.id == i.sectorId ).name}}</td>
                  </tr>
                </tbody>
              </table>-->
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- ADD/EDIT MODAL  -->
    <div>
      <b-modal id="x-modal" @ok="validateModalForm($event)" @hidden="cancelDialog()" centered v-bind:title="entityMode ? 'Add New Entity' : instrument.id == null ? 'Add New Instrument'  : 'Edit Instrument'">
        <form>
          <!-- The below will only activate in entity mode -->
          <b-form-group v-if="entityMode" label="Name:" label-for="inputEntityName">
            <!-- use instrument code var to store entity name when working in entity mode -->
            <b-form-input id="inputEntityName" v-model="instrument.code" class="form-control" :state="errorInfo['is-code']" />
          </b-form-group>
          <b-form-group v-if="entityMode" label="Sector:" label-for="inputSector">
            <b-form-select id="inputSector" v-model="instrument.sectorIndex" :state="errorInfo['is-sectorIndex']">
              <b-form-select-option v-for="(sector, index) in sectors" v-bind:key="index" v-bind:value="index">{{sector.name}}</b-form-select-option>
            </b-form-select>
          </b-form-group>
          <!-- 
            //
            //
          -->
          <!-- The below will only activate in entity mode -->
          <b-form-group v-if="entityMode==false">
            <label for="inputEntity">Entity:</label>
            <b-form-select id="inputEntity" v-model="instrument.entityIndex" :state="errorInfo['is-entityIndex']">
              <b-form-select-option :value="null" disabled>Select an option</b-form-select-option>
              <b-form-select-option v-for="(entity, index) in entities" v-bind:key="index" v-bind:value="index">{{entity.name}}</b-form-select-option>
            </b-form-select>
          </b-form-group>
          <div class="row" v-if="entityMode==false">
            <div class="form-group col-md-6 text-left">
              <label for="inputType">Type:</label>
              <b-form-select id="inputType" v-model="instrument.type" :state="errorInfo['is-type']">
                <b-form-select-option v-for="(typ, index) in instrumentTypes" v-bind:key="index" v-bind:value="typ">{{typ}}</b-form-select-option>
              </b-form-select>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputCurrency">Currency:</label>
              <b-form-select id="inputCurrency" v-model="instrument.currencyIndex" :state="errorInfo['is-currencyIndex']">
                <b-form-select-option v-for="(currency, index) in currencies" v-bind:key="index" v-bind:value="index">{{currency.code}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <div class="row" v-if="entityMode==false">
            <div class="form-group col-md-6 text-left">
              <label for="inputCode">Code:</label>
              <b-form-input id="inputCode" v-model="instrument.code" class="form-control" :state="errorInfo['is-code']" />
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="inputDomicile">Domicile:</label>
              <b-form-select id="inputDomicile" v-model="instrument.domicileIndex" :state="errorInfo['is-domicileIndex']">
                <b-form-select-option v-for="(domicile, index) in instrumentDomiciles" v-bind:key="index" v-bind:value="index">{{domicile.name}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <!-- 
            //
            //
          -->
          <!-- The below will always be visible in entity/instrument mode -->
          <b-form-group label="Description:" label-for="inputDescription">
            <b-form-input id="inputDescription" v-model="instrument.description" class="form-control" :state="errorInfo['is-description']" />
          </b-form-group>
        </form>
      </b-modal>
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
      instrument: {
        code: null,
        entityIndex: null,
        description: null,
        type: null,
        domicileIndex: null,
        currencyIndex: null,
        sectorIndex: null
      },
      errorInfo: {
        "is-code": null,
        "is-entityIndex": null,
        "is-sectorIndex": null,
        "is-description": null,
        "is-currencyIndex": null,
        "is-domicileIndex": null,
        "is-type": null,
        //
        "is-valid": false
      },
      selectedRows: [],
      entityMode: false,
      tableFields: [
        {
          key: "code",
          label: "Code",
          thStyle: {
            width: "110px"
          },
          sortable: true
        },
        {
          key: "description",
          label: "Description",
          class: "text-left",
          sortable: true
        },
        {
          key: "type",
          thStyle: {
            width: "110px"
          },
          sortable: true
        }
        // {
        //   key: "currencyCode",
        //   label: "Currency"
        //   //sortable: true
        //   // formatter: value => {
        //   //   return value.code;
        //   // }
        // },
        // {
        //   key: "units"
        //   //sortable: true
        // }
      ],
      //additional table props
      tableProps: {
        primaryKey: "id",
        striped: true,
        bordered: true,
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
        perPage: 50,
        pageOptions: [10, 20, 30, 50]
      }
    };
  },
  computed: {
    ...mapState(["user", "authenticated", "currencies", "entities", "isBasicAdminDataGotten", "supportedInstruments", "instrumentTypes", "instrumentDomiciles", "sectors"])
  },
  methods: {
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
      const inputs = this.entityMode ? ["code", "sectorIndex"] : ["entityIndex", "type", "currencyIndex", "code", "domicileIndex", "description"];
      for (const input of inputs) {
        if (this.instrument[input] == null) {
          //this.errorInfo[input] = `Input required`;
          this.errorInfo[`is-${input}`] = false;
          this.errorInfo["is-valid"] = false;
        } else {
          //this.errorInfo[input] = null;
          this.errorInfo[`is-${input}`] = null;
        }
      }
      if (this.errorInfo["is-valid"] == false) {
        event.preventDefault(); //only block ok click if validation issue
        return;
      }
      if (this.instrument.id == null) {
        this.add();
      } else {
        this.edit();
      }
    },

    //
    //
    //
    ...mapActions({
      //getBasicAdminDataAction: "getBasicAdminDataAction",
      ensureAuthorized: "ensureAuthorized",
      addAction: "addAction",
      editAction: "editAction",
      deleteAction: "deleteAction"
    }),
    //
    //
    //
    cancelDialog() {
      console.log("Ask Cancel");
      this.instrument = {
        code: null,
        entityIndex: null,
        description: null,
        type: null,
        domicileIndex: null,
        currencyIndex: null,
        sectorIndex: null
      };
    },
    //
    //
    //
    switchMode() {
      this.entityMode = !this.entityMode;
    },
    //
    //
    //
    async add() {
      //e.preventDefault()
      await this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        const instr = this.instrument;
        const requestInfo = this.entityMode
          ? {
              data: {
                name: instr.code,
                description: instr.description,
                sectorId: this.sectors[instr.sectorIndex].id
              },
              url: `/admin/entities`,
              list: "entities"
            }
          : {
              data: {
                type: instr.type,
                currencyId: this.currencies[instr.currencyIndex].id,
                code: instr.code,
                entityId: this.entities[instr.entityIndex].id,
                countryId: this.instrumentDomiciles[instr.domicileIndex].code,
                description: instr.description
              },
              url: `/admin/instruments`,
              list: "supportedInstruments"
            };

        this.addAction(requestInfo);
        this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    async edit() {
      // e.preventDefault() - already blocked with modifier
      await this.ensureAuthorized(); //will updated authenticated state
      if (this.authenticated == true) {
        // //make a safe copy - async might not return before bubbled cancel is called
        // const safeCopy = {};
        // for (var prop in this.instrument) {
        //   safeCopy[prop] = this.instrument[prop];
        // }

        //processing control params
        const editInfo = {data: {}, url: `/admin/instrument/${this.instrument.id}`, list: "supportedInstruments", index: this.supportedInstruments.findIndex(i => i.id == this.instrument.id)  /* length  */ };

        //add ui selections mapped to persistence model attributes
        for (const input of ["id", "code", "description", "type"]) {
          editInfo.data[input] = this.instrument[input];
        }
        editInfo.data.currencyId = this.currencies[this.instrument.currencyIndex].id;
        editInfo.data.entityId = this.entities[this.instrument.entityIndex].id;
        editInfo.data.countryId = this.instrumentDomiciles[this.instrument.domicileIndex].code;

        this.editAction(editInfo);
        this.cancelDialog();
      } else {
        this.cancelDialog();
        this.$router.push("/");
      }
    },
    //
    //
    makeSafeFormData() {
      //make a safe copy of the selected object. This is called before edit to avoid:
      // a) Messing with the Vuex state directly if we use the currently rendered object (rather than its safe copy)
      // b) Ensure that if a user cancels the edit mid-way, we don't have to worry about restoring a bound object

      const selectedObj = this.selectedRows[0];
      const safeCopy = this.instrument;

      for (const input of ["id", "code", "description", "type"]) {
        safeCopy[input] = selectedObj[input];
      }

      safeCopy["entityIndex"] = this.entities.findIndex(item => item.id == selectedObj.entityId);
      safeCopy["domicileIndex"] = this.instrumentDomiciles.findIndex(domicile => domicile.code == selectedObj.countryId);
      safeCopy["currencyIndex"] = this.currencies.findIndex(item => item.id == selectedObj.currencyId);
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>

