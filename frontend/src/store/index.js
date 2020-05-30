/**
 * 2020.05.23  - Added support for authetication persistence, portfolio CRUD
 * 2020.05.30  - Simplified/centralised add/edit/delete actions for domain objects
 */

import Vue from 'vue'
import Vuex from 'vuex'

import Axios from "axios";

//import AuthModule from "./auth";

Vue.use(Vuex)

const AUTH_STORAGE_KEY = "xtinfo";
const ADMIN_ROLE = "ADMIN";

export default new Vuex.Store({
  strict: true,
  //
  //
  //
  state: {
    //message: "My error Message",
    user: { username: null, userId: null, usercode: null },
    server: 'http://localhost:8080',
    jwt: null,
    brokers: [],
    portfolios: [],
    currencies: [],
    transactions: [],
    isBasicDataGotten: false,
    isBasicAdminDataGotten: false,
    isTransactionsGotten: false,
    isAdminUser: false,
    authenticated: false,
    transactionTypes: ['DIV', 'BUY', 'SELL'],
    instrumentTypes: ["STOCK", "CASH", "BOND", "DEBT"],
    instrumentDomiciles: [{ "code": "USA", "name": "United States" },
    { "code": "UK", "name": "United Kingdom" },
    { "code": "FRA", "name": "France" },
    { "code": "SPA", "name": "Spain" },
    { "code": "NGR", "name": "Nigeria" },
    { "code": "CHN", "name": "China" }],
    sectors: [],
    supportedInstruments: [],
  },
  //
  //
  //
  getters: {
    getAuthenticatedAxios(state) {
      return Axios.create({
        headers: { Authorization: `Bearer ${state.jwt}` }
      });
    },
    //
    //
    //
    /**
     * Returns a function which callers can use to validate that the client has a valid JWT
     * @param {*} state the vuex state
     */
    isJWTValid(state) {
      return function (currentTime) {
        if (state.jwt == null || state.jwt.split('.').length < 3) {
          return false;
        }
        const jwtData = JSON.parse(atob(state.jwt.split('.')[1]));
        const exp = new Date(jwtData.exp * 1000); //converted to milliseconds
        console.log(`Lamda Local authentication check:: ${currentTime < exp}`);
        return currentTime < exp;
      };
    }
  },
  //
  //
  //
  mutations: {
    //
    //
    //
    setAuthenticated(state, authInfo) {
      state.jwt = authInfo.jwt;
      state.authenticated = true;

      //Get the other user info from JWT
      const jwtData = JSON.parse(atob(state.jwt.split('.')[1]));
      state.user.userId = jwtData.id;
      state.user.usercode = jwtData.usercode;
      state.user.username = jwtData.sub;

      const userRoles = jwtData.roles;

      state.isAdminUser = (userRoles.indexOf(ADMIN_ROLE) > -1);

      console.log("set Authenticated Called and passed");

      if (authInfo.persist) {
        localStorage.setItem(AUTH_STORAGE_KEY, authInfo.jwt);
        console.log("setAuthenticated persisted info to local store");
      }

    },
    //
    //
    clearAuthentication(state) {
      state.jwt = null;
      state.authenticated = false;
      state.user.username = null;
    },
    //
    //
    setAddedObject(state, objectInfo) {
      state[objectInfo.list].push(objectInfo.data);
    },
    //
    //
    setEditedObject(state, objectInfo) {
      const item = state[objectInfo.list][objectInfo.index];
      // todo use spreader
      var x;
      for (x in objectInfo.data) {
        item[x] = objectInfo.data[x];
      }
    },
    //
    //
    setRemovedObject(state, objectInfo) {
      state[objectInfo.list].splice(objectInfo.index, 1);
    },
    //
    // 
    setBasicData(state, data) {
      state.currencies = data["currencies"];
      state.brokers = data["brokers"];
      state.portfolios = data["portfolios"];
      state.isBasicDataGotten = true;
    },
    //
    //
    setBasicAdminData(state, data) {
      state.entities = data["entities"];
      state.sectors = data["sectors"];
      state.supportedInstruments = data["instruments"];
      state.isBasicAdminDataGotten = true;
    }
    //
  },
  //
  //
  //
  actions: {
    //
    //
    //
    /**
     * Reloads key application states from local (client-side) storage.
     * @param {*} context the required vuex context
     */
    async reloadStateAction(context) {
      if (context.state.jwt == null) {
        //AUTHENTICATION STATE INFO
        const authInfo = {
          jwt: localStorage.getItem(AUTH_STORAGE_KEY),
          persist: false
        };
        if (authInfo.jwt != null) {
          const currentTime = new Date();
          const jwtData = JSON.parse(atob(authInfo.jwt.split('.')[1]));
          const exp = new Date(jwtData.exp * 1000); //converted to milliseconds
          const valid = currentTime < exp;
          if (valid) {
            context.commit("setAuthenticated", authInfo);
          } else {
            //TODO attempt to ask for refresh token
          }
        }
        //OTHER STATES
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to create a new domain object entry
     * @param {*} context the required vuex context
     * @param {*} requestInfo a JSON object containing domain object infomation (ex id), the create URL and the state list name for mutation
     */
    async addAction(context, requestInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.post(
          `${context.state.server}${requestInfo.url}`, requestInfo.data
        );
        if (axiosResponse.status == 200) {
          requestInfo.data = axiosResponse.data;// the server add id field, and may append others as well (e.g. PortfolioUsers) 
          context.commit("setAddedObject", requestInfo);
        }
      } catch (error) {
        //   TODO - handle error 
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to edit an existing domain object
     * @param {*} context the required vuex context
     * @param {*} requestInfo a JSON object containing domain object infomation, the state list name for mutation and an index field representing the index on the list
     */
    async editAction(context, requestInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.put(
          `${context.state.server}${requestInfo.url}`, requestInfo.data
        );
        if (axiosResponse.status == 200) {
          context.commit("setEditedObject", requestInfo);
        }
      } catch (error) {
        //    TODO - handle error
        if (error.response) {
          // console.log(error.response.data);
        }
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to delete an existing domain object
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing the domain object infomation, the state list name for mutation and an index field representing the index on the list
     */
    async deleteAction(context, requestInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.delete(
          // HTTP DELETE body not widely supported, etc. see //https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request and https://github.com/axios/axios/issues/897
          `${context.state.server}${requestInfo.url}`
        );
        if (axiosResponse.status == 200) {
          context.commit("setRemovedObject", requestInfo);
        }
      } catch (error) {
        //    TODO - handle error
        if (error.response) {
          // console.log(error.response.data);
        }
      }
    },


    /**
     * Sends Axios request to backend API to get a list of brokers and portfolios relevant to the current 
     * user as well as supported currencies
     * @param {*} context the required vuex context
     */
    async getBasicDataAction(context) {
      try {
        let results = {};
        //brokers
        let axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/brokers/${context.state.user.userId}`
        );
        if (axiosResponse.status == 200) {
          results["brokers"] = axiosResponse.data;
        }
        //portfolios
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/portfolios/${context.state.user.userId}`
        );
        if (axiosResponse.status == 200) {
          results["portfolios"] = axiosResponse.data;
        }
        // currencies
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/currencies`
        );
        if (axiosResponse.status == 200) {
          results["currencies"] = axiosResponse.data;
        }
        if (results["brokers"] && results["portfolios"] && results["currencies"]) {
          context.commit("setBasicData", results);
        }
      } catch (error) {
        //    TODO - handle error
        if (error.response) {
          // console.log(error.response.data);
          // console.log(error.response.status);
          // console.log(error.response.headers);
        }
      }
    },
    /**
    * Sends Axios request to backend API to get a list of brokers and portfolios relevant to the current 
    * user as well as supported currencies
    * @param {*} context the required vuex context
    */
    async getBasicAdminDataAction(context) {
      try {
        let results = {};
        //entities
        let axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/entities`
        );
        if (axiosResponse.status == 200) {
          results["entities"] = axiosResponse.data;
        }
        //sectors
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/sectors`
        );
        if (axiosResponse.status == 200) {
          results["sectors"] = axiosResponse.data;
        }
        // instruments
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/admin/instruments`
        );
        if (axiosResponse.status == 200) {
          results["instruments"] = axiosResponse.data;
        }
        if (results["sectors"] && results["entities"] && results["instruments"]) {
          context.commit("setBasicAdminData", results);
        }

      } catch (error) {
        //    TODO - handle error
        if (error.response) {
          // console.log(error.response.data);
        }
      }
    },
    /**
     * 
     * @param {*} context 
     */
    async ensureAuthorized(context) {
      const currentDate = new Date();
      if (!context.getters.isJWTValid(currentDate)) {
        //todo ATEMPT TO REFRESH TOKEN
        //
        //
        //
        //
        //
        //
        //if after refresh attempt, still not valid? Force signin
        if (!context.getters.isJWTValid(currentDate)) {
          context.commit("clearAuthentication");
        }
      }
    },
  }

})
