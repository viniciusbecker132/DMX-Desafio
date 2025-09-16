import { useState } from "react";
import "../index.css"
import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const {login} = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password: password }),
            });

            let data;

            try {
                data = await response.json();
            } catch {
                data = { erro: await response.text() };
            }

            if (!response.ok) {
                alert(data.erro || "Erro ao fazer login");
                return;
            }


            alert(`Bem-vindo ${data.email}`);

            login({ email: data.email, role: data.role }, data.token);
            navigate("/perfil");

        } catch (err) {
            console.error("Erro ao conectar com o servidor:", err);
            alert("Erro ao conectar com o servidor");
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-blue-100">
            <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-lg">
                <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
                    Login
                </h2>
                <form onSubmit={handleSubmit} className="space-y-5">
                    {/* Campo Email */}
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            E-mail
                        </label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="seuemail@email.com"
                            className="mt-1 block w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:ring-2 focus:ring-blue-400 outline-none"
                            required
                        />
                    </div>

                    {/* Campo Senha */}
                    <div>
                        <label
                            htmlFor="password"
                            className="block text-sm font-medium text-gray-700"
                        >
                            Senha
                        </label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            className="mt-1 block w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:ring-2 focus:ring-blue-400 outline-none"
                            required
                        />
                    </div>

                    {/* Botão Entrar */}
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-400 transition-all"
                    >
                        Entrar
                    </button>
                </form>

                <p className="text-sm text-center text-gray-600 mt-4">
                    Não tem conta?{" "}
                    <Link to="/cadastrar" className="text-blue-600 hover:underline font-medium">
                        Cadastre-se
                    </Link>
                </p>
            </div>
        </div>
    );
}