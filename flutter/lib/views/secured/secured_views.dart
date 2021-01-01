/*
 * 2021.01.01  - Created 
 * 
 */
import 'package:provider/provider.dart';
import 'package:flutter/material.dart';

import '../views_constants.dart';
import '../../models/models.dart';

class SecuredView extends StatefulWidget {
  @override
  _SecuredViewState createState() => _SecuredViewState();
}

class _SecuredViewState extends State<SecuredView> {
  int index = 0;

  List<BottomNavigationBarItem> buildBottomNavBarItems() {
    return [
      BottomNavigationBarItem(
          icon: ClipOval(
            child: Container(
              color: index == 0 ? AppColors.LightBlue : AppColors.Grey,
              child: Image.asset(
                "assets/images/icon_home.png",
                fit: BoxFit.scaleDown,
                width: 35.0,
                height: 35.0,
              ),
            ),
          ),
          label: 'Home'),
      BottomNavigationBarItem(
          icon: ClipOval(
            child: Container(
              color: index == 1 ? AppColors.LightBlue : AppColors.Grey,
              child: Image.asset(
                "assets/images/icon_send.png",
                fit: BoxFit.scaleDown,
                width: 35.0,
                height: 35.0,
              ),
            ),
          ),
          label: 'Transactions'),
      BottomNavigationBarItem(
          icon: ClipOval(
            child: Container(
              color: index == 2 ? AppColors.LightBlue : AppColors.Grey,
              child: Image.asset(
                "assets/images/icon_orion_user-group.png",
                fit: BoxFit.scaleDown,
                width: 35.0,
                height: 35.0,
              ),
            ),
          ),
          label: 'Brokers'),
    ];
  }

  PageController pageController = PageController(
    initialPage: 0,
    keepPage: true,
  );

  @override
  void initState() {
    super.initState();
  }

  void pageChanged(int index) {
    setState(() {
      this.index = index;
    });
  }

  void bottomTapped(int index) {
    setState(() {
      this.index = index;
      pageController.animateToPage(index, duration: Duration(milliseconds: 500), curve: Curves.ease);
    });
  }

  Widget buildPageView() {
    return PageView(
      controller: pageController,
      onPageChanged: (index) {
        pageChanged(index);
      },
      children: <Widget>[
         HomeView(),
         TrasactionsView(),
         BrokersView(),
        // RaiseMoneyView(),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: _mainAppBar(),
      body: buildPageView(),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: index,
        onTap: (int index) {
          bottomTapped(index);
        },
        type: BottomNavigationBarType.fixed,
        selectedFontSize: 12,
        unselectedFontSize: 12,
        elevation: 9,
        items: buildBottomNavBarItems(),
      ),
    );
  }

  AppBar _mainAppBar() {
    return AppBar(
      leading: Image.asset(
        'assets/images/icon_settings.png',
        color: AppColors.DarkBlue,
      ),
      title: Image.asset('assets/images/Paypal-logo-header.png', height: 25),
      centerTitle: true,
      actions: <Widget>[Image.asset('assets/images/icon_school-bell.png', color: AppColors.DarkBlue)],
      backgroundColor: Colors.transparent,
      elevation: 0.0,
    );
  }
}


class HomeView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Home View',
              style: Theme.of(context).textTheme.headline4,
            ),Text(
              'App for equity portfolios',
            ),
            RaisedButton(
              onPressed: (){
                  Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate(); //effectively log out
                  //this above is like context.read<UserChangeNotifier>().authenticate(...); but can be used inside build method
                  // If succeed or fail, the changes will automatically be notified and the correct view will appear - hence the below is commented out
                  //Navigator.pop(context);
              },
              child: Text( 'Home View'),
            )
          ],
        ),
      ),
      );
  }
}

class TrasactionsView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Transactions View',
              style: Theme.of(context).textTheme.headline4,
            ),Text(
              'App for equity portfolios',
            ),
            RaisedButton(
              onPressed: (){
                //Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate();
              },
              child: Text( 'Transactions View'),
            )
          ],
        ),
      ),
      );
  }
}

class BrokersView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Brokers View',
              style: Theme.of(context).textTheme.headline4,
            ),Text(
              'App for equity portfolios',
            ),
            RaisedButton(
              onPressed: (){
                //Provider.of<AuthenticationDetailsManager>(context, listen: false).authenticate();
              },
              child: Text( 'Brokers View'),
            )
          ],
        ),
      ),
      );
  }
}

