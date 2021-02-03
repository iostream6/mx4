/*
* 2021.01.26  - Created
* 2021.02.03  - Improved implementation - structure and change notifiers
*/

import 'package:flutter/foundation.dart';
import '../models/data_models.dart';
import 'package:sqflite/sqlite_api.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

import 'package:http/http.dart' as http;
import 'dart:convert';

////adb reverse tcp:8080 tcp:8080
////adb reverse --list

const String _BASE_URL =
    'http://10.0.2.2:8080'; // https://stackoverflow.com/questions/55785581 https://stackoverflow.com/questions/64444236, https://stackoverflow.com/questions/49855754

//      Enums                 //
//      Enums                 //
//      Enums                 //
//      Enums                 //

enum Method { GET, POST, PUT, DELETE }

enum AuthenticationStatus { INITIAL, REQUESTING, AUTHENTICATED, UNAUTHENTICATED }

//      ChangeNotifiers       //
//      ChangeNotifiers       //
//      ChangeNotifiers       //
//      ChangeNotifiers       //

class UserChangeManager with ChangeNotifier {
  static const String _AUTHENTICATION_URL = 'authenticate';
  AuthenticationStatus _authState = AuthenticationStatus.INITIAL;
  User _user;
  String _accessToken; //, _refreshToken;

  AuthenticationStatus get authStatus => _authState;

  static const Map<String, String> _JSON_HEADER = {'Content-Type': 'application/json'};

  User get user => _user;

  Future<Map<String, String>> authenticate({String username, String password, bool refresh}) async {
    _authState = AuthenticationStatus.REQUESTING;
    if (username == null && password == null) {
      // username and password are null - this is a signal from client for this ChangeNotifier to notify that auth is required
      notifyListeners(); // AuthenticationStatus.REQUESTING notice published
      return null;
    } else {
      //standard password authentication
      NetworkResponse response =
          await DataSource.send(_AUTHENTICATION_URL, Method.POST, data: jsonEncode({'username': username, 'password': password}), headers: _JSON_HEADER);

      if (response.success == true && response.error != null) {
        Map<String, dynamic> data = response.data;
        if (data['success']) {
          _accessToken = data['jwt'];
          final jwtPayload = json.decode(utf8.decode(base64.decode(base64.normalize(_accessToken.split('.')[1]))));
          _user = User();
          _user.id = jwtPayload['id'];
          _user.username = jwtPayload['sub'];
          _user.password = '__';
          //
          //TLDI; Get roles, etc.
          //

          //load the user preference - if any
          await _getPreferences();
          _authState = AuthenticationStatus.AUTHENTICATED;
          notifyListeners();
          return null;
        } else {
          return data; //print('Response:: \n\t${response['success']}\t${response['username-error']}\t${response['password-error']}');
        }
      } else {
        // if network based authenticate failed, try local db
        List<Map<String, dynamic>> data =
            await DataSource._readMany(DataSource._USERS_TABLE_NAME, whereString: 'username = ?', whereArgs: [username]);
        if (data.isNotEmpty) {
          _user = User.fromMap(data.first);
          _user.password = '__';
          //
          //TLDI; Get roles, etc.
          //

          //load the user preference - if any
          await _getPreferences();

          return null;
        }
        return {'error': response.error};
      }
    }
  }

  Future<void> _getPreferences() async {
    List<Map<String, dynamic>> preferenceList =
        await DataSource._readMany(DataSource._PREFERENCES_TABLE_NAME, whereString: 'userId = ?', whereArgs: [_user.id]);
    //
    if (preferenceList.isNotEmpty) {
      _user.preferences = preferenceList[0];
    }
  }

  bool isValidAccessToken() {
    if (_accessToken == null || _accessToken.split('.').length < 3) {
      return false;
    }
    //https://github.com/dart-lang/sdk/issues/39510
    final jsonDataObject = json.decode(utf8.decode(base64.decode(base64.normalize(_accessToken.split('.')[1]))));
    final DateTime exp =
        DateTime.fromMillisecondsSinceEpoch(jsonDataObject['exp'] * 1000); //JSON data from server is in seconds, we need milliseconds
    return DateTime.now().isBefore(exp);
  }

  Future<Map<String, String>> getHeaders(final bool requiresJWTAuth, final bool isJSONRequest) async {
    Map<String, String> map = {};
    map['Content-Type'] = isJSONRequest ? 'application/json' : 'text/plain';
    if (requiresJWTAuth) {
      if (isValidAccessToken()) {
        map['Authorization'] = 'Bearer $_accessToken';
      } else {
        //FIXME refresh OR reauthenticate REQUIRED
        print('FIXME refresh OR reauthenticate REQUIRED');
      }
    }
    return map;
  }
}

