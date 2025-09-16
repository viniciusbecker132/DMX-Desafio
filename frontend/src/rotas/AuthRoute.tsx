import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type {JSX} from "react";

interface AuthRouteProps {
    children: JSX.Element;
}

export default function AuthRoute({ children }: AuthRouteProps) {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return children;
}