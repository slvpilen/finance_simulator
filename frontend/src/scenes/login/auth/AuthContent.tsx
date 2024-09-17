import * as React from "react";
import { request } from "./axios_helpers";
import { AxiosResponse } from "axios"; // If you're using Axios and need type support
import { setAuthHeader } from "./axios_helpers";

interface AuthContentState {
  data: string[];
}

class AuthContent extends React.Component<{}, AuthContentState> {
  constructor(props: {}) {
    super(props);
    this.state = {
      data: [],
    };
  }

  componentDidMount() {
    request("GET", "/messages", {})
      .then((response: AxiosResponse<{ data: string[] }>) => {
        this.setState({ data: response.data.data });
      })
      .catch((error) => {
        if (error.response && error.response.status === 401) {
          setAuthHeader(null); // If no acces assuming token is invalid and removing it
        } else {
          this.setState({
            data: [error.response ? error.response.code : error.message],
          });
        }
      });
  }

  render() {
    return (
      <div className="row justify-content-md-center">
        <div className="col-4">
          <div className="card" style={{ width: "18rem" }}>
            <div className="card-body">
              <p className="card-text">Logged in</p>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default AuthContent;
