import Vue from 'vue'
import Vuex from 'vuex'

import Axios from "axios";

//import AuthModule from "./auth";

Vue.use(Vuex)

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
    portfolios: []
  },
  //
  //
  //
  getters: {
    getAuthenticatedAxios(state) {
      // if (this.isAuthenticated){
      return Axios.create({
        headers: { Authorization: `Bearer ${state.jwt}` }
      });
      // }else{
      //   //TODO automagically refresh auth token ans return authenticated Axios
      // }
    },
    //
    //
    //
    isAuthenticated(state) {
      if (state.jwt == null || state.jwt.split('.').length < 3) {
        return false;
      }
      const jwtData = JSON.parse(atob(state.jwt.split('.')[1]));
      const exp = new Date(jwtData.exp * 1000); //converted to milliseconds
      const now = new Date();
      return now < exp;
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
      state.user.username = authInfo.username;
      //Get the other user info from JWT
      const jwtData = JSON.parse(atob(state.jwt.split('.')[1]));
      state.user.userId = jwtData.id;
      state.user.usercode = jwtData.usercode;
    },
    //
    //
    //
    clearAuthentication(state) {
      state.jwt = null;
      state.authenticated = false;
      state.user.username = null;
    },
    //
    //
    //
    setAddedBroker(state, data) {
      state.brokers.push(data);
      //state.brokers = data;
    },
    //
    //
    //
    setRemovedBroker(state, index) {
      state.brokers.splice(index);
      //state.brokers = data;
    },
    //
    //
    //
    setBrokers(state, data) {
      state.brokers = data;
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
    setPortfolios(state, data) {
      state.portfolios = data;
    }
    // setResponseMessage(state, message) {
    //   state.message = message;
    // }
  },
  //
  //
  //
  actions: {
    //
    //
    //
    /**
     * Sends Axios request to backend API to create a new broker entry
     * @param {*} context the required vuex context
     * @param {*} brokerInfo a JSON object containing broker name and requesting user's userId
     */
    async addBrokerAction(context, brokerJSON) {
      if (context.getters.isAuthenticated) {
        try {
          const axiosResponse = await context.getters.getAuthenticatedAxios.post(
            `${context.state.server}/api/brokers`, brokerJSON
          );
          if (axiosResponse.data["id"]) {
            brokerJSON['id'] = axiosResponse.data["id"]
            //console.log(brokerJSON);

            context.commit("setAddedBroker", brokerJSON);
          }
        } catch (error) {
          //   TODO - handle error
          // if (error.response) {
          //   console.log(error.response.data);
          //   console.log(error.response.status);
          //   console.log(error.response.headers);
          // }
        }
      } else {
        // TODO - handle error
      }
    },
    //
    //
    //
    /**
     * Sends Axios request to backend API to get a list of brokers relevant to the current user
     * @param {*} context the required vuex context
     */
    async getBrokersAction(context) {
      if (context.getters.isAuthenticated) {
        try {
          const axiosResponse = await context.getters.getAuthenticatedAxios.get(
            `${context.state.server}/api/brokers/${context.state.user.userId}`
          );
          if (axiosResponse.status == 200) {
            //   brokerJSON['id'] = axiosResponse.data["id"]
            //   //console.log(brokerJSON);
            context.commit("setBrokers", axiosResponse.data);
          }
        } catch (error) {
          //    TODO - handle error
          if (error.response) {
            // console.log(error.response.data);
            // console.log(error.response.status);
            // console.log(error.response.headers);
          }
        }
      } else {
        // TODO - handle error
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
      if (context.getters.isAuthenticated) {
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
            // console.log(error.response.status);
            // console.log(error.response.headers);
          }
        }
      } else {
        // TODO - handle error
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
      if (context.getters.isAuthenticated) {
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
            // console.log(error.response.status);
            // console.log(error.response.headers);
          }
        }
      } else {
        // TODO - handle error
      }
    },





  }

})
