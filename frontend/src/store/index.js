/**
 * 2020.05.23  - Added support for authetication persistence, portfolio CRUD
 * 2020.05.30  - Simplified/centralised add/edit/delete actions for domain objects
 * 2020.05.31  - Added centralized logout action that supports current backend endpoint defn
 * 2020.06.04  - Added frontend cookie based refresh token support and centralized functions for enforcing/restoring authorization
 *               Temporarily commented out BasicAdminData data retrieval routines.
 * 2020.06.07  - Introduced centralized getAction method which knows how to READ/get stuff from server with optional injection into shared state
 *               Transactions now retrieved via getAction 
 * 2020.06.08  - Transaction retrieval now merged with basic data retrieval and placed in shared state
 * 2020.06.12  - Fixed delete transaction frontend state update - only entries with the right indices are now removed by starting from back to front
 * 2020.06.13  - Added centralized state info for displayFormat and fx rates client download and usage.
 * 2020.06.29  - Added centralized Values data download
 */

import Vue from 'vue'
import Vuex from 'vuex'

import Axios from "axios";

//import AuthModule from "./auth";

Vue.use(Vuex)

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
    accessToken: null,
    brokers: [],
    portfolios: [],
    currencies: [],
    entities: [],
    fxr: [],
    transactions: [],
    values: [],
    isBasicDataGotten: false,
    isBasicAdminDataGotten: false,
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
    displayFormats: [
      { name: "UK", locale: "en-GB", baseCurrencyId: 1001103, currency: "GBP" },
      { name: "US", locale: "en-US", baseCurrencyId: 1001101, currency: "USD" }],
    formmaterIndex: 0,
    sectors: [],
    supportedInstruments: [],
  },
  //
  //
  //
  getters: {
    getAuthenticatedAxios(state) {
      return Axios.create({
        headers: { Authorization: `Bearer ${state.accessToken}` }
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
        if (state.accessToken == null || state.accessToken.split('.').length < 3) {
          return false;
        }
        const jwtData = JSON.parse(atob(state.accessToken.split('.')[1]));
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
      state.accessToken = authInfo.jwt;

      console.log(`setAuth JWT:${state.accessToken}`)

      //Get the other user info from JWT
      const jwtData = JSON.parse(atob(state.accessToken.split('.')[1]));
      state.user.userId = jwtData.id;
      state.user.usercode = jwtData.usercode;
      state.user.username = jwtData.sub;
      const userRoles = jwtData.roles;
      state.isAdminUser = (userRoles.indexOf(ADMIN_ROLE) > -1);
      console.log("set Authenticated Called and passed");
      state.authenticated = true;
    },
    //
    //
    clearAuthentication(state) {
      state.accessToken = null;
      state.authenticated = false;
      state.user.username = null;
      state.user.userId = null;
      state.user.usercode = null;
    },
    //
    //
    setAddedObject(state, objectInfo) {
      state[objectInfo.list].push(objectInfo.data);
    },
    //
    //
    setReadObjects(state, objectInfo) {
      const list = state[objectInfo.list];
      if (objectInfo.replace) {
        list.splice(0, list.length, ...objectInfo.data);
      } else {
        //append
        list.push(...objectInfo.data);
      }
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
    setRemovedObjects(state, objectInfo) {
      //used to specify delete a list of  contiguous objects, starting at a given index
      const list = state[objectInfo.list];
      if (objectInfo.index != null && objectInfo.length != null) {
        list.splice(objectInfo.index, objectInfo.length);
      }
      //used to specify a list of objects, by their indexes, indexes should be in order
      // as delete is back to front to preserve for earlier indexes so that delete is accurate
      if (objectInfo.indexes != null) {
        let i = objectInfo.indexes.length - 1;
        for (; i > -1; i--) {
          list.splice(objectInfo.indexes[i], 1);
        }
      }
    },
    //
    // 
    setBasicData(state, data) {
      state.currencies = data["currencies"];
      state.brokers = data["brokers"];
      state.portfolios = data["portfolios"];
      state.entities = data["entities"];
      state.sectors = data["sectors"];
      state.supportedInstruments = data["instruments"];
      state.transactions = data["transactions"];
      state.fxr = data["fxr"],
      state.values = data["values"],
      state.isBasicDataGotten = true;
    },
    // //
    // //
    // setBasicAdminData(state, data) {

    //   state.isBasicAdminDataGotten = true;
    // }
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
     * Signout from the application.
     * @param {*} context the required vuex context
     */
    async signoutAction(context) {
      try {
        // Create and use custom Axios instance with the right content-type (so the string is clean at the server,
        // else Axios treats its as application/x-www-form-urlencoded and it gets = at the server end
        const axiosResponse = await Axios.create({
          headers: {
            Authorization: `Bearer ${context.state.accessToken}`,
            'Content-Type': 'text/plain'
          }
        }).post(
          `${context.state.server}/signout`, context.state.accessToken
        );
        if (axiosResponse.data["success"] == true) {
          context.commit("clearAuthentication");
          return true;
        }
      } catch (error) {
        //   TODO - handle error 
      }
      return false;
    },
    /**
     * Sends Axios request to backend API to add a new domain object entry
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
          requestInfo.success = true;
        }
      } catch (error) {
        requestInfo.success = false;
      }
      if (requestInfo.list != null && requestInfo.success == true) {
        context.commit("setAddedObject", requestInfo);
      }
      return requestInfo;
    },
    /**
     * Sends Axios request to backend API to retrieve a collection of domain objects
     * @param {*} context the required vuex context
     * @param {*} requestInfo a JSON object containing url, and other relevant processing information
     */
    async getAction(context, requestInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}${requestInfo.url}`);
        if (axiosResponse.status == 200) {
          requestInfo.data = axiosResponse.data;
          requestInfo.success = true;
        }
      } catch (error) {
        requestInfo.data = [];
        requestInfo.success = false;
      }
      if (requestInfo.list != null && requestInfo.success == true) {
        context.commit("setReadObjects", requestInfo);
      }
      return requestInfo.data;
    },
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
          requestInfo.success = true;
        }
      } catch (error) {
        requestInfo.success = false;
      }
      if (requestInfo.list != null && requestInfo.success == true) {
        context.commit("setEditedObject", requestInfo);
      }
      return requestInfo.success;
    },
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
          requestInfo.success = true;
        }
      } catch (error) {
        requestInfo.success = false;
      }
      if (requestInfo.list != null && requestInfo.success == true) {
        context.commit("setRemovedObjects", requestInfo);
      }
      return requestInfo.success;
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

        //entities
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
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
          `${context.state.server}/api/instruments`
        );
        if (axiosResponse.status == 200) {
          results["instruments"] = axiosResponse.data;
        }

        //exchange rates
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/exchanges`
        );
        if (axiosResponse.status == 200) {
          results["fxr"] = axiosResponse.data;
        }

        //transactions
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/transactions/${context.state.user.userId}`
        );
        if (axiosResponse.status == 200) {
          results["transactions"] = [];
          for (const t of axiosResponse.data) {
            const item = {};
            for (const input of ["id", "type", "units", "amountPerUnit", "fees", "taxes", "provisional"]) {
              item[input] = t[input];
            }
            item.date = new Date(t.date);
            //the following text attributes are helpful/needed in table filtering, hence they are used
            item.portfolioCode = results["portfolios"].find(item => item.id == t.portfolioId).code;
            item.currencyCode = results["currencies"].find(item => item.id == t.currencyId).code;
            item.instrumentCode = results["instruments"].find(item => item.id == t.instrumentId).code;
            //
            results["transactions"].push(item);
          }
        }

        //instrument values
        axiosResponse = await context.getters.getAuthenticatedAxios.get(
          `${context.state.server}/api/values/last`
        );
        if (axiosResponse.status == 200) {
          results["values"] = axiosResponse.data;
        }

        if (results["brokers"] && results["portfolios"] && results["currencies"] && results["sectors"] && results["entities"]
          && results["instruments"] && results["fxr"] && results["transactions"] && results["values"]) {
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
    // /**
    // * Sends Axios request to backend API to get a list of brokers and portfolios relevant to the current 
    // * user as well as supported currencies
    // * @param {*} context the required vuex context
    // */
    // async getBasicAdminDataAction(context) {
    //   try {
    //     // let results = {};
    //     // //entities
    //     // let axiosResponse = await context.getters.getAuthenticatedAxios.get(
    //     //   `${context.state.server}/api/entities`
    //     // );
    //     // if (axiosResponse.status == 200) {
    //     //   results["entities"] = axiosResponse.data;
    //     // }
    //     // //sectors
    //     // axiosResponse = await context.getters.getAuthenticatedAxios.get(
    //     //   `${context.state.server}/api/sectors`
    //     // );
    //     // if (axiosResponse.status == 200) {
    //     //   results["sectors"] = axiosResponse.data;
    //     // }
    //     // // instruments
    //     // axiosResponse = await context.getters.getAuthenticatedAxios.get(
    //     //   `${context.state.server}/admin/instruments`
    //     // );
    //     // if (axiosResponse.status == 200) {
    //     //   results["instruments"] = axiosResponse.data;
    //     // }
    //     // if (results["sectors"] && results["entities"] && results["instruments"]) {
    //     //   context.commit("setBasicAdminData", results);
    //     // }

    //   } catch (error) {
    //     //    TODO - handle error
    //     if (error.response) {
    //       // console.log(error.response.data);
    //     }
    //   }
    // },
    /**
     * 
     * @param {*} context 
     */
    async ensureAuthorized(context) {
      const currentDate = new Date();
      if (context.getters.isJWTValid(currentDate)) {
        console.log("Ensured authorize without refresh call");
      } else {
        //attempt to obtain access token using refresh token in httpOnly cookie
        console.log("Attempting refresh . . . ");
        try {
          // Create and use custom Axios instance with the right content-type, also include the withCredentials
          // property so that the subsystems knows to send any unexpired refresh token stored in cookie
          const axiosResponse = await Axios.create({
            withCredentials: true,
            headers: {
              'Content-Type': 'text/plain'
            }
          }).post(
            `${context.state.server}/refresh`
          );
          if (axiosResponse.data["success"] == true) {
            const authInfo = {
              jwt: axiosResponse.data.jwt
            };
            context.commit("setAuthenticated", authInfo);
          }
        } catch (error) {
          //TODO handle error
        }
        //after refresh attempt, check if valid auth
        if (!context.getters.isJWTValid(currentDate)) {
          context.commit("clearAuthentication");
          console.log("Ensured authorize FAILED refresh call");
        } else {
          console.log("Ensured authorize WITH refresh call");
        }
      }
    },
  }

})
