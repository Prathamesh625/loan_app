
import React, {useState} from 'react';
import {Text, StyleSheet, View, NativeModules, StatusBar} from 'react-native';
import {
  Camera,
  useCodeScanner,
  useCameraDevice,
  CameraDevice,
} from 'react-native-vision-camera';
import {useNavigation} from '../context/RouteContext';
import {BlurView} from '@react-native-community/blur';

const QrScanner: React.FunctionComponent = () => {
  const {setRoute, setData} = useNavigation();
  const {NativeLocalStorage} = NativeModules;

  const codeScanner = useCodeScanner({
    codeTypes: ['qr'],
    onCodeScanned: result => {
      if (result.length < 0) {
        return null;
      }
      const value = result[0].value;
      setData(value);
      setRoute('SuccessPage');
    },
  });



  const data = NativeLocalStorage.getItem('epinNo');
  console.log('data', data);

  const cameraDevice = useCameraDevice('back') as CameraDevice;

  if (!cameraDevice) return <Text>Camera Not Found</Text>;

  return (
    <View style={styles.container}>
      {/* Status Bar with custom color */}
      <StatusBar barStyle="light-content" backgroundColor="#008080" />

      {/* App Bar */}
      <View style={styles.appBar}>
        <Text style={styles.appBarText}>QR Scanner</Text>
      </View>

      <View style={styles.cameraContainer}>
        {/* Apply Blur Effect to the whole area */}
        <BlurView
          style={styles.blurBackground}
          blurType="light"
          blurAmount={60} // Adjust blur intensity here
        >
          <Camera
            device={cameraDevice}
            isActive={true}
            codeScanner={codeScanner}
            style={styles.camera}
          />
        </BlurView>

        {/* Scanner Box */}
        <View style={styles.overlay}>
          <View style={styles.scannerBox} />
          <Text style={styles.instructionText}>
            Align the QR code within the box to scan.
          </Text>
        </View>
      </View>
    </View>
  );
};

export default QrScanner;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  appBar: {
    height: 56,
    backgroundColor: '#008080', // App bar color
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  appBarText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  cameraContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  camera: {
    width: '100%',
    height: '100%',
    borderRadius: 10,
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scannerBox: {
    width: 250, // Adjust the width of the scanner box
    height: 250,
    borderWidth: 2,
    borderColor: 'white',
    borderRadius: 10,
  },
  instructionText: {
    marginTop: 20,
    color: 'white',
    fontSize: 16,
    textAlign: 'center',
  },
  blurBackground: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
});
