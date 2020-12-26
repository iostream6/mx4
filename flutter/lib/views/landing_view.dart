/*
 * 2020.12.26  - Created 
 * 
 */

import 'package:flutter/material.dart';
import '../models/models.dart';
import 'package:provider/provider.dart';

class LandingView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'MX4 Flutter',
              style: Theme.of(context).textTheme.headline4,
            ),Text(
              'App for equity portfolios',
            ),
            RaisedButton(
              onPressed: (){
                Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate();
              },
              child: Text('Login'),
            )
          ],
        ),
      ),
      );
    
  }
}
