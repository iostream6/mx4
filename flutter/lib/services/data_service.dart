/*
* 2021.01.26  - Created
*/

import 'package:flutter/foundation.dart';
import '../models/data_models.dart';
import 'package:sqflite/sqlite_api.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class RequestObject {
  Map<String, dynamic> data;
  String url;
}

class ResponseObject {
  var data;
  String error;
  bool success;
  int status;
}

class ResponseObjects {
  List<Map<String, dynamic>> data;
  String error;
  bool success;
  int status;
}

// class CollectionChangeManager<T extends Transformable> with ChangeNotifier {
//   /// the collection/table name
//   final String name;

//   //final Map<String, dynamic> queryModifiers;

//   List<T> items;

//   //CollectionChangeManager()

// }

class DataSource {
  static const _DATABASE_NAME = 'fx_pim.db';

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

  static Future<bool> start() async {
    if (!_opened) {
      var path = await getDatabasesPath();
      var dbPath = join(path, _DATABASE_NAME);
      // ignore: argument_type_not_assignable
      _conn = await openDatabase(dbPath, version: 1, onCreate: (Database db, int version) async {
        ////print('executing create query from onCreate callback');
        await db.execute(_buildCreateTableQuery(_BROKERS_TABLE_NAME, _BROKERS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_PORTFLIOS_TABLE_NAME, _PORTFLIOS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_ENTITIES_TABLE_NAME, _ENTITIES_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_TRANSACTIONS_TABLE_NAME, _TRANSACTIONS_TABLE_SCHEMA));
        await db.execute(_buildCreateTableQuery(_INSTRUMENTS_TABLE_NAME, _INSTRUMENTS_TABLE_SCHEMA));
      }, onOpen: (Database db) {
        print('Local Database opened');
        _opened = true;
      });
    }
    return _opened;
  }

  static Future<int> create(RequestObject req) async {
    return 0;
  }

  static Future<int> update(RequestObject req) async {
    return 0;
  }

  static Future<ResponseObjects> select(RequestObject req) {
    return null;
  }

  static Future<ResponseObjects> selectLocal(RequestObject req, {String orderByString, String whereString, List<dynamic> whereArgs}) async {
    ResponseObjects response = ResponseObjects();
    response.data = await _conn.query(req.url, orderBy: orderByString, where: whereString, whereArgs: whereArgs);
    response.success = true;
    response.status = 200;
    return response;
  }

  static Future<bool> delete(RequestObject req) async {
    return false;
  }

  static Future<bool> deleteLocal(RequestObject req) async {
    return false;
  }

  static const String _BROKERS_TABLE_NAME = 'brokers', _PORTFLIOS_TABLE_NAME = 'portfolios';
  static const String _ENTITIES_TABLE_NAME = 'entities', _INSTRUMENTS_TABLE_NAME = 'instruments';
  static const String _TRANSACTIONS_TABLE_NAME = 'transactions';

  static const _BROKERS_TABLE_SCHEMA = {
    'id': 'STRING PRIMARY KEY',
    'name': 'STRING',
    'userId': 'STRING',
  };

  static const _PORTFLIOS_TABLE_SCHEMA = {
    'id': 'STRING PRIMARY KEY',
    'name': 'STRING',
    'code': 'STRING',
    'brokerId': 'STRING',
  };

  static const _ENTITIES_TABLE_SCHEMA = {
    'id': 'STRING PRIMARY KEY',
    'name': 'STRING',
    'description': 'STRING',
    'sectorId': 'STRING',
  };

  static const _INSTRUMENTS_TABLE_SCHEMA = {
// 'id': 'STRING PRIMARY KEY',
//     'name': 'STRING',
//     'description': 'STRING',
//     'sectorId': 'STRING',
  };

  static const _TRANSACTIONS_TABLE_SCHEMA = {
// 'id': 'STRING PRIMARY KEY',
//     'name': 'STRING',
//     'description': 'STRING',
//     'sectorId': 'STRING',
  };
}
