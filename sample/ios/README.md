# iOS navigation sample: release prerequisite

As checked on 27 September 2026, `Podfile` still requests
`pod 'MapVina', '~> 1.0.0'`, but CocoaPods trunk does not have a `MapVina`
pod (HTTP 404). `pod install` from the public CDN cannot be used as a clean
installation path for this sample. The public native iOS delivery is the
[MapVina Swift Package `1.0.0`](https://github.com/mapvina/mapvina-gl-native-distribution),
not a CocoaPods release.

To make this sample reproducible, replace the Podfile dependency with an SPM
integration and verify the navigation target's Xcode linking, or publish and
verify the CocoaPods pod. Do not document a nonexistent `1.0.1` release or
claim this sample has been tested on the current simulator.
