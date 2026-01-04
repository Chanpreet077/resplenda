// src/SkinQuizPopup.js
import React, { useState } from "react";

function SkinQuizPopup({ userId, token, onComplete }) {
  const [skinType, setSkinType] = useState("");
  const [finish, setFinish] = useState("");
  const [productTypes, setProductTypes] = useState([]);
  const [extraTags, setExtraTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [recommended, setRecommended] = useState([]);

  const getTokenOrThrow = () => {
    // Prefer localStorage (most reliable). Fall back to prop if you really want.
    const stored = localStorage.getItem("token");
    const t = stored || token;

    if (!t || t === "undefined") {
      throw new Error("Missing JWT token. Login token not stored / not passed.");
    }
    return t;
  };

  const handleSubmit = async () => {
    if (!skinType || !finish) {
      alert("Please answer all required questions");
      return;
    }

    setLoading(true);

    try {
      const jwt = getTokenOrThrow();
      console.log("Submitting quiz with token present:", jwt.slice(0, 20) + "...");

      // 1) Save quiz
      const quizRes = await fetch(`http://localhost:8080/api/quiz`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`,
        },
        body: JSON.stringify({
          skinType,
          finish,
          productPref: productTypes.filter(Boolean),
          tagPref: extraTags.filter(Boolean),
        }),
      });

      if (!quizRes.ok) {
        // read body if backend sends something (sometimes empty)
        let msg = "";
        try {
          msg = await quizRes.text();
        } catch {}
        throw new Error(`Quiz save failed: ${quizRes.status} ${msg}`);
      }

      // 2) Fetch recommendations
      const recRes = await fetch(`http://localhost:8080/api/recommendations`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!recRes.ok) {
        let msg = "";
        try {
          msg = await recRes.text();
        } catch {}
        throw new Error(`Recommendations failed: ${recRes.status} ${msg}`);
      }

      const recData = await recRes.json();
      console.log("Received recommendations:", recData);
      setRecommended(recData);
    } catch (err) {
      console.error(err);
      alert(err.message || "Something went wrong while saving quiz or fetching recommendations.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center">
      <div className="bg-white rounded-xl shadow-lg p-6 w-96">
        <h2 className="text-xl font-bold mb-4 text-pink-700">Skin Quiz</h2>

        <label className="block mb-2 font-medium">Skin Type</label>
        <select
          value={skinType}
          onChange={(e) => setSkinType(e.target.value)}
          className="w-full p-2 border rounded mb-4"
        >
          <option value="">Select...</option>
          <option value="oily">Oily</option>
          <option value="dry">Dry</option>
          <option value="combination">Combination</option>
          <option value="sensitive">Sensitive</option>
          <option value="normal">Normal</option>
        </select>

        <label className="block mb-2 font-medium">Preferred Finish</label>
        <select
          value={finish}
          onChange={(e) => setFinish(e.target.value)}
          className="w-full p-2 border rounded mb-4"
        >
          <option value="">Select...</option>
          <option value="matte">Matte</option>
          <option value="dewy">Dewy</option>
        </select>

        <label className="block mb-2 font-medium">Product Types</label>
        <input
          type="text"
          placeholder="e.g. foundation, lipstick"
          value={productTypes.join(", ")}
          onChange={(e) =>
            setProductTypes(
              e.target.value
                .split(",")
                .map((s) => s.trim())
                .filter(Boolean)
            )
          }
          className="w-full p-2 border rounded mb-4"
        />

        <label className="block mb-2 font-medium">Extra Preferences</label>
        <input
          type="text"
          placeholder="e.g. fragrance free, oil free"
          value={extraTags.join(", ")}
          onChange={(e) =>
            setExtraTags(
              e.target.value
                .split(",")
                .map((s) => s.trim())
                .filter(Boolean)
            )
          }
          className="w-full p-2 border rounded mb-4"
        />

        <button
          onClick={handleSubmit}
          className="bg-pink-600 hover:bg-pink-700 text-white px-4 py-2 rounded w-full"
          disabled={loading}
        >
          {loading ? "Saving..." : "Submit Quiz"}
        </button>

        {recommended.length > 0 && (
          <div className="mt-4">
            <h3 className="font-bold mb-2">Recommended Products</h3>
            <div className="max-h-40 overflow-y-auto">
              {recommended.map((p, index) => (
                <div key={`${p.name}-${p.brand}-${index}`} className="border-b py-1">
                  {p.name}
                </div>
              ))}
            </div>

            <button
              onClick={onComplete}
              className="mt-3 bg-green-600 hover:bg-green-700 text-white px-3 py-2 rounded w-full"
            >
              Continue to Shop
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default SkinQuizPopup;
