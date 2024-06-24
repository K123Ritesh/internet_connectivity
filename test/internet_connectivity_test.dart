import 'package:flutter_test/flutter_test.dart';
import 'package:internet_connectivity/internet_connectivity.dart';
import 'package:internet_connectivity/internet_connectivity_platform_interface.dart';
import 'package:internet_connectivity/internet_connectivity_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockInternetConnectivityPlatform
    with MockPlatformInterfaceMixin
    implements InternetConnectivityPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final InternetConnectivityPlatform initialPlatform = InternetConnectivityPlatform.instance;

  test('$MethodChannelInternetConnectivity is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelInternetConnectivity>());
  });

  test('getPlatformVersion', () async {
    InternetConnectivity internetConnectivityPlugin = InternetConnectivity();
    MockInternetConnectivityPlatform fakePlatform = MockInternetConnectivityPlatform();
    InternetConnectivityPlatform.instance = fakePlatform;

    expect(await internetConnectivityPlugin.getPlatformVersion(), '42');
  });
}
