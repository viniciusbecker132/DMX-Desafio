import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import "../index.css"

interface IndicatorDto {
    acumuladoChuva: number;
    mediaNDVI: number;
}

interface Talhao {
    id: number;
    name: string;
    municipio?: string;
}

export default function Indicadores() {
    const { token } = useAuth();

    const [talhoes, setTalhoes] = useState<Talhao[]>([]);
    const [talhaoId, setTalhaoId] = useState<number | "">("");
    const [plantio, setPlantio] = useState("");
    const [colheita, setColheita] = useState("");
    const [indicadores, setIndicadores] = useState<IndicatorDto | null>(null);

    // Buscar talhões
    const fetchTalhoes = async () => {
        if (!token) return;
        try {
            const res = await fetch("http://localhost:8080/talhao", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!res.ok) throw new Error("Erro ao buscar talhões");
            const data = await res.json();
            setTalhoes(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchTalhoes();
    }, [token]);

    // Buscar indicadores
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!talhaoId || !plantio || !colheita) return;

        if (new Date(plantio) > new Date(colheita)) {
            alert("A data de plantio não pode ser maior que a data de colheita.");
            return;
        }

        try {
            const url = `http://localhost:8080/indicadores/${talhaoId}?dataPlantio=${plantio}&dataColheita=${colheita}`;

            const res = await fetch(url, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) throw new Error("Erro ao buscar indicadores");

            const data = await res.json();
            setIndicadores(data);
        } catch (err) {
            console.error(err);
            alert("Erro ao buscar indicadores");
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <main className="max-w-4xl mx-auto p-8">
                <h2 className="text-3xl font-bold mb-6">Indicadores Agroambientais</h2>

                <form
                    onSubmit={handleSubmit}
                    className="bg-white p-6 rounded-xl shadow mb-6 space-y-4"
                >
                    <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                        {/* Seleção do Talhão */}
                        <select
                            value={talhaoId}
                            onChange={(e) => setTalhaoId(Number(e.target.value))}
                            className="p-2 border rounded"
                            required
                        >
                            <option value="">Selecione o Talhão</option>
                            {talhoes.map((t) => (
                                <option key={t.id} value={t.id}>
                                    {t.name}
                                </option>
                            ))}
                        </select>

                        {/* Datas */}
                        <input
                            type="date"
                            value={plantio}
                            onChange={(e) => setPlantio(e.target.value)}
                            className="p-2 border rounded"
                            required
                        />

                        <input
                            type="date"
                            value={colheita}
                            onChange={(e) => setColheita(e.target.value)}
                            className="p-2 border rounded"
                            required
                        />

                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                        >
                            Buscar
                        </button>
                    </div>
                </form>

                {indicadores && (
                    <div className="bg-white p-6 rounded-xl shadow">
                        <h3 className="text-xl font-semibold mb-4">Resultado</h3>
                        <p>
                            <strong>Chuva acumulada:</strong> {indicadores.acumuladoChuva} mm
                        </p>
                        <p>
                            <strong>Média NDVI:</strong> {indicadores.mediaNDVI}
                        </p>
                    </div>
                )}
            </main>
        </div>
    );
}
