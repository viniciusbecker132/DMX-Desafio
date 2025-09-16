import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import "../index.css"

interface Safra {
    id: number;
    cultivation: string;
    year: number;
    planting: string;
    harvest: string;
    talhao: Talhao
}
interface Talhao {
    id: number;
    name: string;
}

export default function Safra() {
    const { user, token } = useAuth();
    const [safras, setSafras] = useState<Safra[]>([]);
    const [talhao, setTalhao] = useState<Talhao[]>([]);
    const [cultivation, setCultivation] = useState("");
    const [year, setYear] = useState<number | "">("");
    const [planting, setPlanting] = useState("");
    const [harvest, setHarvest] = useState("");
    const [talhaoId, setTalhaoId] = useState<number | "">("");
    const [editingId, setEditingId] = useState<number | null>(null);


    const [page, setPage] = useState(1); // página atual
    const pageSize = 5; // itens por página

    // Buscar safras
    const fetchSafras = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/safra", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao buscar safras");
            const data = await res.json();
            setSafras(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchSafras();
    }, [token]);

    // Criar/atualizar safra
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!cultivation || !year || !planting || !harvest || !talhaoId) return;

        const method = editingId ? "PUT" : "POST";
        const url = editingId
            ? `http://localhost:8080/safra/${editingId}`
            : "http://localhost:8080/safra";

        try {
            const res = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    cultivation,
                    year,
                    planting,
                    harvest,
                    talhao: { id: talhaoId },
                }),
            });

            if (!res.ok) throw new Error("Erro ao salvar safra");

            resetForm();
            fetchSafras();
        } catch (err) {
            console.error(err);
            alert("Não foi possível salvar a safra.");
        }
    };
    // Buscar propriedades para selecionar
    const fetchTalhao = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/talhao", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao buscar propriedades");
            const data = await res.json();
            setTalhao(data);
        } catch (err) {
            console.error(err);
        }
    };
    useEffect(() => {
        fetchTalhao();
    }, [token]);

    const resetForm = () => {
        setCultivation("");
        setYear("");
        setPlanting("");
        setHarvest("");
        setTalhaoId("");
        setEditingId(null);
    };

    // Editar safra
    const handleEdit = (safra: Safra) => {
        setEditingId(safra.id);
        setCultivation(safra.cultivation);
        setYear(safra.year);
        setPlanting(safra.planting);
        setHarvest(safra.harvest);
        setTalhaoId(safra.talhao.id);
    };

    // Deletar
    const handleDelete = async (id: number) => {
        if (!confirm("Deseja realmente deletar esta safra?")) return;
        try {
            const res = await fetch(`http://localhost:8080/safra/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao deletar safra");
            fetchSafras();
        } catch (err) {
            console.error(err);
            alert("Erro ao deletar safra.");
        }
    };

    // Calcula total de páginas
    const totalPages = Math.ceil(safras.length / pageSize);

    // Safras da página atual
    const pagedSafras = safras.slice((page - 1) * pageSize, page * pageSize);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <main className="max-w-5xl mx-auto p-8">
                <h2 className="text-3xl font-bold text-gray-800 mb-6">Safras</h2>

                {/* Formulário */}
                {user?.role === "ADMIN" && (
                    <form
                        onSubmit={handleSubmit}
                        className="bg-white p-6 rounded-xl shadow mb-6 space-y-4"
                    >
                        <h3 className="text-xl font-semibold">
                            {editingId ? "Editar Safra" : "Cadastrar Safra"}
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                            <input
                                type="text"
                                placeholder="Cultivo"
                                value={cultivation}
                                onChange={(e) => setCultivation(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="number"
                                placeholder="Ano"
                                value={year}
                                onChange={(e) => setYear(Number(e.target.value))}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="date"
                                value={planting}
                                onChange={(e) => setPlanting(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="date"
                                value={harvest}
                                onChange={(e) => setHarvest(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <select
                                value={talhaoId}
                                onChange={(e) => setTalhaoId(e.target.value === "" ? "" : Number(e.target.value))}
                                className="p-2 border rounded"
                                required
                            >
                                <option value="">Selecione a propriedade</option>
                                {talhao.map((p) => (
                                    <option key={p.id} value={p.id}>
                                        {p.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                        >
                            {editingId ? "Atualizar" : "Cadastrar"}
                        </button>
                    </form>
                )}

                {/* Lista */}
                <div className="bg-white p-6 rounded-xl shadow">
                    <h3 className="text-xl font-semibold mb-4">Lista de Safras</h3>
                    <table className="w-full table-auto border border-gray-300">
                        <thead>
                        <tr className="bg-gray-200">
                            <th className="border px-4 py-2">Cultivo</th>
                            <th className="border px-4 py-2">Ano</th>
                            <th className="border px-4 py-2">Plantio</th>
                            <th className="border px-4 py-2">Colheita</th>
                            <th className="border px-4 py-2">Talhão</th>
                            {user?.role === "ADMIN" && (
                                <th className="border px-4 py-2">Ações</th>
                            )}
                        </tr>
                        </thead>
                        <tbody>
                        {pagedSafras.map((s) => (
                            <tr key={s.id}>
                                <td className="border px-4 py-2">{s.cultivation}</td>
                                <td className="border px-4 py-2">{s.year}</td>
                                <td className="border px-4 py-2">{s.planting}</td>
                                <td className="border px-4 py-2">{s.harvest}</td>
                                <td className="border px-4 py-2">{s.talhao?.name}</td>
                                {user?.role === "ADMIN" && (
                                    <td className="border px-4 py-2 space-x-2">
                                        <button
                                            onClick={() => handleEdit(s)}
                                            className="bg-yellow-500 text-white px-2 py-1 rounded hover:bg-yellow-600"
                                        >
                                            Editar
                                        </button>
                                        <button
                                            onClick={() => handleDelete(s.id)}
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
                    <div className="flex justify-center items-center gap-4 mt-4">
                        <button
                            disabled={page === 1}
                            onClick={() => setPage((p) => p - 1)}
                            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Anterior
                        </button>
                        <span>
                            Página {page} de {totalPages || 1}
                        </span>
                        <button
                            disabled={page === totalPages || totalPages === 0}
                            onClick={() => setPage((p) => p + 1)}
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
