import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  AppstoreOutlined,
  DesktopOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  PieChartOutlined,
  SettingOutlined,
  LineChartOutlined,
  OrderedListOutlined,
} from "@ant-design/icons";
import { Button, Menu, Dropdown } from "antd";
import type { MenuProps } from "antd";
import useFetchStrategyNames from "../../hooks/useFetchStrategyNames";

type MenuItem = Required<MenuProps>["items"][number];

function strategyPath(strategyName: string) {
  if (strategyName === "makro-trend") {
    return "/custom-strategy/makro-trend";
  }
  return `/strategy/${strategyName}`;
}

function screenerPath(screenerSidebarName: string) {
  let screenerName = screenerSidebarName;
  if (screenerSidebarName === "Stable stocks global") {
    screenerName = "Nordea Stabil Aksjer Global";
  } else if (screenerSidebarName === "Country") {
    screenerName = "Country ETFs on US stock exchange";
  } else if (screenerSidebarName === "Sector") {
    screenerName = "US sector ETFs";
  }
  return `/screener/rsi/${screenerName}`;
}

function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[],
  type?: "group"
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
    type,
  } as MenuItem;
}

const Sidebar: React.FC = () => {
  const isProduction = process.env.NODE_ENV === "production";

  let strategyNames: string[] = [];
  if (isProduction) {
    strategyNames = [
      "makro-trend",
      "mt-short-edge",
      "rotation",
      "president-election",
      "medicine-cycle",
    ];
  } else {
    strategyNames = useFetchStrategyNames();
  }

  const menuItems = [
    {
      label: "Dashboard",
      key: "1",
      icon: <DesktopOutlined />,
      route: "/",
    },
    {
      label: "Analyse",
      key: "2",
      icon: <LineChartOutlined />,
      route: "/analyse",
    },
    {
      label: "Portfolio Mixer",
      key: "3",
      icon: <PieChartOutlined />,
      route: "/portfoliomixer",
    },
    {
      label: "Settings",
      key: "4",
      icon: <SettingOutlined />,
      route: "/settings",
    },
    {
      label: "Strategies",
      key: "sub1",
      icon: <AppstoreOutlined />,
      children: strategyNames.map((strategyName, index) => ({
        label: strategyName,
        key: (index + 5).toString(),
        route: strategyPath(strategyName),
      })),
    },
    {
      label: "Screeners",
      key: "sub2",
      icon: <OrderedListOutlined />,
      children: [
        "Country ETFs on US stock exchange",
        "US sector ETFs",
        "Nordea Stabil Aksjer Global",
      ].map((screenerName, index) => ({
        label: screenerName,
        key: (index + 5 + strategyNames.length).toString(),
        route: screenerPath(screenerName),
      })),
    },
  ];

  const items: MenuItem[] = menuItems.map((mi) =>
    getItem(
      mi.label,
      mi.key,
      mi.icon,
      mi.children?.map((child) => getItem(child.label, child.key))
    )
  );

  const [collapsed, setCollapsed] = useState(true);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleMenuClick = (e: any) => {
    const item =
      menuItems.find((mi) => mi.key === e.key) ||
      menuItems
        .flatMap((mi) => mi.children || [])
        .find((child) => child.key === e.key);
    if (item) {
      navigate(item.route as string);
    } else {
      console.warn("Unknown menu item:", e.key);
    }
  };

  const toggleCollapsed = () => setCollapsed(!collapsed);

  const currentMenuItemKey =
    menuItems.find((mi) => mi.route === location.pathname)?.key ||
    menuItems
      .flatMap((mi) => mi.children || [])
      .find((child) => child.route === location.pathname)?.key;

  const isMobile = windowWidth < 600;
  const showPageButtons = !isMobile || (isMobile && !collapsed);

  const menuDropdown = (
    <Menu
      defaultSelectedKeys={[currentMenuItemKey ?? ""]}
      mode="inline"
      theme="light"
      inlineCollapsed={collapsed}
      items={items}
      onClick={handleMenuClick}
    />
  );

  return (
    <div
      style={{
        width: isMobile ? 0 : collapsed ? 80 : 200,
        transition: "width 0.5s ease",
      }}
    >
      {!isMobile && (
        <Button
          type="primary"
          onClick={toggleCollapsed}
          style={{ marginBottom: 16 }}
        >
          {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        </Button>
      )}
      {isMobile ? (
        <Dropdown overlay={menuDropdown} trigger={["click"]}>
          <Button type="primary" icon={<MenuUnfoldOutlined />} />
        </Dropdown>
      ) : (
        showPageButtons && (
          <Menu
            defaultSelectedKeys={[currentMenuItemKey ?? ""]}
            mode="inline"
            theme="light"
            inlineCollapsed={collapsed}
            items={items}
            onClick={handleMenuClick}
          />
        )
      )}
    </div>
  );
};

export default Sidebar;
