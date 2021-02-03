/*
 * 2020.12.26  - Created 
 * 2021.02.03  - Changed to stateful widget and used form support
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/data_service.dart';

class LoginView extends StatefulWidget {
  @override
  _LoginViewState createState() => _LoginViewState();
}

class _LoginViewState extends State<LoginView> {
  final _formKey = GlobalKey<FormState>();
  final _userNameTextController = TextEditingController();
  final _passwordTextController = TextEditingController();
  final TextStyle _style = TextStyle(fontFamily: 'Montserrat', fontSize: 20.0);

  String _errorMessage;

  bool _autovalidate = false; //if true, form is validated after each change.

  Widget _buildErrorTextWidget(String message) => message == null ? Text("") : Text(message, style: TextStyle(color: Colors.red));

  Widget _buildUsernameTextFieldWidget() => TextFormField(
        style: _style,
        controller: _userNameTextController,
        decoration: InputDecoration(
            contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
            hintText: "Username",
            prefixIcon: Icon(Icons.person),
            border: OutlineInputBorder(borderRadius: BorderRadius.horizontal())),
        validator: (value) {
          if (value.isEmpty) {
            return 'Username is required';
          }
          return null;
        },
        onChanged: (String val) {
          setState(() => _errorMessage = null);
        },
      );

  Widget _buildPasswordTextFieldWidget() => TextFormField(
        obscureText: true,
        style: _style,
        controller: _passwordTextController,
        decoration: InputDecoration(
            contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
            hintText: "Password",
            prefixIcon: Icon(Icons.vpn_key),
            border: OutlineInputBorder(borderRadius: BorderRadius.horizontal())),
        validator: (value) {
          if (value.isEmpty) {
            return 'Password is required';
          }
          return null;
        },
        onChanged: (String val) {
          setState(() => _errorMessage = null);
        },
      );

  Widget _buildLoginButtonWidget() => Material(
        elevation: 5.0,
        color: Colors.blueAccent[400],
        child: MaterialButton(
          minWidth: MediaQuery.of(context).size.width,
          padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          onPressed: () {
            if (_formKey.currentState.validate()) {
              Provider.of<UserChangeManager>(context, listen: false)
                  .authenticate(username: _userNameTextController.text, password: _passwordTextController.text)
                  .then((Map<String, String> res) {
                if (res != null) {
                  //login did not work!! - show error on the view
                  setState(() => _errorMessage = 'Login failed. Please check your login details.');
                } //else{}  If succeed, the changes will automatically be notified and the correct view will appear
              });
            } else {
              //submissiom attempt has failed, so we want the form to validate on each change,
              //rather than wait till next submit, so that the user has realtime feedback on whether issues are fixed
              setState(() {
                _autovalidate = true;
              });
            }
          },
          child: Text("Sign-In", textAlign: TextAlign.center, style: _style.copyWith(color: Colors.white, fontWeight: FontWeight.bold)),
        ),
      );

  @override
  void dispose() {
    _passwordTextController.dispose();
    _userNameTextController.dispose();
    super.dispose();
  }

  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      //appBar: AppBar(),
      body: Form(
        autovalidateMode: _autovalidate ? AutovalidateMode.onUserInteraction : AutovalidateMode.disabled,
        key: _formKey,
        child: SingleChildScrollView(
            padding: EdgeInsets.all(20.0),
            child: Column(crossAxisAlignment: CrossAxisAlignment.center, mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
              SizedBox(height: 25.0),
              _buildUsernameTextFieldWidget(),
              SizedBox(height: 25.0),
              _buildPasswordTextFieldWidget(),
              SizedBox(
                height: 25.0,
              ),
              _buildLoginButtonWidget(),
              SizedBox(
                height: 10.0,
              ),
              _buildErrorTextWidget(_errorMessage)
            ])),
      ),
    );
  }
}