<template>
  <div>
    <div>
      <div id="logreg-forms">
        <!-- Container for the main signup form -->
        <!-- We combine built-in HTML5 validation (for client-side) and bootstrap validation styling (for servers-side) validation -->
        <div v-if="formname==='signin'">
          <form v-on:submit="handleAuth">
            <!-- The event hanler is attached to the form, rather than the submit button (@onclick) event, allowing the inbuit HTML5 attribute based validation to take effect-->
            <!-- Basically, the onclick event bubbles up to the form and triggers validation on the form - if it passes, the submit event is triggers-->
            <div class="form-group">
              <h1 class="h3 mb-3 font-weight-normal" style="text-align: center">Sign in</h1>
              <input type="text" class="form-control" v-bind:class="{'is-invalid': response['username-error']}" v-model="userdata.username" placeholder="Username" required autofocus />
              <div class="invalid-feedback">{{response['username-error']}}</div>
            </div>
            <div class="form-group">
              <input type="password" class="form-control" v-bind:class="{'is-invalid': response['password-error']}" v-model="userdata.password" placeholder="Password" required />
              <div class="invalid-feedback">{{response['password-error']}}</div>
            </div>
            <button class="btn btn-primary btn-block" type="submit">
              <i class="fa fa-sign-in"></i> Sign in
            </button>
            <a href="#" v-on:click.capture="formname='reset'">Forgot password?</a>
            <hr />
            <!-- <p>Don't have an account!</p>  -->
            <button class="btn btn-primary btn-block" type="button" v-on:click="formname='signup'">
              <i class="fa fa-user-plus"></i> Sign up New Account
            </button>
          </form>
        </div>

        <!-- Container for the password reset form - hidden by default-->
        <div v-if="formname==='reset'">
          <form>
            <div class="form-group">
              <input type="email" class="form-control" id="inputEmail" placeholder="Email address" required autofocus />
            </div>
            <button class="btn btn-primary btn-block" type="submit">Reset Password</button>
            <a href="#" v-on:click="formname='signin'">
              <i class="fa fa-angle-left"></i> Back
            </a>
          </form>
        </div>

        <!-- Container for the Signup form - hidden by default-->
        <!-- We combine built-in HTML5 validation (for client-side) and bootstrap validation styling (for servers-side) validation -->
        <div v-if="formname==='signup'">
          <form v-on:submit="handleSignup">
            <!-- The event hanler is attached to the form, rather than the submit button (@onclick) event, allowing the inbuit HTML5 attribute based validation to take effect-->
            <!-- Basically, the onclick event bubbles up to the form and triggers validation on the form - if it passes, the submit event is triggers-->
            <div class="form-group">
              <input type="text" class="form-control" v-model="userdata.fullname" v-bind:class="{'is-invalid': response['fullname-error']}" placeholder="Full name" required minlength="3" autofocus />
              <div class="invalid-feedback">{{response['fullname-error']}}</div>
            </div>
            <hr />
            <div class="form-group">
              <input type="text" class="form-control" v-model="userdata.username" v-bind:class="{'is-invalid': response['username-error']}" placeholder="Username" required minlength="3" autofocus />
              <div class="invalid-feedback">{{response['username-error']}}</div>
            </div>
            <div class="form-group">
              <input type="email" class="form-control" v-model="userdata.email" v-bind:class="{'is-invalid': response['email-error']}" placeholder="Email address" required autofocus />
              <div class="invalid-feedback">{{response['email-error']}}</div>
            </div>
            <hr />

            <div class="form-group">
              <input type="password" class="form-control" v-model="userdata.password" v-bind:class="{'is-invalid': response['password-error']}" placeholder="Password" required minlength="6" />
              <div class="invalid-feedback">{{response['password-error']}}</div>
            </div>
            <div class="form-group">
              <input type="password" class="form-control" id="inputPassword2" v-model="password2" v-bind:class="{'is-invalid': response['password-error']}" placeholder="Re-enter password" required />
              <div class="invalid-feedback">{{response['password-error']}}</div>
            </div>
            <hr />
            <button class="btn btn-primary btn-block" type="submit">
              <i class="fa fa-user-plus"></i> Sign Up
            </button>
            <a href="#" v-on:click="formname='signin'">
              <i class="fa fa-angle-left"></i> Back
            </a>
          </form>
        </div>
        <br />

        <br />
      </div>
    </div>
  </div>
