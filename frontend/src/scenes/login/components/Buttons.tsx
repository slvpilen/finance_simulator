import * as React from "react";

interface ButtonsProps {
  login: React.MouseEventHandler<HTMLButtonElement> | undefined;
  logout: React.MouseEventHandler<HTMLButtonElement> | undefined;
  hasToken: boolean;
}

export default function Buttons(props: ButtonsProps) {
  return (
    <div className="row">
      <div className="col-md-12 text-center" style={{ marginTop: "30px" }}>
        {props.hasToken && (
          <button
            className="btn btn-dark"
            style={{ margin: "10px" }}
            onClick={props.logout}
          >
            Logout
          </button>
        )}
      </div>
    </div>
  );
}
