import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Login from "../pages/Login.tsx";
import Cadastrar from "../pages/Cadastrar.tsx";
import Perfil from "../pages/Perfil.tsx";
import AuthRoute from "./AuthRoute.tsx";
import Propriedades from "../pages/Propriedades.tsx";
import Talhao from "../pages/Talhao.tsx";
import Safra from "../pages/Safra.tsx";
import Indicadores from "../pages/Indicadores.tsx";

export default function Rotas() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/cadastrar" element={<Cadastrar />} />
                <Route path="*" element={<Login />} />
                <Route
                    path="/perfil"
                    element={
                        <AuthRoute>
                            <Perfil />
                        </AuthRoute>
                    }
                />
                <Route
                    path="/propriedades"
                    element={
                        <AuthRoute>
                            <Propriedades />
                        </AuthRoute>
                    }
                />
                <Route
                    path="/talhao"
                    element={
                        <AuthRoute>
                            <Talhao />
                        </AuthRoute>
                    }
                />
                <Route
                    path="/safra"
                    element={
                        <AuthRoute>
                            <Safra />
                        </AuthRoute>
                    }
                />
                <Route
                    path="/indicadores"
                    element={
                        <AuthRoute>
                            <Indicadores />
                        </AuthRoute>
                    }
                />
            </Routes>
        </Router>
    );
}