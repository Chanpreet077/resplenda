import React, { useEffect, useState } from "react";

function Looks() {
  const [looks, setLooks] = useState([]);
  const [newLookName, setNewLookName] = useState("");
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchLooks = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/looks/all/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!res.ok) throw new Error("Failed to fetch looks");
        const data = await res.json();
        setLooks(data);
      } catch (err) {
        console.error("Error fetching looks:", err);
      }
    };

    fetchLooks();
  }, [userId, token]);

  const handleCreateLook = async () => {
    if (!newLookName.trim()) return;
    try {
      const res = await fetch(`http://localhost:8080/api/looks/create/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ lookName: newLookName }),
      });
      if (!res.ok) throw new Error("Failed to create look");
      const newLook = await res.json();
      setLooks((prev) => [...prev, newLook]);
      setNewLookName("");
    } catch (err) {
      console.error("Error creating look:", err);
    }
  };

  const handleDeleteLook = async (lookName) => {
    try {
      const res = await fetch(`http://localhost:8080/api/looks/${userId}/${lookName}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (res.ok) {
        setLooks((prev) => prev.filter((look) => look.lookName !== lookName));
      } else {
        console.error("Failed to delete look");
      }
    } catch (err) {
      console.error("Error deleting look:", err);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-purple-50 to-pink-100 p-6">
      <h1 className="text-4xl font-extrabold text-purple-600 text-center mb-10 tracking-wide">
        🎀 My Looks
      </h1>

      {/* Create new look */}
      <div className="flex justify-center mb-8 gap-4">
        <input
          type="text"
          value={newLookName}
          onChange={(e) => setNewLookName(e.target.value)}
          placeholder="Name your new look..."
          className="px-4 py-2 rounded-full border-2 border-purple-300 focus:outline-none focus:ring-2 focus:ring-purple-400 w-64"
        />
        <button
          onClick={handleCreateLook}
          className="px-5 py-2 bg-purple-400 text-white font-semibold rounded-full hover:bg-purple-500 transition-colors"
        >
          Create Look
        </button>
      </div>

      {/* Looks grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
        {looks.map((look) => (
          <div
            key={look.id}
            className="bg-white rounded-2xl shadow-xl p-6 border-2 border-purple-200 hover:scale-105 transition-transform"
          >
            <h2 className="text-xl font-bold text-purple-700 text-center mb-2">
              {look.lookName}
            </h2>

            <ul className="text-sm text-gray-600 list-disc list-inside mb-4">
              {look.productKeys.map((key, index) => (
                <li key={index} className="italic">{key}</li>
              ))}
            </ul>

            <button
              onClick={() => handleDeleteLook(look.lookName)}
              className="mt-4 px-4 py-2 bg-red-400 text-white text-sm font-semibold rounded-full hover:bg-red-500 transition-colors"
            >
              Delete Look
            </button>
          </div>
        ))}
      </div>

      {looks.length === 0 && (
        <p className="text-center text-gray-500 mt-12 text-lg">
          No looks created yet... time to curate your glam 💫
        </p>
      )}
    </div>
  );
}

export default Looks;