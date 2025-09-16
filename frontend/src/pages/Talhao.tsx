import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import "../index.css"

interface Property {
    id: number;
    name: string;
}

interface Talhao {
    id: number;
    name: string;
    area: number;
    propriedade: Property;
}

export default function Talhao() {
    const { user, token } = useAuth();
    const [talhao, setTalhao] = useState<Talhao[]>([]);
    const [properties, setProperties] = useState<Property[]>([]);
    const [name, setName] = useState("");
    const [area, setArea] = useState<number | "">("");
    const [propertyId, setPropertyId] = useState<number | "">("");
    const [file, setFile] = useState<File | null>(null);
    const [editingId, setEditingId] = useState<number | null>(null);

    // Paginação
    const [page, setPage] = useState(1); // página atual (começa em 1)
    const pageSize = 5; // itens por página

    // Buscar talhões
    const fetchTalhao = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/talhao", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao buscar talhões");
            const data = await res.json();
            setTalhao(data);
        } catch (err) {
            console.error(err);
        }
    };

    // Buscar propriedades para selecionar
    const fetchProperties = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/propriedades", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao buscar propriedades");
            const data = await res.json();
            setProperties(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchTalhao();
        fetchProperties();
    }, [token]);

    // Criar / atualizar talhão
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!name || !area || !propertyId) return;

        const method = editingId ? "PUT" : "POST";
        const url = editingId
            ? `http://localhost:8080/talhao/${editingId}`
            : "http://localhost:8080/talhao";

        try {
            const res = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    name,
                    area,
                    propriedade: { id: propertyId },
                }),
            });

            if (!res.ok) throw new Error("Erro ao salvar talhão");

            setName("");
            setArea("");
            setPropertyId("");
            setEditingId(null);
            fetchTalhao();
        } catch (err) {
            console.error(err);
            alert("Não foi possível salvar o talhão.");
        }
    };

    // Editar
    const handleEdit = (talhao: Talhao) => {
        setEditingId(talhao.id);
        setName(talhao.name);
        setArea(talhao.area);
        setPropertyId(talhao.propriedade?.id || "");
    };

    // Deletar
    const handleDelete = async (id: number) => {
        if (!confirm("Deseja realmente deletar?")) return;

        try {
            const res = await fetch(`http://localhost:8080/talhao/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao deletar talhão");
            fetchTalhao();
        } catch (err) {
            console.error(err);
            alert("Erro ao deletar talhão.");
        }
    };

    // Upload CSV
    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        try {
            const res = await fetch("http://localhost:8080/talhao/import", {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` },
                body: formData,
            });

            if (!res.ok) throw new Error("Erro ao importar CSV");

            setFile(null);
            fetchTalhao();
            alert("CSV importado com sucesso!");
        } catch (err) {
            console.error(err);
            alert("Falha ao importar CSV.");
        }
    };

    // Calcula total de páginas
    const totalPages = Math.ceil(talhao.length / pageSize);

    // Talhões da página atual
    const pagedTalhao = talhao.slice((page - 1) * pageSize, page * pageSize);
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <main className="max-w-4xl mx-auto p-8">
                <h2 className="text-3xl font-bold text-gray-800 mb-6">Talhões</h2>

                {/* Formulário */}
                {user?.role === "ADMIN" && (
                    <form
                        onSubmit={handleSubmit}
                        className="bg-white p-6 rounded-xl shadow mb-6 space-y-4"
                    >
                        <h3 className="text-xl font-semibold">Cadastrar / Editar Talhão</h3>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <input
                                type="text"
                                placeholder="Nome"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="p-2 border rounded"
                                required
                            />
                            <input
                                type="number"
                                placeholder="Área"
                                value={area}
                                onChange={(e) => setArea(e.target.value === "" ? "" : Number(e.target.value))}
                                className="p-2 border rounded"
                                required
                            />
                            <select
                                value={propertyId}
                                onChange={(e) => setPropertyId(e.target.value === "" ? "" : Number(e.target.value))}
                                className="p-2 border rounded"
                                required
                            >
                                <option value="">Selecione a propriedade</option>
                                {properties.map((p) => (
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

                {/* Import CSV */}
                {user?.role === "ADMIN" && (
                    <div className="bg-white p-6 rounded-xl shadow mb-6">
                        <h3 className="text-xl font-semibold mb-2">Importar CSV</h3>
                        <input type="file" accept=".csv" onChange={handleFileChange} />
                        <button
                            onClick={handleUpload}
                            className="ml-2 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
                        >
                            Importar
                        </button>
                    </div>
                )}

                {/* Lista de Talhões */}
                <div className="bg-white p-6 rounded-xl shadow">
                    <h3 className="text-xl font-semibold mb-4">Lista de Talhões</h3>
                    <table className="w-full table-auto border border-gray-300">
                        <thead>
                        <tr className="bg-gray-200">
                            <th className="border px-4 py-2">Nome</th>
                            <th className="border px-4 py-2">Área</th>
                            <th className="border px-4 py-2">Propriedade</th>
                            {user?.role === "ADMIN" && <th className="border px-4 py-2">Ações</th>}
                        </tr>
                        </thead>
                        <tbody>
                        {pagedTalhao.map((t) => (
                            <tr key={t.id}>
                                <td className="border px-4 py-2">{t.name}</td>
                                <td className="border px-4 py-2">{t.area}</td>
                                <td className="border px-4 py-2">{t.propriedade?.name}</td>
                                {user?.role === "ADMIN" && (
                                    <td className="border px-4 py-2 space-x-2">
                                        <button
                                            onClick={() => handleEdit(t)}
                                            className="bg-yellow-500 text-white px-2 py-1 rounded hover:bg-yellow-600"
                                        >
                                            Editar
                                        </button>
                                        <button
                                            onClick={() => handleDelete(t.id)}
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
