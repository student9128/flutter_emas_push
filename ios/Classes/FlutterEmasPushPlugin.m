#import "FlutterEmasPushPlugin.h"
#if __has_include(<flutter_emas_push/flutter_emas_push-Swift.h>)
#import <flutter_emas_push/flutter_emas_push-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_emas_push-Swift.h"
#endif

@implementation FlutterEmasPushPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterEmasPushPlugin registerWithRegistrar:registrar];
}
@end
