import { BrowserRouter as Router } from "react-router-dom";
import { Routes, Route } from "react-router-dom";
import Navbar from "./components/layout/Navbar";
import Dashboard from "./pages/Dashboard";
import { LoginForm } from "./pages/Login";
import ProtectedRoute from "./components/routes/ProtectedRoute";
import TransactionsPage from "./pages/TransactionsPage";

function App() {
  return (
    <Router>
      <Navbar />
      <div className="p-6">
        <Routes>
          <Route path="/login" element={<LoginForm />} />={" "}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard/:id" element={<Dashboard />} />={" "}
            <Route path="/transactions/:id" element={<TransactionsPage />} />={" "}
          </Route>
        </Routes>
      </div>
    </Router>
  );
}

export default App;
