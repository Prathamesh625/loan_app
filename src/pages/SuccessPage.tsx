import React, {useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  Image,
  TouchableOpacity,
  StatusBar,
  NativeModules,
} from 'react-native';
import {useNavigation} from '../context/RouteContext';

const SuccessPage: React.FunctionComponent = () => {
  const {NativeLocalStorage, NotificationModule} = NativeModules;
  const {data} = useNavigation();

  useEffect(() => {
    NativeLocalStorage.setItem('epinNo', data);
    NotificationModule.startNotificationService();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" backgroundColor="#f5f5f5" />
      <View style={styles.content}>
        <Image
          source={require('../assets/tick.png')}
          style={styles.successImage}
        />
        <Text style={styles.successText}>Done!</Text>
      </View>
    </SafeAreaView>
  );
};

export default SuccessPage;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  successImage: {
    width: 150,
    height: 150,
    marginBottom: 20,
  },
  successText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'black',
    textAlign: 'center',
    marginBottom: 30,
  },
  button: {
    backgroundColor: '#4CAF50',
    paddingVertical: 15,
    paddingHorizontal: 40,
    borderRadius: 5,
  },
  buttonText: {
    color: 'black',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
