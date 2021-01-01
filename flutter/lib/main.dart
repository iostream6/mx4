/*
 * 2020.12.26  - Created 
 * 
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'models/models.dart';

import 'views/login_view.dart';
import 'views/landing_view.dart';
import 'views/secured/secured_views.dart';

// Inspired by 
// Learpainless:
//   https://learnpainless.com/flutter/firebase-auth-email-login-using-provider-4-flutter/ 
//   https://github.com/learnpainless/FirebaseAuthProviderFlutter
//
// Managing Flutter State using Provider
//   https://medium.com/flutter-community/managing-flutter-state-using-provider-e26c78060c26
//
// How to authenticate and login users in Flutter from a REST Api
//   https://mundanecode.com/posts/flutter-restapi-login/
//
void main() {
  //The Providers are above MyApp instead of inside it so that tests can use MyApp will mocking the providers
  //Note that the ChangeNotifierProvider itself is a widget
  runApp(ChangeNotifierProvider(
      create: (_) => AuthenticationDetailsManager(),
      child: FlutterApp())); //can also use MultiProvider here!
}

class FlutterApp extends StatelessWidget {
  static const String _title = 'Maloo Equity Manager';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: _title,
        theme: ThemeData(
          primarySwatch: Colors.blue,
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        home: _showView(context),
        debugShowCheckedModeBanner: false);
  }

  Widget _showView(BuildContext context) {
    
    switch (context.watch<AuthenticationDetailsManager>().userState) {
      case UserState.authenticating:
      case UserState.unauthenticated:
        return LoginView();
      case UserState.authenticated:
        return SecuredView(
          //call extension read method
          //https://pub.dev/packages/provider
          //user: Provider.of<AuthenticationDetailsManager>(context, listen: false).user, // context.read<UserChangeNotifier>().user,
        );
      default:
        return LandingView();
    }
  }
}