</template>

<script>
import Axios from "axios";
import { mapState, mapGetters, mapMutations } from "vuex";

// TODO - use a signle view for singin/up/reset and decide which subview based on URL args

export default {
  name: "SigninView",
  computed: {
    ...mapState(["server", "user"]),
    // isJWTValid returns a function so even though computed, it is not cached
    ...mapGetters(["isJWTValid"])
  },
  data() {
    return {
      formname: "signin",
      password2: "",
      response: {
        success: true,
        "email-error": null,
        "username-error": null,
        "fullname-error": null,
        "password-error": null
      },
      userdata: { fullname: null, username: null, password: null, email: null }
    };
  },
  methods: {
    ...mapMutations(["clearAuthentication", "setAuthenticated"]),

    async handleAuth(e) {
      e.preventDefault();

      let thisInstanceObject = this; //so that we can refer to it in the lambda callback

      //clear the and results flag
      this.response = {
        success: true,
        "email-error": null,
        "username-error": null,
        "fullname-error": null,
        "password-error": null
      };
      this.clearAuthentication();
      await Axios.post(
        `${this.server}/authenticate`,
        thisInstanceObject.userdata
      )
        .then(function(axiosResponse) {
          if (axiosResponse.data.success == true) {
            //mutations can only take one additional arg, so we construct a new multi-part object to move everything in one arg
            const authInfo = {
              jwt: axiosResponse.data.jwt,
              persist: true
            };
            thisInstanceObject.setAuthenticated(authInfo);
            thisInstanceObject.$router.push("/main");
          } else {
            //login failed
            thisInstanceObject.response = axiosResponse.data;
          }
        })
        .catch(error => {
          console.log(error);
        });
    },

    async handleSignup(e) {
      e.preventDefault();
      //let thisInstanceObject = this; //so that we can refer to it in the lambda callback

      //clear the and results flag
      this.response = {
        success: true,
        code: 200,
        email: null,
        username: null,
        fullname: null,
        password: null
      };

      //perform additional custom client-side validation to ensure that the passwords match (presence and length already validated with HTML5 attributes)
      if (this.userdata.password != this.password2) {
        this.response["password"] = "Passwords do not match";
        return;
      }
      //prepare the validated data
      let signupData = {
        fullname: this.userdata.fullname,
        username: this.userdata.username,
        password: this.userdata.password,
        email: this.userdata.email
      };

      try {
        const axiosResponse = await Axios.post(
          `${this.server}/signup`,
          signupData
        );
        if (axiosResponse.data["success"]) {
          this.$router.push("/");
        } else {
          //signup failed
          this.response = axiosResponse.data;
        }
      } catch (error) {
        //todo forward to page!!!
        //console("Response !!!");
        //console.error(error);
      }
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
/* sign in FORM */
#logreg-forms {
  width: 412px;
  margin: 10vh auto;
  background-color: #f3f3f3;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
#logreg-forms form {
  width: 100%;
  max-width: 410px;
  padding: 15px;
  margin: auto;
}
#logreg-forms .form-control {
  position: relative;
  box-sizing: border-box;
  height: auto;
  padding: 10px;
  font-size: 16px;
}
#logreg-forms .form-control:focus {
  z-index: 2;
}

#logreg-forms a {
  display: block;
  padding-top: 10px;
}

#logreg-form .lines {
  width: 200px;
}

#logreg-forms button[type="submit"] {
  margin-top: 10px;
}

/* Mobile */

@media screen and (max-width: 500px) {
  #logreg-forms {
    width: 300px;
  }
}
</style>
