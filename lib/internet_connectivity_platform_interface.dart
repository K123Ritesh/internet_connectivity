import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'internet_connectivity_method_channel.dart';

abstract class InternetConnectivityPlatform extends PlatformInterface {
  /// Constructs a InternetConnectivityPlatform.
  InternetConnectivityPlatform() : super(token: _token);

  static final Object _token = Object();

  static InternetConnectivityPlatform _instance = MethodChannelInternetConnectivity();

  /// The default instance of [InternetConnectivityPlatform] to use.
  ///
  /// Defaults to [MethodChannelInternetConnectivity].
  static InternetConnectivityPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [InternetConnectivityPlatform] when
  /// they register themselves.
  static set instance(InternetConnectivityPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
