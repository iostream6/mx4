/*
 * 2020.12.26  - Created 
 * 
 */

import 'package:http/http.dart' as http;
import 'dart:convert';

////adb reverse tcp:8080 tcp:8080
////adb reverse --list

const String BASE_URL = 'http://10.0.2.2:8080'; // https://stackoverflow.com/questions/55785581 https://stackoverflow.com/questions/64444236, https://stackoverflow.com/questions/49855754

Future<ServiceResponse> post(String url, {dynamic data, Map<String, String> headers}) async {
  ServiceResponse sr = ServiceResponse();
  try {
    print('URL:: $BASE_URL/$url');
    final response = await http.post('$BASE_URL/$url', body: data, headers: headers);
    switch (response.statusCode) {
      case 200:
        sr.data = json.decode(response.body);
        break;
      default:
        sr.error = json.decode(response.body);
        break;
    }
  } catch (e) {
    sr.error = 'Failure encountered';
    print(e.toString());
  }
  return sr;
}

class ServiceResponse {
  Object data;
  Object error;
}
