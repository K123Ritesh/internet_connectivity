import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'internet_connectivity_platform_interface.dart';

/// An implementation of [InternetConnectivityPlatform] that uses method channels.
class MethodChannelInternetConnectivity extends InternetConnectivityPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('internet_connectivity');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
