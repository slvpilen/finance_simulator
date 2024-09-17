import { useState, useEffect, useCallback } from "react";
import LineChart from "../components/chart/LineChart";
import InputWithChoices from "../components/InputWithChoices";
import { usePortfolioMixer } from "../hooks/usePortfolioMixer";
import { setCookie, getCookie } from "../utils/cookies";
import useFetchStrategyNames from "../hooks/useFetchStrategyNames";

export default function PortfolioMixer() {
  const [strategyNameAndResult, setStrategyNameAndResult] = useState<
    string[][]
  >(() => {
    const cookie = getCookie("strategyNameAndResult");
    return cookie ? JSON.parse(cookie) : [];
  });
  const [rebalanceInterval, setRebalanceInterval] = useState(() => {
    const cookie = getCookie("rebalanceInterval");
    return cookie ? parseInt(cookie, 10) : 22;
  });
  const [gearing, setGearing] = useState(() => {
    const cookie = getCookie("gearing");
    return cookie ? parseFloat(cookie) : 1;
  });
  const [hasError, setHasError] = useState(false);
  const choicesStrategyName = useFetchStrategyNames();
  const choicesStrategyResult = [
    "ALL_INDUSTRIES_COMBINED",
    "simulation_ver_1",
    "v_1.0",
    "Result 4",
    "v_1_days",
    "v_2_days",
    "v_3_days",
    "v_4_days",
    "v_5_days",
    "v_5_days_ewa",
    "v_5_days_ewo",
    "v_5_days_ewk",
    "v_5_days_ewz",
    "v_5_days_ewc",
    "v_5_days_ech",
    "v_5_days_gxc",
    "v_5_days_gxg",
    "v_5_days_ewq",
    "v_5_days_ewg",
    "v_5_days_ewh",
    "v_1_days_btc_usd",
    "v_1_days_btc_usd_lower_threshold",
    "v_1_days_sp500",
    "v_1_days_tlt",
    "v_15_days",
    "v_10_days_sp500",
    "v_2_days_sp500",
  ];
  // TODO add key_value for strategyName and results
  // Håndter rebalansering!

  const { result, submitData, loading } = usePortfolioMixer();

  useEffect(() => {
    setCookie(
      "strategyNameAndResult",
      JSON.stringify(strategyNameAndResult),
      7
    );
    setCookie("rebalanceInterval", rebalanceInterval.toString(), 7);
    setCookie("gearing", gearing.toString(), 7);
  }, [strategyNameAndResult, rebalanceInterval, gearing]);

  const validateInputs = useCallback(() => {
    for (const [name, result] of strategyNameAndResult) {
      if (
        !choicesStrategyName.includes(name) ||
        !choicesStrategyResult.includes(result)
      ) {
        return false;
      }
    }
    if (strategyNameAndResult.length < 1) {
      return false;
    }
    return true;
  }, [strategyNameAndResult, choicesStrategyName, choicesStrategyResult]);

  const handleSubmit = useCallback(
    (e?: React.FormEvent) => {
      if (e) e.preventDefault();
      if (validateInputs()) {
        submitData(strategyNameAndResult, rebalanceInterval, gearing);
        setHasError(false);
      } else {
        setHasError(true);
      }
    },
    [
      strategyNameAndResult,
      rebalanceInterval,
      gearing,
      submitData,
      validateInputs,
    ]
  );

  const handleStrategyChange = (
    index: number,
    fieldIndex: number,
    value: string
  ) => {
    const newStrategies = [...strategyNameAndResult];
    newStrategies[index][fieldIndex] = value;
    setStrategyNameAndResult(newStrategies);
  };

  const addStrategyField = () => {
    setStrategyNameAndResult([...strategyNameAndResult, ["", ""]]);
  };

  const removeStrategyField = () => {
    setStrategyNameAndResult(strategyNameAndResult.slice(0, -1));
  };

  const incrementRebalanceInterval = () => {
    setRebalanceInterval((prev) => prev + 1);
  };

  const decrementRebalanceInterval = () => {
    setRebalanceInterval((prev) => (prev - 1 >= 0 ? prev - 1 : 0));
  };

  const incrementGearing = () => {
    setGearing((prev) => (prev + 0.5 >= 0.5 ? prev + 0.5 : 0.5));
  };

  const decrementGearing = () => {
    setGearing((prev) => (prev - 0.5 >= 0.5 ? prev - 0.5 : 0.5));
  };

  return (
    <div>
      <h1 className="mb-4">Portfolio Mixer</h1>
      <div className="container mt-5">
        <div className="alert alert-info">
          <div className="container mt-5"></div>
          <p>
            This page is under construction. It allows you to mix different
            strategies together to create a combined portfolio.
          </p>
        </div>
        <form onSubmit={handleSubmit}>
          {strategyNameAndResult.map((strategy, index) => (
            <div key={index} className="mb-3">
              <h6>Strategy {index + 1}</h6>
              <InputWithChoices
                choices={choicesStrategyName}
                placeholder="Select a strategy name..."
                value={strategy[0]}
                onChange={(e) => handleStrategyChange(index, 0, e.target.value)}
                hasError={
                  hasError && !choicesStrategyName.includes(strategy[0])
                }
              />
              <InputWithChoices
                choices={choicesStrategyResult}
                placeholder="Select a strategy result..."
                value={strategy[1]}
                onChange={(e) => handleStrategyChange(index, 1, e.target.value)}
                hasError={
                  hasError && !choicesStrategyResult.includes(strategy[1])
                }
              />
            </div>
          ))}
          <button
            type="button"
            className="btn btn-primary mb-3"
            onClick={addStrategyField}
          >
            Add Strategy
          </button>
          {strategyNameAndResult.length > 1 && (
            <button
              type="button"
              className="btn btn-danger mb-3"
              onClick={removeStrategyField}
            >
              Remove Strategy
            </button>
          )}
          <div className="mb-3">
            <label htmlFor="rebalanceInterval" className="form-label">
              Rebalance Interval:
            </label>
            <div className="input-group">
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={decrementRebalanceInterval}
              >
                -
              </button>
              <input
                type="number"
                id="rebalanceInterval"
                className="form-control text-center no-arrows"
                value={rebalanceInterval}
                onChange={(e) => setRebalanceInterval(Number(e.target.value))}
              />
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={incrementRebalanceInterval}
              >
                +
              </button>
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="gearing" className="form-label">
              Gearing:
            </label>
            <div className="input-group">
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={decrementGearing}
              >
                -
              </button>
              <input
                type="number"
                id="gearing"
                className="form-control text-center no-arrows"
                value={gearing}
                step="0.5"
                min="0.5"
                onChange={(e) => setGearing(Number(e.target.value))}
              />
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={incrementGearing}
              >
                +
              </button>
            </div>
          </div>
          <button type="submit" className="btn btn-success">
            Submit
          </button>
        </form>
      </div>
      <div className="performance-chart mt-5">
        <h2>Performance</h2>
        {loading ? (
          <div className="text-center">
            <div className="spinner-border text-primary" role="status">
              <span className="sr-only">Loading...</span>
            </div>
          </div>
        ) : result.length > 0 ? (
          <LineChart ticks={[result]} chartName="Combined Portfolio" />
        ) : (
          <p>No data to display</p>
        )}
      </div>
    </div>
  );
}
