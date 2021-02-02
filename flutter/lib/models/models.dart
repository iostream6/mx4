// /*
//  * 2020.12.26  - Created 
//  * 
//  */

// import 'package:flutter/material.dart';
// import '../services/http_services.dart' as services;

// import 'dart:convert';

// enum UserState { initial, authenticating, authenticated, unauthenticated }

// // class AuthenticationDetailsManager with ChangeNotifier {
// //   UserState _userState = UserState.initial;
// //   User _user;
// //   String _access;
// //   //String _refresh;

// //   UserState get userState => _userState;

// //   User get user => _user;

// //   Map<String, String> get accessHeader => {'Authorization': 'Bearer $_access'};

// //   void authenticate({String username, String password, bool refresh}) {
// //     _userState = UserState.authenticating;

// //     if (refresh != null && refresh == true) {
// //       // jwt refresh
// //       //notifyListeners();
// //     } else {
// //       if (username == null && password == null) {
// //         //view requires authentication
// //         //only state change notification publish
// //         notifyListeners();
// //       } else {
// //         // standard password authentication
// //         // do standard auth then notification publish

// //         //services.post('authenticate', headers: {'Authorization': 'Bearer $_access', 'Content-Type': 'application/json'}, data: {'username': username, 'password': password}).then((response) {
// //         services.post('authenticate', headers: {'Content-Type': 'application/json'}, data: jsonEncode({'username': username, 'password': password})).then((service) {
// //           if(service.error == null){
// //             //http service call returned something from server - lets check that out
// //             //
// //             var response = service.data as Map;
// //             if(response['success']){
// //               _userState = UserState.authenticated;
// //                _user = User();
// //                _user.username = username;
// //                _access = response['jwt'];
// //                print('Testing validity:: isValid=${hasValidAccessToken()}');
              
// //               //

// //                notifyListeners();
// //             }else{
// //               print('Response:: \n\t${response['success']}\t${response['username-error']}\t${response['password-error']}');
// //             }
// //           }else{
// //             //service call did not work out
// //             print('Error encountered\n${service.error}');
// //           }
// //         });
        
// //       }
// //     }

// //   }

// //   bool hasValidAccessToken() {
// //     if (_access == null || _access.split('.').length < 3) {
// //       return false;
// //     }
// //     //https://github.com/dart-lang/sdk/issues/39510
// //     final jsonDataObject = json.decode(utf8.decode(base64.decode(base64.normalize(_access.split('.')[1]))));
// //     final DateTime exp = DateTime.fromMillisecondsSinceEpoch(jsonDataObject['exp'] * 1000); //JSON data from server is in seconds, we need milliseconds

// //     return DateTime.now().isBefore(exp);
// //   }
// // }

// class User {
//   String username;
//   String email;
//   String userCode;
//   String password;
// }