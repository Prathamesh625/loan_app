import React from 'react';
import {
  View,
  Button,
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  NativeModules,
  Touchable,
  TouchableOpacity,
} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';
import {useNavigation} from '../context/RouteContext';

const PermissionPage = () => {
  const {NativeLocalStorage, NativePermission} = NativeModules;
  const {setRoute} = useNavigation();

  const [permissions, setPermissions] = React.useState([
    {name: 'Camera', granted: false},
    {name: 'Storage', granted: false},
    {name: 'Notification', granted: false},
    {name: 'Overlay', granted: false},
    {name: 'Device Admin', granted: false},
    {name: 'Location', granted: false},
    {name: 'Accessiblity', granted: false},
  ]);

  const handlePermissionRequest = (permissionName: string) => {
    // Logic to handle permission request

    switch (permissionName) {
      case 'Camera':
        NativePermission.requestCameraPermission();
        break;
      case 'Storage':
        NativePermission.requestStoragePermission();
        break;
      case 'Notification':
        NativePermission.requestNotificationPermission();
        break;
      case 'Overlay':
        NativePermission.requestOverlayPermission();
        break;
      case 'Device Admin':
        NativePermission.requestDeviceAdminPermission();
        break;
      case 'Location':
        NativePermission.requestLocationPermission();
        break;
      case 'Accessiblity':
        NativePermission.requestAccessibilityPermission();
        break;
      default:
        break;
    }
  };


  

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#008080" />
      <View style={styles.appBar}>
        <Text style={styles.appBarText}>PermissionPage</Text>
      </View>
      <View style={styles.titleContainer}>
        <Text style={styles.titleText}>Manage Permissions</Text>
      </View>
      <View style={styles.content}>
        {permissions.map((permission, index) =>
          permission.granted ? (
            <View key={index} style={styles.checkboxContainer}>
              <Text style={styles.checkboxLabel}>{permission.name}</Text>
            </View>
          ) : (
            <React.Fragment key={index}>
              <Button
                title={`${permission.name} Permission`}
                color="#008080"
                onPress={() => handlePermissionRequest(permission.name)}
              />
              <View style={styles.buttonSpacing} />
            </React.Fragment>
          ),
        )}
      </View>

      <TouchableOpacity
        style={styles.button}
        onPress={() => setRoute('ScannerPage')}>
        <Text style={styles.buttonText}>Proceed</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  appBar: {
    height: 56,
    backgroundColor: '#008080',
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 5,
  },
  appBarText: {
    color: '#fff',
    fontSize: 20,
    fontWeight: 'bold',
  },
  titleContainer: {
    padding: 20,
    alignItems: 'center',
  },
  titleText: {
    color: '#333',
    fontSize: 28,
    fontWeight: '600',
  },
  content: {
    flex: 1,
    alignItems: 'center',
    padding: 20,
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 15,
    backgroundColor: '#444',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 10,
    width: '100%',
  },
  checkboxLabel: {
    fontSize: 16,
    color: '#fff',
  },
  buttonSpacing: {
    height: 16,
  },
  button: {
    backgroundColor: '#008080',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 5,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default PermissionPage;
