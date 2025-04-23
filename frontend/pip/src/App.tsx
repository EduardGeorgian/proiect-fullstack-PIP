import { BrowserRouter as Router } from "react-router-dom";
import { Routes, Route } from "react-router-dom";
import Navbar from "./components/layout/Navbar";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <Router>
      <Navbar />
      <div className="p-6">
        <Routes>
          = <Route path="/dashboard/:id" element={<Dashboard />} />={" "}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
