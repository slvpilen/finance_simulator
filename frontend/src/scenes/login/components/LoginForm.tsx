import * as React from "react";
import classNames from "classnames";

interface LoginFormProps {
  onLogin: (e: React.FormEvent, login: string, password: string) => void;
}

interface LoginFormState {
  login: string;
  password: string;
}

export default class LoginForm extends React.Component<
  LoginFormProps,
  LoginFormState
> {
  constructor(props: LoginFormProps) {
    super(props);
    this.state = {
      login: "",
      password: "",
    };
  }

  onChangeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    this.setState({ [name]: value } as Pick<
      LoginFormState,
      keyof LoginFormState
    >);
  };

  onSubmitLogin = (e: React.FormEvent) => {
    e.preventDefault();
    const { login, password } = this.state;
    this.props.onLogin(e, login, password);
  };

  render() {
    return (
      <div className="row justify-content-center">
        <div className="col-4">
          <h1>Log in</h1>
          <div className="tab-content">
            <div
              className={classNames("tab-pane", "fade", "show active")}
              id="pills-login"
            >
              <form onSubmit={this.onSubmitLogin}>
                <div className="form-outline mb-4">
                  <input
                    type="text"
                    id="loginName"
                    name="login"
                    className="form-control"
                    onChange={this.onChangeHandler}
                  />
                  <label className="form-label" htmlFor="loginName">
                    Username
                  </label>
                </div>

                <div className="form-outline mb-4">
                  <input
                    type="password"
                    id="loginPassword"
                    name="password"
                    className="form-control"
                    onChange={this.onChangeHandler}
                  />
                  <label className="form-label" htmlFor="loginPassword">
                    Password
                  </label>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary btn-block mb-4"
                >
                  Log in
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
