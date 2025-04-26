import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";

export default function Navbar() {
  const { pathname } = useLocation();
  const [id, setId] = useState<null | null>(null);

  useEffect(() => {
    const id = JSON.parse(localStorage.getItem("user") || "{}").id;
    setId(id);
  }, []);

  const linkStyle = (path: string) =>
    `text-sm font-medium ${
      pathname === path ? "text-primary" : "text-muted-foreground"
    } hover:underline`;

  return (
    <nav className="flex gap-6 p-4 border-b">
      <Link to={`/dashboard/${id}`} className={linkStyle("/dashboard")}>
        Dashboard
      </Link>
      <Link to={`/transactions/${id}`} className={linkStyle(`/transactions`)}>
        Transactions
      </Link>
      <Link to="/friends" className={linkStyle("/friends")}>
        Friends
      </Link>
    </nav>
  );
}
