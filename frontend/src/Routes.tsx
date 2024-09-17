import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import "./Routes.css";
import Sidebar from "./scenes/global/Sidebar";
import Dashboard from "./scenes/dashboard/Dashboard";
import PortfolioMixer from "./scenes/PortfolioMixer";
import Settings from "./scenes/Settings";
import Analyse from "./scenes/Analyse";
import MakroTrend from "./scenes/strategies/MakroTrend";
import StandardStrategy from "./scenes/strategies/StandardStrategy";
import RSIScreenerPage from "./scenes/screeners/RsiScreenerPage";
import Login from "./scenes/login/Login";
import PrivateRoutes from "./PrivateRoutes";

function App() {
  return (
    <Router>
      <div className="app">
        <div className="sidebar-content">
          <Sidebar />
        </div>
        <div className="scene">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />

            {/* Private Routes */}
            <Route element={<PrivateRoutes />}>
              <Route path="/" element={<Dashboard />} />
              <Route path="/analyse" element={<Analyse />} />
              <Route path="/portfoliomixer" element={<PortfolioMixer />} />
              <Route path="/settings" element={<Settings />} />
              <Route
                path="/custom-strategy/makro-trend"
                element={<MakroTrend />}
              />
              <Route
                path="/strategy/:strategyName"
                element={<StandardStrategy />}
              />
              <Route
                path="/screener/rsi/:symbolList"
                element={<RSIScreenerPage />}
              />
            </Route>
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
