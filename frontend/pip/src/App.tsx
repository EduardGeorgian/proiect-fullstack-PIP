import { BrowserRouter as Router } from "react-router-dom";
import { Routes, Route } from "react-router-dom";
import Navbar from "./components/layout/Navbar";
import Dashboard from "./pages/Dashboard";
import { LoginForm } from "./pages/Login";
import ProtectedRoute from "./components/routes/ProtectedRoute";
import TransactionsPage from "./pages/TransactionsPage";
import FriendsPage from "./pages/FriendsPage";
import { Toaster } from "./components/ui/sonner";
import { UserProvider } from "./context/UserContext";
import { RegisterForm } from "./pages/Register";

// TODO: Check routes to see if the problem with the id undefined has something to do with it

function App() {
  return (
    <UserProvider>
      <Router>
        <Navbar />
        <div className="p-6">
          <Routes>
            <Route path="/login" element={<LoginForm />} />={" "}
            <Route path="/register" element={<RegisterForm />} />={" "}
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<Dashboard />} />={" "}
              <Route path="/dashboard/:id" element={<Dashboard />} />={" "}
              <Route path="/transactions/:id" element={<TransactionsPage />} />={" "}
              <Route path="/friends/:id" element={<FriendsPage />} />={" "}
            </Route>
          </Routes>
        </div>
        <Toaster position="top-right" />
      </Router>
    </UserProvider>
  );
}

export default App;
