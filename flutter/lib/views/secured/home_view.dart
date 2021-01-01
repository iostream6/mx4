// /*
//  * 2020.12.26  - Created 
//  * 
//  */

// import 'package:flutter/material.dart';
// import '../models/models.dart';
// import 'package:provider/provider.dart';

// class DashboardView extends StatelessWidget {
//   final User user;

//   final TextStyle _style = TextStyle(fontFamily: 'Montserrat', fontSize: 20.0);

//   DashboardView({Key key, this.user}) : super(key: key);

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text('Dashboard'),
//       ),
//       body: Center(
//         // Center is a layout widget. It takes a single child and positions it
//         // in the middle of the parent.
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: <Widget>[
//             Text(
//               'Welcome :${user.username}',
//             ),
//             Text(
//               'Hey',
//               style: Theme.of(context).textTheme.headline4,
//             ),
//             //STUB logout function
//             Material(
//               elevation: 5.0,
//               color: Colors.blueAccent[400],
//               child: MaterialButton(
//                 minWidth: MediaQuery.of(context).size.width,
//                 padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
//                 onPressed: () {
//                   Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate(); //effectively log out
//                   //this above is like context.read<UserChangeNotifier>().authenticate(...); but can be used inside build method
//                   // If succeed or fail, the changes will automatically be notified and the correct view will appear - hence the below is commented out
//                   //Navigator.pop(context);
//                 },
//                 child: Text("Log out", textAlign: TextAlign.center, style: _style.copyWith(color: Colors.white, fontWeight: FontWeight.bold)),
//               ),
//             )
//           ],
//         ),
//       ),
//       // floatingActionButton: FloatingActionButton(
//       //   onPressed: _incrementCounter,
//       //   tooltip: 'Increment',
//       //   child: Icon(Icons.add),
//       // ), // This trailing comma makes auto-formatting nicer for build methods.
//     );
//   }
// }
