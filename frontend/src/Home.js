// src/Home.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import SkinQuizPopup from "./SkinQuizPopup";

function Home() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [loading, setLoading] = useState(true);
  const [showQuiz, setShowQuiz] = useState(false);

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  const navigate = useNavigate();

  const productTypesCats = [
    "Blush", "Bronzer", "Eyebrow", "Eyeliner", "Eyeshadow",
    "Foundation", "Lipliner", "Lipstick", "Mascara", "Nail polish"
  ];

  const fetchProducts = async (cat = category, searchTerm = search) => {
    if (!token) return;

    setLoading(true);
    try {
      let url = "";

      if (!cat && !searchTerm.trim()) {
        url = `http://localhost:8080/api/recommendations/${userId}`;
      } else if (cat) {
        url = `http://localhost:8080/api/products?productType=${encodeURIComponent(cat)}`;
      } else if (searchTerm.trim()) {
        url = `http://localhost:8080/api/products/search?search=${encodeURIComponent(searchTerm)}`;
      }

      const res = await fetch(url, {
        headers: { 
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Failed to fetch products");
      const data = await res.json();
      setProducts(data || []);
    } catch (err) {
      console.error("Error fetching products:", err);
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts(category);
  }, [category]);

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      if (search.trim()) {
        fetchProducts(category, search);
      }
    }, 500);

    return () => clearTimeout(delayDebounce);
  }, [search]);

  const handleSearch = (e) => setSearch(e.target.value);

  const handleCategory = (cat) => {
    if (category === cat) setCategory("");
    else setCategory(cat);
  };

  const handleQuizComplete = () => {
    setShowQuiz(false);
    localStorage.setItem("showQuiz", "false");
    fetchProducts("", "");
  };

  return (
    <div className="min-h-screen bg-pink-50 flex p-6">
      {/* Sidebar categories */}
      <aside className="w-56 mr-8">
        <h2 className="text-2xl font-extrabold text-pink-600 tracking-wide mb-5">
          Categories
        </h2>
        <div className="space-y-3">
          {productTypesCats.map((cat) => (
            <div
              key={cat}
              onClick={() => handleCategory(cat)}
              className={`p-3 rounded-lg font-bold shadow-sm text-center transition-all duration-200 cursor-pointer
                ${category === cat 
                  ? "bg-pink-400 text-white shadow-md scale-105" 
                  : "bg-white hover:bg-pink-100 text-gray-700"}`}
            >
              {cat}
            </div>
          ))}
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 overflow-y-auto">
        {/* Retake Skin Quiz Button */}
        <div className="flex justify-end mb-4">
          <button
            onClick={() => setShowQuiz(true)}
            className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-700 transition"
          >
            🔄 Retake Skin Quiz
          </button>
        </div>

        {/* Search bar */}
        <input
          type="text"
          placeholder="Search products..."
          value={search}
          onChange={handleSearch}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              fetchProducts(category, search);
            }
          }}
          className="border p-2 rounded w-full mb-12"
        />

        {/* Product grid */}
        {loading ? (
          <p>Loading products...</p>
        ) : products.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {products.map((p, index) => (
              <div
                key={`${p.name}-${p.brand}-${index}`}
                className="bg-white rounded-xl shadow border border-pink-100 p-3"
                onClick={() => navigate("/product/productKey", { state: { product: p, userId } })}
              >
                <img
                  src={p.image_link || p.api_featured_image || "https://via.placeholder.com/150"}
                  alt={p.name}
                  className="w-full h-32 object-cover rounded mb-2"
                />
                <p className="mt-2 text-center text-sm font-medium text-pink-700">
                  {p.name}
                </p>
              </div>
            ))}
          </div>
        ) : (
          <p>No products found.</p>
        )}
      </div>

      {/* Skin Quiz Popup */}
      {showQuiz && (
        <SkinQuizPopup
          userId={userId}
          token={token}
          onComplete={handleQuizComplete}
        />
      )}
    </div>
  );
}

export default Home;