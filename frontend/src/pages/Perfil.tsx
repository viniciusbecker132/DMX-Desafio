import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import "../index.css"

export default function ProfilePage() {
    const { user } = useAuth();

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <main className="max-w-3xl mx-auto p-8">
                <h2 className="text-3xl font-bold text-gray-800 mb-6">
                    Bem-vindo, {user?.email || "Usuário"}!
                </h2>

                <div className="bg-white p-6 rounded-xl shadow">
                    <p><strong>Email:</strong> {user?.email}</p>
                    <p><strong>Papel:</strong> {user?.role}</p>
                </div>
            </main>
        </div>
    );
}