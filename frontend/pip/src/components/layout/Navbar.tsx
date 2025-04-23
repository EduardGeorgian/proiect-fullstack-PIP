import { Link, useLocation, useParams } from "react-router-dom";

export default function Navbar() {
  const { pathname } = useLocation();
  const { id } = useParams<{ id: string }>();

  const linkStyle = (path: string) =>
    `text-sm font-medium ${
      pathname === path ? "text-primary" : "text-muted-foreground"
    } hover:underline`;

  return (
    <nav className="flex gap-6 p-4 border-b">
      <Link to={`/dashboard/${id}`} className={linkStyle("/")}>
        Dashboard
      </Link>
      <Link to="/transactions" className={linkStyle("/transactions")}>
        Transactions
      </Link>
      <Link to="/friends" className={linkStyle("/friends")}>
        Friends
      </Link>
    </nav>
  );
}
