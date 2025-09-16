import { Link } from "react-router-dom";

export default function Navbar() {
    return (
        <header className="w-full bg-blue-600 text-white shadow-md">
            <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
                <h1 className="text-xl font-bold">DMX Agro</h1>

                <nav className="space-x-6">
                    <Link to="/perfil" className="hover:underline">Perfil</Link>
                    <Link to="/propriedades" className="hover:underline">Propriedades</Link>
                    <Link to="/talhao" className="hover:underline">Talhões</Link>
                    <Link to="/safra" className="hover:underline">Safra</Link>
                    <Link to="/indicadores" className="hover:underline">Indicadores</Link>
                </nav>
            </div>
        </header>
    );
}