class EditableCollectionChangeManager<T extends Transformable> with ChangeNotifier {
  /// the collection/table name
  final String _name;
  final String _orderBy;
  final String _whereString;
  final List<dynamic> _whereArgs;
  List<T> items;
  final Function _mapper;
  final UserChangeManager ucm;

  EditableCollectionChangeManager(this._name, this._orderBy, this._whereString, this._whereArgs, this._mapper, this.ucm);

  Future<T> insert(T item, String url, final Map<String, String> headers) async {
    final NetworkResponse nr = await DataSource.send(url, Method.POST, data: item.asMap(), headers: headers);
    Map<String, dynamic> data;
    if (nr.success == true && nr.error != null) {
      //network success
      data = nr.data;
      //
      T insertedItem = _mapper(data);
      item.id = insertedItem.id;
      //
      //locally cache the downloaded data now
      DataSource._create(item, _name);
      notifyListeners();
      return item;
    } else {
      return null;
    }
  }

  void select(String url, final Map<String, String> headers) async {
    final NetworkResponse nr = await DataSource.send(url, Method.GET, headers: headers);
    List<Map<String, dynamic>> data;
    if (nr.success == true && nr.error != null) {
      //network success
      data = nr.data;
      //locally cache using transactions and batch (avoid ping-ponging)
      DataSource._cache(_name, data);
    } else {
      data = await DataSource._readMany(_name, orderByString: _orderBy, whereString: _whereString, whereArgs: _whereArgs);
    }

    items = data.map(_mapper).toList();
    notifyListeners();
  }

  void update(T item, String url,  final Map<String, String> headers, bool notify) async {
    final NetworkResponse nr = await DataSource.send(url, Method.PUT, data: item.asMap(), headers: headers);
    if (nr.success == true && nr.error != null) {
      //network success
      //
      //locally update the data now
      DataSource._update(item, _name);
      if (notify) {
        notifyListeners();
      }
    }
  }

  void delete(T item, String url, final Map<String, String> headers, bool auth) async {
    final NetworkResponse nr = await DataSource.send(url, Method.DELETE, headers: headers);
    if (nr.success == true && nr.error != null) {
      //network success
      //locally delete
      DataSource._delete(item, _name);
      notifyListeners();
    }
  }
}

//      Support Classes      //
//      Support Classes      //
//      Support Classes      //
//      Support Classes      //

class DataSource {
  static const _DATABASE_NAME = 'fx_mx4.sfd';
  static const _NOT_OPENED_ERROR_MESSAGE = 'Database should be opened before issuing queries!';

  static bool _opened = false;
  static Database _conn;

  static String _buildCreateTableQuery(String tableName, Map<String, String> fieldsMap) {
    String query = 'CREATE TABLE IF NOT EXISTS $tableName (';
    fieldsMap.forEach((columnName, columnType) {
      //print('$column : $field');
      query += '$columnName $columnType,';
    });
    query = query.substring(0, query.length - 1);
    query += ' )';
    print('CREATE TABLE QUERY:: $query');
    return query;
  }

  static Future<NetworkResponse> send(String url, Method method, {dynamic data, Map<String, String> headers}) async {
    NetworkResponse nr = NetworkResponse();
    try {
      print('URL:: $_BASE_URL/$url');
      http.Response response;
      switch (method) {
        case Method.GET:
          response = await http.get('$_BASE_URL/$url', headers: headers);
          break;
        case Method.POST:
          response = await http.post('$_BASE_URL/$url', body: data, headers: headers);
          break;
        case Method.PUT:
          response = await http.put('$_BASE_URL/$url', body: data, headers: headers);
          break;
        case Method.DELETE:
          response = await http.delete('$_BASE_URL/$url', headers: headers);
          break;
      }
      switch (response.statusCode) {
        case 200:
          nr.data = json.decode(response.body);
          nr.success = true;
          break;
        default:
          nr.error = json.decode(response.body);
          break;
      }
    } catch (e) {
      nr.error = 'HTTP failure';
      print(e.toString());
    }
    return nr;
  }

