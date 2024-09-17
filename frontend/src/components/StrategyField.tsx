import React from "react";

interface StrategyFieldProps {
  index: number;
  strategy: string[];
  handleStrategyChange: (index: number, value: string[]) => void;
}

const StrategyField: React.FC<StrategyFieldProps> = ({
  index,
  strategy,
  handleStrategyChange,
}) => {
  return (
    <div className="mb-3" key={index}>
      <label className="form-label">Strategy Name and Result:</label>
      <input
        type="text"
        className="form-control mb-2"
        value={strategy[0]}
        onChange={(e) =>
          handleStrategyChange(index, [e.target.value, strategy[1]])
        }
        placeholder="Strategy Name"
      />
      <input
        type="text"
        className="form-control"
        value={strategy[1]}
        onChange={(e) =>
          handleStrategyChange(index, [strategy[0], e.target.value])
        }
        placeholder="Strategy Result"
      />
    </div>
  );
};

export default StrategyField;
