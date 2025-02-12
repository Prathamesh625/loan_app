import React, {useContext, createContext, useEffect} from 'react';
import {RouteContext} from './RouteContext';
import {Pages} from '../types';

export const RouteContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [route, setRoute] = React.useState<string>();
  const [data, setData] = React.useState<string>();

  useEffect(() => {
    if (route === 'ScannerPage') {
      setRoute('ScannerPage');
    } else if (route === 'EpinPage') {
      setRoute('EpinPage');
    } else if (route === 'DeleteAppPage') {
      setRoute('DeleteAppPage');
    } else if (route === 'PermissionPage') {
      setRoute('PermissionPage');
    } else if (route === 'SuccessPage') {
      setRoute('SuccessPage');
    } else {
      setRoute('EpinPage');
    }
  }, [route]);

  const value = {
    route,
    setRoute,
    setData,
    data,
  };

  return (
    <RouteContext.Provider value={value}>{children}</RouteContext.Provider>
  );
};
