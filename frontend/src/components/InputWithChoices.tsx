import React from "react";

interface InputWithChoicesProps {
  choices: string[];
  placeholder?: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  hasError: boolean;
}

const InputWithChoices: React.FC<InputWithChoicesProps> = ({
  choices,
  placeholder,
  value,
  onChange,
  hasError,
}) => {
  return (
    <div className={`form-group ${hasError ? "has-error" : ""}`}>
      <label htmlFor="exampleInput">Choose an option:</label>
      <div className="input-group">
        <input
          type="text"
          className={`form-control ${hasError ? "is-invalid" : ""}`}
          id="exampleInput"
          list="datalistOptions"
          placeholder={placeholder || "Type to search..."}
          value={value}
          onChange={onChange}
        />
        <datalist id="datalistOptions">
          {choices.map((choice, index) => (
            <option key={index} value={choice} />
          ))}
        </datalist>
      </div>
      {hasError && (
        <div className="invalid-feedback">Please select a valid option</div>
      )}
    </div>
  );
};

export default InputWithChoices;
