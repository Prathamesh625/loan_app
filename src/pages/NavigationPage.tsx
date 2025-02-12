import React, {useEffect} from 'react';
import {useNavigation} from '../context/RouteContext';
import QrScanner from '../components/Scanner';
import EpinPage from './EpinPage';
import PermissionPage from './PermissionPage';
import HomePage from './HomePage';
import SuccessPage from './SuccessPage';

const NaviationPage: React.FunctionComponent = () => {
  const {route} = useNavigation();

  console.log(route);

  useEffect(() => {
    console.log('Page updated');
  }, []);

  if (route === 'HomePage') {
    return <HomePage />;
  } else if (route === 'ScannerPage') {
    return <QrScanner />;
  } else if (route === 'EpinPage') {
    return <EpinPage />;
  } else if (route === 'DeleteAppPage') {
    return <></>;
  } else if (route === 'PermissionPage') {
    return <PermissionPage />;
  } else if (route === 'SuccessPage') {
    return <SuccessPage />;
  } else {
    return <HomePage />;
  }

  return <React.Fragment></React.Fragment>;
};

export default NaviationPage;
