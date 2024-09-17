import * as React from "react";
import { getAuthToken, request, setAuthHeader } from "./auth/axios_helpers";
import { useNavigate, useSearchParams } from "react-router-dom";

import Buttons from "./components/Buttons";
import AuthContent from "./auth/AuthContent";
import LoginForm from "./components/LoginForm";
import "./Login.css"; // Make sure to import the CSS file

interface LoginState {
  componentToShow: "login" | "messages";
  error: string | null;
  wiggle: boolean; // State to trigger wiggle animation
}

interface LoginProps {
  navigate: (path: string) => void;
  redirPath: string;
}

class Login extends React.Component<LoginProps, LoginState> {
  constructor(props: LoginProps) {
    super(props);
    this.state = {
      componentToShow: "login",
      error: null,
      wiggle: false,
    };
  }

  login = (): void => {
    this.setState({ componentToShow: "login", error: null, wiggle: false });
  };

  logout = (): void => {
    this.setState({ componentToShow: "login", error: null, wiggle: false });
    setAuthHeader(null);
  };

  onLogin = (e: React.FormEvent, username: string, password: string): void => {
    let { redirPath } = this.props;
    if (!redirPath || redirPath === "/login") {
      redirPath = "/";
    }

    e.preventDefault();
    this.logout();
    request("POST", "/login", {
      login: username,
      password: password,
    })
      .then((response) => {
        setAuthHeader(response.data.token);
        this.setState({
          componentToShow: "messages",
          error: null,
          wiggle: false,
        });
        // prodction
        const isProduction = process.env.NODE_ENV === "production";
        if (isProduction) {
          this.props.navigate(redirPath);
        } else {
          window.location.href = redirPath;
        }
      })
      .catch((error) => {
        setAuthHeader(null);
        const errorMessage =
          error?.response?.data?.message || "Something wrong happened";
        this.setState(
          {
            error: errorMessage,
            wiggle: false, // Reset wiggle state to trigger reanimation
          },
          () => {
            setTimeout(() => this.setState({ wiggle: true }), 0);
          }
        );
      });
  };

  render() {
    const token = getAuthToken();
    return (
      <>
        <Buttons
          login={this.login}
          logout={this.logout}
          hasToken={token !== null}
        />
        {this.state.componentToShow === "login" && (
          <>
            <LoginForm onLogin={this.onLogin} />
            {this.state.error && (
              <p
                className={`error-message ${this.state.wiggle ? "wiggle" : ""}`}
              >
                {this.state.error}
              </p>
            )}
          </>
        )}
        {this.state.componentToShow === "messages" && <AuthContent />}
      </>
    );
  }
}

const LoginWrapper: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const redirPath = searchParams.get("redir_path") || "";

  return <Login navigate={navigate} redirPath={redirPath} />;
};

export default LoginWrapper;