  static Future<bool> start() async {
    if (!_opened) {
      var path = await getDatabasesPath();
      var dbPath = join(path, _DATABASE_NAME);
      // ignore: argument_type_not_assignable
      _conn = await openDatabase(dbPath, version: 1, onCreate: (Database db, int version) async {
        ////print('executing create query from onCreate callback');
        await db.execute(_buildCreateTableQuery(_BROKERS_TABLE_NAME, _BROKERS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_PORTFOLIOS_TABLE_NAME, _PORTFOLIOS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_ENTITIES_TABLE_NAME, _ENTITIES_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_TRANSACTIONS_TABLE_NAME, _TRANSACTIONS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_INSTRUMENTS_TABLE_NAME, _INSTRUMENTS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_PREFERENCES_TABLE_NAME, _PREFERENCES_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_USERS_TABLE_NAME, _USERS_TABLE_SCHEMA));
      }, onOpen: (Database db) {
        print('Local Database opened');
        _opened = true;
      });
    }
    return _opened;
  }

  static void _create(Transformable t, String table) {
    if (!_opened) {
      print(_NOT_OPENED_ERROR_MESSAGE);
      return;
    }
    _conn.insert(table, t.asMap(), conflictAlgorithm: ConflictAlgorithm.replace);
  }

  static void _update(Transformable t, String table) {
    if (!_opened) {
      print(_NOT_OPENED_ERROR_MESSAGE);
      return;
    }
    _conn.update(table, t.asMap(), where: 'id = ?', whereArgs: [t.id]);
  }

  static Future<List<Map<String, dynamic>>> _readMany(String table, {String orderByString, String whereString, List<dynamic> whereArgs}) async {
    if (!_opened) {
      print(_NOT_OPENED_ERROR_MESSAGE);
      return [];
    }
    List<Map<String, dynamic>> data = await _conn.query(table, orderBy: orderByString, where: whereString, whereArgs: whereArgs);
    return data;
  }

  static void _delete(Transformable t, String table) {
    if (_opened && t?.id != null) {
      try {
        _conn.delete(table, where: "id = ?", whereArgs: [t.id]);
      } catch (Error) {
        print("Error deleting ${t.id}: ${Error.toString()}");
      }
    }
  }

  static Future<bool> _cache(String table, List<Map<String, dynamic>> items) async {
    if (_opened) {
      try {
        await _conn.transaction((txn) async {
          var batch = txn.batch();
          batch.delete(table, where: "id > ?", whereArgs: [0]);
          for (Map<String, dynamic> item in items) {
            batch.insert(table, item, conflictAlgorithm: ConflictAlgorithm.replace); //TODO Any need to delete first?
          }
          await batch.commit(noResult: true);
        });
        return true;
      } catch (Error) {
        print("Error cache to $table}");
      }
    } else {
      print(_NOT_OPENED_ERROR_MESSAGE);
    }
    return false;
  }

  static const String _BROKERS_TABLE_NAME = 'brokers', _PORTFOLIOS_TABLE_NAME = 'portfolios';
  static const String _ENTITIES_TABLE_NAME = 'entities', _INSTRUMENTS_TABLE_NAME = 'instruments';
  static const String _TRANSACTIONS_TABLE_NAME = 'transactions', _PREFERENCES_TABLE_NAME = 'preferences';
  static const String _USERS_TABLE_NAME = 'users';

  static const _USERS_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'username': 'TEXT',
    'email': 'TEXT',
    'userCode': 'TEXT',
    'password': 'TEXT',
  };

  static const _BROKERS_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'name': 'TEXT',
    'userId': 'TEXT',
  };

  static const _PORTFOLIOS_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'name': 'TEXT',
    'code': 'TEXT',
    'brokerId': 'TEXT',
  };

  static const _ENTITIES_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'name': 'TEXT',
    'description': 'TEXT',
    'sectorId': 'TEXT',
  };

  static const _INSTRUMENTS_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'code': 'TEXT',
    'description': 'TEXT',
    'countryId': 'TEXT',
    'entityId': 'TEXT',
    'active': 'TEXT',
    'currencyId': 'INTEGER',
    'type': 'TEXT'
  };

  static const _TRANSACTIONS_TABLE_SCHEMA = {
    'id': 'TEXT PRIMARY KEY',
    'instrumentId': 'TEXT',
    'portfolioId': 'TEXT',
    'currencyId': 'INTEGER',
    'units': 'REAL',
    'fees': 'REAL',
    'taxes': 'REAL',
    'rate': 'REAL',
    'type': 'TEXT',
    'date': 'TEXT',
    'provisional': 'TEXT',
  };

  static const _PREFERENCES_TABLE_SCHEMA = {
    'userId': 'TEXT PRIMARY KEY',
    'currencyPref': 'INTEGER',
    'provisionalsPref': 'INTEGER',
    'accessPref': 'TEXT',
    'refreshPref': 'TEXT',
  };
}

class NetworkResponse {
  var data;
  String error;
  bool success = false;
}
