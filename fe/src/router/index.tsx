import React from 'react';
import { Navigate, createBrowserRouter } from 'react-router-dom';

import PATH from '@constants/routerPath';

import Login from '@pages/Login';
import Auth from '@pages/Auth';
import HomeMain from '@pages/Home/HomeMain';
import TownSetting from '@pages/Home/TownSetting';
import TownSearching from '@pages/Home/TownSearching';
import HomeCategory from '@pages/Home/HomeCategory';
import Sale from '@pages/Sale';
import SaleCategory from '@pages/Sale/SaleCategory';
import Like from '@pages/Like';
import SalesHistory from '@pages/SalesHistory';
import MainTabBar from '@molecules/TabBars/MainTabBar';
import SaleTabBar from '@molecules/TabBars/SaleTabBar';
import Detail from '@pages/Detail';
import Ready from '@pages/Ready';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to={PATH.HOME.DEFAULT} />,
  },
  {
    path: PATH.HOME.DEFAULT,
    element: <HomeMain />,
    children: [
      {
        path: PATH.HOME.DEFAULT,
        element: <MainTabBar isClickedId={PATH.IS_CLICKED_ID.HOME} />,
      },
    ],
  },
  {
    path: PATH.HOME.TOWN_SETTING,
    element: <TownSetting />,
  },
  {
    path: PATH.HOME.TOWN_SEARCH,
    element: <TownSearching />,
  },
  {
    path: PATH.HOME.CATEGORY,
    element: <HomeCategory />,
  },
  {
    path: PATH.SALE.DEFAULT,
    element: <Sale />,
    children: [
      {
        path: PATH.SALE.DEFAULT,
        element: <SaleTabBar townNames="역삼1동" />,
      },
    ],
  },
  {
    path: PATH.SALE.CATEGORY,
    element: <SaleCategory />,
  },
  {
    path: PATH.PRODUCT.DEFAULT,
    element: <Detail />,
  },
  {
    path: PATH.PRODUCT.CHAT,
    element: <Ready />,
    children: [
      {
        path: PATH.PRODUCT.CHAT,
        element: <MainTabBar isClickedId={PATH.IS_CLICKED_ID.CHAT} />,
      },
    ],
  },
  {
    path: PATH.PRODUCT.SALES,
    element: <SalesHistory />,
    children: [
      {
        path: PATH.PRODUCT.SALES,
        element: <MainTabBar isClickedId={PATH.IS_CLICKED_ID.SALES} />,
      },
    ],
  },
  {
    path: PATH.PRODUCT.LIKE,
    element: <Like />,
    children: [
      {
        path: PATH.PRODUCT.LIKE,
        element: <MainTabBar isClickedId={PATH.IS_CLICKED_ID.LIKE} />,
      },
    ],
  },
  {
    path: PATH.AUTH.DEFAULT,
    element: <Auth />,
  },
  {
    path: PATH.AUTH.LOGIN,
    element: <Login />,
    children: [
      {
        path: PATH.AUTH.LOGIN,
        element: <MainTabBar isClickedId={PATH.IS_CLICKED_ID.LOGIN} />,
      },
    ],
  },
  {
    path: PATH.AUTH.SETTING,
  },
]);

export default router;
