import 'dart:async';
import 'package:flutter/services.dart';
import 'internet_connectivity_platform_interface.dart';

class InternetConnectivity {
  static const EventChannel _eventChannel =
      EventChannel('internet_connectivity');

  static Stream<String>? _onConnectivityChanged;

  static Stream<String> get onConnectivityChanged {
    _onConnectivityChanged ??=
        _eventChannel.receiveBroadcastStream().cast<String>();
    return _onConnectivityChanged!;
  }

  Future<String?> getPlatformVersion() {
    return InternetConnectivityPlatform.instance.getPlatformVersion();
  }
}
