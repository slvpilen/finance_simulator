import { useState } from "react";
import "./CheckBox.css";

interface CheckboxProps {
  label: string;
  onChange: (label: string, isChecked: boolean) => void;
  checked?: boolean;
  color?: string;
}

const Checkbox = ({
  label,
  onChange,
  checked = false,
  color = "green",
}: CheckboxProps) => {
  const [isChecked, setIsChecked] = useState(checked);
  const bacgroundCollorNow = isChecked ? color : "white";

  const toggleCheckbox = () => {
    const newCheckedState = !isChecked;
    setIsChecked(newCheckedState);
    onChange(label, newCheckedState);
  };

  return (
    <div className="check-box">
      <label
        className={`filter-button ${isChecked ? "selected" : ""}`}
        htmlFor={label}
        style={{
          backgroundColor: bacgroundCollorNow,
          borderColor: bacgroundCollorNow,
        }}
      >
        {label}
        <input
          id={label}
          type="checkbox"
          checked={isChecked}
          onChange={toggleCheckbox}
        />
      </label>
    </div>
  );
};
export default Checkbox;
