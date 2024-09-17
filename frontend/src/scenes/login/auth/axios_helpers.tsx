import axios, { Method, AxiosRequestConfig, AxiosResponse } from "axios";
import { path } from "../../../hooks/apiAdress";

axios.defaults.baseURL = path;
axios.defaults.headers.post["Content-Type"] = "application/json";

export const getAuthToken = (): string | null => {
  return window.localStorage.getItem("auth_token");
};

export const setAuthHeader = (token: string | null): void => {
  if (token !== null) {
    window.localStorage.setItem("auth_token", token);
  } else {
    window.localStorage.removeItem("auth_token");
  }
};

axios.defaults.baseURL = path;
axios.defaults.headers.post["Content-Type"] = "application/json";

// Add a response interceptor to handle 401 errors
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token has expired or user is not authenticated
      // Remove the auth token and redirect to login screen
      //setAuthHeader(null);
      // if not in login page
      if (window.location.pathname !== "/login") {
        const currentPath = window.location.pathname;
        const encodedPath = encodeURIComponent(currentPath);
        window.location.href = `/login?redir_path=${encodedPath}`;
      }
    }
    return Promise.reject(error);
  }
);

export const request = (
  method: Method,
  url: string,
  data?: any
): Promise<AxiosResponse<any>> => {
  let headers: Record<string, string> = {};
  const authToken = getAuthToken();

  if (authToken && authToken !== "null") {
    headers = { Authorization: `Bearer ${authToken}` };
  }

  const config: AxiosRequestConfig = {
    method: method,
    url: url,
    headers: headers,
    data: data,
  };

  return axios(config);
};
