import React, {useContext} from 'react';

interface RouteContextType {
  data: string | undefined;
  setData: React.Dispatch<React.SetStateAction<string | undefined>>;
  route: string | undefined;
  setRoute: React.Dispatch<React.SetStateAction<string | undefined>>;
}

export const RouteContext = React.createContext<RouteContextType>({
  data: '',
  setData: () => '',
  route: '',
  setRoute: () => '',
});

export const useNavigation = () => useContext(RouteContext);
