/*
 * 2021.01.26  - Created 
 * 
 */

abstract class Transformable {
  Map<String, dynamic> asMap();

  // @override
  // String toString() {
  //   return asMap.toString();
  // }

  String id;

  Transformable._(this.id);
}

class User extends Transformable {
  String username;
  String email;
  String userCode;
  String password;
  Map<String, dynamic> preferences;

  User([id, this.username, this.email, this.userCode, this.password]) : super._(id);

  User.fromMap(Map<String, dynamic> map) : this(map['id'], map['username'], map['email'], map['userCode'], map['password']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {'username': username, 'email': email, 'userCode': userCode, 'password': password};
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

class Broker extends Transformable {
  String name;
  String userId;

  Broker([id, this.name, this.userId]) : super._(id);

  Broker.fromMap(Map<String, dynamic> map) : this(map['id'], map['name'], map['userId']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {
      'name': name,
      'userId': userId,
    };
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

class PortfolioUser extends Transformable {
  String portfolioID;
  String userId;
  bool read, write;
  double stake;

  PortfolioUser([id, this.portfolioID, this.userId, this.read, this.write, this.stake]) : super._(id);

  PortfolioUser.fromMap(Map<String, dynamic> map) : this(map['id'], map['portfolioID'], map['userId'], map['read'], map['write'], map['stake']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {'portfolioID': portfolioID, 'userId': userId, 'read': read, 'write': write, 'stake': stake};
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

class Portfolio extends Transformable {
  String name, brokerId;

  /// A short code identifying this broker, ec. IBKR
  String code;

  Portfolio([id, this.name, this.brokerId, this.code]) : super._(id);

  Portfolio.fromMap(Map<String, dynamic> map) : this(map['id'], map['name'], map['brokerId'], map['code']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {'name': name, 'brokerId': brokerId, 'code': code};
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

class Currency {
  String symbol, code, name;
  int id;

  Currency.fromMap(Map<String, dynamic> map) {
    symbol = map['symbol'];
    code = map['code'];
    name = map['name'];
    id = map['id'];
  }
}

class Sector {
  String id, name;

  Sector.fromMap(Map<String, dynamic> map) {
    id = map['id'];
    name = map['name'];
  }
}

class Entity extends Transformable {
  String name, description, sectorId;

  Entity([id, this.name, this.description, this.sectorId]) : super._(id);

  Entity.fromMap(Map<String, dynamic> map) : this(map['id'], map['name'], map['description'], map['sectorId']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {'name': name, 'description': description, 'sectorId': sectorId};
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

enum InstrumentType { STOCK, CASH, BOND, DEBT }
enum TransactionType {BUY, SELL, DIV, SDIV, ROC}

class Instrument extends Transformable {
  InstrumentType type;
  int currrencyId;
  String code, entityId, description, countryId;
  bool active;

  Instrument([id, this.currrencyId, this.code, this.entityId, this.description, this.countryId, this.active, this.type]) : super._(id);

  Instrument.fromMap(Map<String, dynamic> map) : this(map['id'], map['currencyId'], map['code'], map['entityId'], 
  map['type'], map['description'], );

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {
      'currencyId': currrencyId,
      'description': description,
      'countryId': countryId,
      'entityId': entityId,
      'code': code,
      'active': active,
      'type': type.toString()
    };
    if (id != null) {
      map['id'] = id;
    }
    return map;
  }
}

class Transaction extends Transformable{

  DateTime date;

  String portfolioId, instrumentId;
  double units, rate, fees, taxes;

  bool provisional;

  TransactionType type;

  Transaction([id, this.date, this.portfolioId, this.instrumentId, this.units, this.rate, this.fees, this.taxes, this.type, this.provisional]): super._(id);

  Transaction.fromMap(Map<String, dynamic> map): this(map['id'], map['date'], map['portfolioId'], map['instrumentId'], map['units'], map['rate'], map['fees'], map['taxes'], map['type'], map['provisional']);

  @override
  Map<String, dynamic> asMap() {
    Map<String, dynamic> map = {
      'date': date,
      'portfolioId': portfolioId,
      'instrumentId': instrumentId,
      'units': units,
      'rate': rate,
      'fees': fees,
      'taxes': taxes,
      'type': type,
      'provisional': provisional
    };
    if (id != null) {
      map['id'] = id;
    }
    return map;
  } 
}
