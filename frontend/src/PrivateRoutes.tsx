import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { getAuthToken } from "./scenes/login/auth/axios_helpers";

const PrivateRoutes: React.FC = () => {
  const authToken = getAuthToken();

  if (!authToken || authToken === null) {
    const currentPath = window.location.pathname;
    const encodedPath = encodeURIComponent(currentPath);
    return <Navigate to={`/login?redir_path=${encodedPath}`} />;
  }

  return <Outlet />;
};

export default PrivateRoutes;
