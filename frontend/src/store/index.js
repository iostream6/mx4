/**
 * 2020.05.23  - Added support for authetication persistence, portfolio CRUD
 */

import Vue from 'vue'
import Vuex from 'vuex'

import Axios from "axios";

//import AuthModule from "./auth";

Vue.use(Vuex)

const AUTH_STORAGE_KEY = "xtinfo";

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
    isBasicDataGotten: false,
    isTransactionsGotten: false,
    authenticated: false,
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
    },
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

      console.log("set Authenticated Called and passed");

      if (authInfo.persist) {
        localStorage.setItem(AUTH_STORAGE_KEY, authInfo.jwt);
        console.log("setAuthenticated persisted info to local store");
      }

    },
    //
    //
    //
    clearAuthentication(state) {
      state.jwt = null;
      state.authenticated = false;
      state.user.username = null;
      console.log("Cleared Auth state");
    },
    //
    //
    //
    setAddedBroker(state, data) {
      state.brokers.push(data);
    },
    //
    //
    //
    setRemovedBroker(state, index) {
      state.brokers.splice(index, 1);
    },
    //
    //
    //
    setBroker(state, data) {
      state.brokers[data.index].name = data.broker.name;
      //state.brokers[data.index].id = data.broker.id;
      //state.brokers[data.index].userId = data.broker.userId;
    },
    //
    //
    //
    setAddedPortfolio(state, data) {
      state.portfolios.push(data);
      console.log("Added !!");
      console.log(data);
    },
    //
    //
    //
    setRemovedPortfolio(state, index) {
      state.portfolios.splice(index, 1);
    },
    //
    //
    //
    setPortfolio(state, data) {
      state.portfolios[data.index].name = data.portfolio.name;
      state.portfolios[data.index].code = data.portfolio.code;
    },
    //
    //
    //
    setBasicData(state, data) {
      state.currencies = data["currencies"];
      state.brokers = data["brokers"];
      state.portfolios = data["portfolios"];
      state.isBasicDataGotten = true;
    }
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
     * 
     * @param {*} context 
     */
    async refreshTokenAction(context) {
      if (context.state.jwt == null) {
        console.log("TODO");
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to create a new broker entry
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing broker name and requesting user's userId
     */
    async addBrokerAction(context, brokerJSON) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.post(
          `${context.state.server}/api/brokers`, brokerJSON
        );
        if (axiosResponse.data["id"]) {
          brokerJSON['id'] = axiosResponse.data["id"];
          context.commit("setAddedBroker", brokerJSON);
        }
      } catch (error) {
        //   TODO - handle error
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to edit a broker entry
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing a broker field and an index field representing the index of this broker on the list
     */
    async editBrokerAction(context, brokerInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.put(
          `${context.state.server}/api/broker/${context.state.user.userId}`, brokerInfo.broker
        );
        if (axiosResponse.status == 200) {
          context.commit("setBroker", brokerInfo);
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
     * Sends Axios request to backend API to delete a broker entry
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing a broker field and an index field representing the index of this broker on the list
     */
    async deleteBrokerAction(context, brokerInfo) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.delete(
          // HTTP DELETE body not widely supported, etc. 
          //https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request
          //https://github.com/axios/axios/issues/897
          `${context.state.server}/api/broker/${context.state.user.userId}/${brokerInfo.broker.id}`
        );
        if (axiosResponse.status == 200) {
          context.commit("setRemovedBroker", brokerInfo.index);
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
     * Sends Axios request to backend API to create a new portfolio entry
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing broker name and requesting user's userId
     */
    async addPortfolioAction(context, requestData) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.post(
          `${context.state.server}/api/portfolios/${context.state.user.userId}`, requestData
        );
        if (axiosResponse.data["id"]) {
          requestData['id'] = axiosResponse.data["id"];
          requestData['users'] = axiosResponse.data["users"]
          context.commit("setAddedPortfolio", requestData);
        }
      } catch (error) {
        //   TODO - handle error
      }
    },

    //
    //
    //
    /**
     * Sends Axios request to backend API to edit a portfolio entry
     * @param {*} context the required vuex context
     * @param {*} data a JSON object containing a portfolio field and an index field representing the index of this portfolio on the list
     */
    async editPortfolioAction(context, data) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.put(
          `${context.state.server}/api/portfolio/${context.state.user.userId}`, data.portfolio
        );
        if (axiosResponse.status == 200) {
          context.commit("setPortfolio", data);
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
     * Sends Axios request to backend API to delete a portfolio entry
     * @param {*} context the required vuex context
     * @param {*} data a JSON object containing a portfolio field and an index field representing the index of this portfolio on the list
     */
    async deletePortfolioAction(context, data) {
      try {
        const axiosResponse = await context.getters.getAuthenticatedAxios.delete(
          // HTTP DELETE body not widely supported, etc. 
          //https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request
          //https://github.com/axios/axios/issues/897
          `${context.state.server}/api/portfolio/${context.state.user.userId}/${data.portfolio.id}`
        );
        if (axiosResponse.status == 200) {
          context.commit("setRemovedPortfolio", data.index);
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
          //map broker ids to friendly broker names
          let dataIndex = 0, brokerIndex;
          for (; dataIndex < axiosResponse.data.length; dataIndex++) {
            for (brokerIndex = 0; brokerIndex < results["brokers"].length; brokerIndex++) {
              if (results["brokers"][brokerIndex].id == axiosResponse.data[dataIndex].brokerId) {
                axiosResponse.data[dataIndex]['brokerName'] = results["brokers"][brokerIndex].name;
              }
            }
          }
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
  }

})
