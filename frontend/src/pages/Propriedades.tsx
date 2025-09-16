import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import "../index.css"

interface Property {
    id: number;
    name: string;
    city: string;
    uf: string;
    area: number;
}

export default function Propriedades() {
    const { user, token } = useAuth();
    const [properties, setProperties] = useState<Property[]>([]);
    const [name, setName] = useState("");
    const [city, setCity] = useState("");
    const [uf, setUf] = useState("");
    const [area, setArea] = useState<number | "">("");
    const [editingId, setEditingId] = useState<number | null>(null);

    // filtro/paginação
    const [searchCity, setSearchCity] = useState("");
    const [searchUf, setSearchUf] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 5;

    const fetchProperties = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/propriedades", {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!res.ok) throw new Error("Erro ao buscar propriedades");
            const data = await res.json();
            setProperties(data);
        } catch (err) {
            console.error("Erro ao buscar propriedades:", err);
        }
    };

    useEffect(() => {
        fetchProperties();
    }, [token]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!name || !city || !uf || !area) return;
        const method = editingId ? "PUT" : "POST";
        const url = editingId
            ? `http://localhost:8080/propriedades/${editingId}`
            : "http://localhost:8080/propriedades";

        try {
            const res = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ name, city, uf, area }),
            });
            if (!res.ok) throw new Error("Erro ao salvar propriedade");
            setName("");
            setCity("");
            setUf("");
            setArea("");
            setEditingId(null);
            fetchProperties();
        } catch (err) {
            console.error(err);
            alert("Não foi possível salvar a propriedade.");
        }
    };

    const handleEdit = (property: Property) => {
        setEditingId(property.id);
        setName(property.name);
        setCity(property.city);
        setUf(property.uf);
        setArea(property.area);
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Deseja realmente deletar?")) return;
        try {
            const res = await fetch(`http://localhost:8080/propriedades/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao deletar propriedade");
            fetchProperties();
        } catch (err) {
            console.error(err);
            alert("Erro ao deletar propriedade.");
        }
    };

    // ---------- Filtro + Paginação ----------
    const filtered = properties.filter((p) => {
        const byCity = searchCity
            ? p.city.toLowerCase().includes(searchCity.toLowerCase())
            : true;
        const byUf = searchUf
            ? p.uf.toLowerCase().includes(searchUf.toLowerCase())
            : true;
        return byCity && byUf;
    });

    const totalPages = Math.ceil(filtered.length / pageSize);
    const paged = filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <main className="max-w-4xl mx-auto p-8">
                <h2 className="text-3xl font-bold text-gray-800 mb-6">Propriedades</h2>

                {user?.role === "ADMIN" && (
                    <form
                        onSubmit={handleSubmit}
                        className="bg-white p-6 rounded-xl shadow mb-6 space-y-4"
                    >
                        <h3 className="text-xl font-semibold">Cadastrar / Editar Propriedade</h3>
                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                            <input
                                type="text"
                                placeholder="Nome"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="text"
                                placeholder="Cidade"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="text"
                                placeholder="UF"
                                value={uf}
                                onChange={(e) => setUf(e.target.value.toUpperCase())}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="number"
                                placeholder="Área"
                                value={area}
                                onChange={(e) => setArea(Number(e.target.value))}
                                className="p-2 border rounded"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                        >
                            {editingId ? "Atualizar" : "Cadastrar"}
                        </button>
                    </form>
                )}

                {/* Filtros */}
                <div className="bg-white p-4 rounded shadow mb-4 flex gap-4">
                    <input
                        className="p-2 border rounded w-1/2"
                        placeholder="Filtrar por cidade"
                        value={searchCity}
                        onChange={(e) => {
                            setSearchCity(e.target.value);
                            setCurrentPage(1);
                        }}
                    />
                    <input
                        className="p-2 border rounded w-1/4"
                        placeholder="Filtrar por UF"
                        value={searchUf}
                        onChange={(e) => {
                            setSearchUf(e.target.value);
                            setCurrentPage(1);
                        }}
                    />
                </div>

                {/* Lista + Paginação */}
                <div className="bg-white p-6 rounded-xl shadow">
                    <h3 className="text-xl font-semibold mb-4">Lista de Propriedades</h3>
                    <table className="w-full table-auto border border-gray-300">
                        <thead>
                        <tr className="bg-gray-200">
                            <th className="border px-4 py-2">Nome</th>
                            <th className="border px-4 py-2">Cidade</th>
                            <th className="border px-4 py-2">UF</th>
                            <th className="border px-4 py-2">Área</th>
                            {user?.role === "ADMIN" && <th className="border px-4 py-2">Ações</th>}
                        </tr>
                        </thead>
                        <tbody>
                        {paged.map((prop) => (
                            <tr key={prop.id}>
                                <td className="border px-4 py-2">{prop.name}</td>
                                <td className="border px-4 py-2">{prop.city}</td>
                                <td className="border px-4 py-2">{prop.uf}</td>
                                <td className="border px-4 py-2">{prop.area}</td>
                                {user?.role === "ADMIN" && (
                                    <td className="border px-4 py-2 space-x-2">
                                        <button
                                            onClick={() => handleEdit(prop)}
                                            className="bg-yellow-500 text-white px-2 py-1 rounded hover:bg-yellow-600"
                                        >
                                            Editar
                                        </button>
                                        <button
                                            onClick={() => handleDelete(prop.id)}
                                            className="bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700"
                                        >
                                            Deletar
                                        </button>
                                    </td>
                                )}
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {/* Paginação */}
                    <div className="flex justify-center items-center gap-4 mt-4">
                        <button
                            disabled={currentPage === 1}
                            onClick={() => setCurrentPage((p) => p - 1)}
                            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Anterior
                        </button>
                        <span>
                            Página {currentPage} de {totalPages || 1}
                        </span>
                        <button
                            disabled={currentPage === totalPages || totalPages === 0}
                            onClick={() => setCurrentPage((p) => p + 1)}
                            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Próxima
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}
