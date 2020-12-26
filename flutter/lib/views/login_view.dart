/*
 * 2020.12.26  - Created 
 * 
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/models.dart';

class LoginView extends StatelessWidget {
  final _userNameTextController = TextEditingController();
  final _passwordTextController = TextEditingController();

  final TextStyle _style = TextStyle(fontFamily: 'Montserrat', fontSize: 20.0);

  @override
  Widget build(BuildContext context) {
    final userField = TextField(
      style: _style,
      controller: _userNameTextController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Username",
          border: OutlineInputBorder(borderRadius: BorderRadius.horizontal())),
    );

    final passwordField = TextField(
      obscureText: true,
      style: _style,
      controller: _passwordTextController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Password",
          border: OutlineInputBorder(borderRadius: BorderRadius.horizontal())),
    );

    final loginButon = Material(
      elevation: 5.0,
      color: Colors.blueAccent[400],
      child: MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        onPressed: () {
          Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate(username:_userNameTextController.text, password: _passwordTextController.text);
          //this above is like context.read<UserChangeNotifier>().authenticate(...); but can be used inside build method
          // If succeed or fail, the changes will automatically be notified and the correct view will appear - hence the below is commented out
          //Navigator.pop(context);
        },
        child: Text("Sign-In",
            textAlign: TextAlign.center, style: _style.copyWith(color: Colors.white, fontWeight: FontWeight.bold)),
      ),
    );

    return Scaffold(
        body: Center(
            child: Container(
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            SizedBox(height: 25.0),
            userField,
            SizedBox(height: 25.0),
            passwordField,
            SizedBox(
              height: 25.0,
            ),
            loginButon,
            SizedBox(
              height: 25.0,
            ),
          ],
        ),
      ),
    )));
  }
}
