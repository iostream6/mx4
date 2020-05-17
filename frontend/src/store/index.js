import Vue from 'vue'
import Vuex from 'vuex'

import Axios from "axios";

//import AuthModule from "./auth";

Vue.use(Vuex)

export default new Vuex.Store({
  strict: true,
  state: {
    //message: "My error Message",
    user: { username: null, userId: null, usercode: null },
    server: 'http://localhost:8080',
    jwt: null,
    brokers: [],
    portfolios: []
  },
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
  mutations: {
    setAuthenticated(state, authInfo) {
      state.jwt = authInfo.jwt;
      state.authenticated = true;
      state.user.username = authInfo.username;
      //Get the other user info from JWT
      const jwtData = JSON.parse(atob(state.jwt.split('.')[1]));
      state.user.userId = jwtData.id;
      state.user.usercode = jwtData.usercode;
    },
    clearAuthentication(state) {
      state.jwt = null;
      state.authenticated = false;
      state.user.username = null;
    },
    setAddedBroker(state, data) {
      state.brokers.push(data);
      //state.brokers = data;
    },
    setBrokers(state, data) {
      state.brokers = data;
    },
    setPortfolios(state, data) {
      state.portfolios = data;
    }
    // setResponseMessage(state, message) {
    //   state.message = message;
    // }
  },
  actions: {

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
          //     //todo forward to page!!!
          // if (error.response) {
          //   console.log(error.response.data);
          //   console.log(error.response.status);
          //   console.log(error.response.headers);
          // }
        }
      } else {
        //TODO go to loging screen
      }
    },
    async editBrokerAction(context, brokerJSON) {
      if (context.getters.isAuthenticated) {
        try {
          const axiosResponse = await context.getters.getAuthenticatedAxios.put(
            `${context.state.server}/api/brokers/${context.state.user.userId}/${brokerJSON.id}`
          );
           if (axiosResponse.status == 200) {
            //context.commit("setBrokers", axiosResponse.data);
          }
        } catch (error) {
          //     //todo forward to page!!!
          if (error.response) {
            // console.log(error.response.data);
            // console.log(error.response.status);
            // console.log(error.response.headers);
          }
        }
      } else {
        //TODO go to loging screen
      }
    },
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
          //     //todo forward to page!!!
          if (error.response) {
            // console.log(error.response.data);
            // console.log(error.response.status);
            // console.log(error.response.headers);
          }
        }
      } else {
        //TODO go to loging screen
      }
    },





  }

})
