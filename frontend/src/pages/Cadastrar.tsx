import { useState } from "react";
import "../index.css"
import {Link} from "react-router-dom";

export default function Cadastrar() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage(null);

        if (!name || !email || !password || !confirmPassword) {
            setMessage("Por favor, preencha todos os campos.");
            return;
        }
        if (password !== confirmPassword) {
            setMessage("As senhas não coincidem.");
            return;
        }

        const user = { name, email, password, role };

        try {
            const response = await fetch("http://localhost:8080/auth/register", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(user),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Erro ao cadastrar usuário");
            }

            setMessage(`Usuário ${name} cadastrado com sucesso!`);

            // Limpar formulário
            setName("");
            setEmail("");
            setPassword("");
            setConfirmPassword("");
            setRole("");
        }catch (err: unknown) {
                if (err instanceof Error) {
                    setMessage(`Erro: ${err.message}`);
                } else {
                    setMessage("Erro inesperado");
                }
            }
    };


        return (
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-lg">
                    <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
                        Cadastro
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* Nome */}
                        <div>
                            <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                                Nome
                            </label>
                            <input
                                type="text"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="Seu nome completo"
                                className="mt-1 block w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:ring-2 focus:ring-blue-400 outline-none"
                                required
                            />
                        </div>

                        {/* Email */}
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

                        {/* Senha */}
                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
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

                        {/* Confirmar Senha */}
                        <div>
                            <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                                Confirmar Senha
                            </label>
                            <input
                                type="password"
                                id="confirmPassword"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="••••••••"
                                className="mt-1 block w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:ring-2 focus:ring-blue-400 outline-none"
                                required
                            />
                        </div>
                        {/* Cargo  */}
                        <div>
                            <label htmlFor="role" className="block text-sm font-medium text-gray-700">Cargo</label>
                            <select
                                id="role"
                                value={role}
                                onChange={(e) => setRole(e.target.value)}
                                className="mt-1 block w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:ring-2 focus:ring-blue-400 outline-none"
                            >
                                <option value="">Selecione um cargo</option>
                                <option value="ANALISTA">Analista</option>
                                <option value="ADMIN">Admin</option>
                            </select>
                        </div>

                        {/* Mensagem de erro/sucesso */}
                        {message && (
                            <div
                                className={`text-sm text-center ${
                                    message.includes("sucesso") ? "text-green-600" : "text-red-600"
                                }`}
                            >
                                {message}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-400 transition-all"
                        >
                            Cadastrar
                        </button>
                    </form>

                    <p className="text-sm text-center text-gray-600 mt-4">
                        Já tem conta?{" "}
                        <Link to="/login" className="text-blue-600 hover:underline font-medium">
                            Login
                        </Link>
                    </p>
                </div>
            </div>
        );
}