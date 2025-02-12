import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  NativeModules,
  StyleSheet,
} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';
import { useNavigation } from '../context/RouteContext';

const EpinPage: React.FunctionComponent = () => {
  const {setRoute} = useNavigation()
  const [epin, setEpin] = React.useState<string>('');
  const {NativeLocalStorage, NotificationModule, OverlayModule} = NativeModules;

  const handleEpin = () => {
    // NotificationModule.startNotificationService();
    OverlayModule.startOverlay();
  };

  const handlePermission = () => {
    setRoute('PermissionPage')
  }


  console.log('epin', NativeLocalStorage.getItem('EpinData'));

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle={'light-content'} backgroundColor={Colors.darker} />
      <Text style={styles.title}>Device Blocker</Text>

      <View style={[styles.innerContainer, {backgroundColor: Colors.black}]}>
        <Text style={[styles.title, {color: Colors.white}]}>E-pin</Text>
        <TextInput
          style={[
            styles.input,
            {
              color: Colors.white,
              borderColor: Colors.white,
            },
          ]}
          placeholder="Enter Your Epin"
          placeholderTextColor={Colors.light}
          value={epin}
          onChangeText={setEpin}
        />

        <TouchableOpacity
          style={[styles.button, {backgroundColor: Colors.light}]}
          onPress={handleEpin}>
          <Text style={[styles.buttonText, {color: Colors.black}]}>Verify</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, {backgroundColor: Colors.light}]}
          onPress={handlePermission}>
          <Text style={[styles.buttonText, {color: Colors.black}]}>permission</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

export default EpinPage;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center', // Centers vertically
    alignItems: 'center', // Centers horizontally
    color: Colors.darker,
  },
  innerContainer: {
    width: '80%',
    padding: 20,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
    color: 'white',
  },
  input: {
    height: 50,
    borderWidth: 1,
    borderRadius: 5,
    paddingHorizontal: 10,
    marginBottom: 15,
    width: '100%',
  },
  button: {
    height: 50, // Matches input field height
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 5,
    width: '100%', // Matches input field width
  },
  buttonText: {
    fontWeight: 'bold',
    fontSize: 16,
  },
});
