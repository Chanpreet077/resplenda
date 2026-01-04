// src/ProductPage.js
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

function ProductPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { product } = location.state || {};

  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");
  const productKey = product ? `${product.brand || "unknown"}-${product.name}` : null;

  const [rating, setRating] = useState(0);
  const [showDatePrompt, setShowDatePrompt] = useState(false);
  const [openDate, setOpenDate] = useState("");

  // 🔥 ADDED: State for Looks dropdown
  const [looks, setLooks] = useState([]);
  const [selectedLook, setSelectedLook] = useState("");

  console.log("ProductPage mounted");
  console.log("UserId:", userId);
  console.log("Token:", token);
  console.log("Product:", product);
  console.log("Computed productKey:", productKey);

useEffect(() => {
  if (!userId || !productKey || !token) {
    console.warn("Missing userId, productKey, or token. Skipping fetchRating.");
    return;
  }

  const fetchRating = async () => {
    console.log("Fetching rating for", productKey);
    try {
      const res = await fetch(
        `http://localhost:8080/api/ratings/${userId}/product?productKey=${encodeURIComponent(productKey)}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log("Rating fetch status:", res.status);

      if (res.status === 404 || res.status === 204) {
        console.log("No rating found for this product.");
        setRating(0); // fallback for unrated products
        return;
      }

      if (!res.ok) {
        console.warn("Failed to fetch rating:", res.statusText);
        return;
      }

      const data = await res.json();
      console.log("Rating data received:", data);

      // Accept 0-star ratings too
      if (data?.stars !== undefined && data?.stars !== null) {
        setRating(data.stars);
      } else {
        setRating(0); // fallback if stars is missing
      }
    } catch (err) {
      console.error("Error fetching rating:", err);
    }
  };

  fetchRating();
}, [userId, productKey, token]);

  // 🔥 ADDED: Fetch existing looks
  useEffect(() => {
    const fetchLooks = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/looks/all/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await res.json();
        setLooks(data);
      } catch (err) {
        console.error("Error fetching looks:", err);
      }
    };
    fetchLooks();
  }, [userId, token]);

  if (!product) {
    console.warn("Product not found in location.state");
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-lg text-red-500">Product not found.</p>
        <button
          onClick={() => navigate("/home")}
          className="mt-4 px-4 py-2 bg-pink-500 text-white rounded hover:bg-pink-700"
        >
          Back to Home
        </button>
      </div>
    );
  }

  const handleRating = async (value) => {
    console.log("handleRating called:", value);
    setRating(value);

    if (userId && productKey) {
      localStorage.setItem(`rating-${userId}-${productKey}`, value);
      console.log("Saved rating to localStorage");
    }

    if (userId && productKey && token) {
      try {
        console.log("Sending rating to backend:", { productKey, stars: value });
        const res = await fetch(`http://localhost:8080/api/ratings/${userId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            productKey,
            productName: product.name,
            brand: product.brand,
            stars: value,
          }),
        });
        console.log("Rating save status:", res.status);
        if (!res.ok) {
          console.warn("Failed to save rating:", await res.text());
        }
      } catch (err) {
        console.error("Error saving rating:", err);
      }
    }
  };

 const handleConfirmInventory = async () => {
  console.log("handleConfirmInventory called. OpenDate:", openDate);
  if (!openDate) {
    alert("Please select an open date.");
    return;
  }

  const inventoryKey = `inventory-${userId}-${productKey}`;
  const expiryDate = new Date(openDate);


  const EXPIRY_MONTHS = {
    bronzer: 24,
    eyebrow: 12,
    eyeliner: 12,
    eyeshadow: 24,
    foundation: 18,
    lipliner: 18,
    lipstick: 18,
    "nail polish": 30,
  };

  // normalize product type
  const type = product.product_type ? product.product_type.toLowerCase() : null;
  const monthsToAdd = type && EXPIRY_MONTHS[type] ? EXPIRY_MONTHS[type] : 6; // fallback = 6 months

  expiryDate.setMonth(expiryDate.getMonth() + monthsToAdd);
  const formattedExpiryDate = expiryDate.toISOString().split("T")[0];

  localStorage.setItem(
    inventoryKey,
    JSON.stringify({
      productName: product.name,
      brand: product.brand,
      openDate,
      expiryDate: formattedExpiryDate,
    })
  );
  console.log("Saved inventory locally:", { inventoryKey, product });


    try {
      const res = await fetch(`http://localhost:8080/api/inventory/add/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          productKey,
          productName: product.name,
          productType: product.productType,
          openDate,
          expiryDate: formattedExpiryDate
        }),
      });

      console.log("Inventory save status:", res.status);
      if (!res.ok) {
        const text = await res.text();
        console.error("Failed to save inventory:", text);
        alert("❌ Failed to save inventory.");
        return;
      }

      alert("Inventory item saved!");
      setShowDatePrompt(false);
      setOpenDate("");
    } catch (err) {
      console.error("Error saving inventory:", err);
      alert("Failed to save inventory.");
    }
  };

  const handleConfirmWishlist = async () => {
    console.log("handleConfirmWishlist called");

    const wishlistKey = `wishlist-${userId}-${productKey}`;

    localStorage.setItem(wishlistKey, JSON.stringify({
      productName: product.name,
      brand: product.brand
    }));
    console.log("Saved wishlist locally:", { wishlistKey, product });

    try {
      const res = await fetch(`http://localhost:8080/api/wishlist/add/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          productKey,
          productName: product.name,
          brand: product.brand
        }),
      });

      console.log("Wishlist save status:", res.status);
      if (!res.ok) {
        const text = await res.text();
        console.error("Failed to save wishlist:", text);
        alert("Failed to save wishlist.");
        return;
      }

      alert("Wishlist item saved!");
    } catch (err) {
      console.error("Error saving wishlist:", err);
      alert("Failed to save wishlist.");
    }
  };

 
  const handleAddToLook = async () => {
    if (!selectedLook) {
      alert("Please select a look.");
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/looks/add/${userId}/${selectedLook}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ productKey }),
      });

      if (res.ok) {
        alert(`🎀 Added to "${selectedLook}"`);
        setSelectedLook("");
      } else {
        alert("Failed to add to look.");
      }
    } catch (err) {
      console.error("Error adding to look:", err);
      alert("Error adding to look.");
    }
  };

  return (
    <div className="min-h-screen bg-pink-50 flex justify-center p-6">
      <div className="bg-white rounded-2xl shadow-lg p-6 w-full max-w-3xl">
        <button
          onClick={() => navigate(-1)}
          className="mb-4 px-3 py-1 bg-pink-200 rounded hover:bg-pink-300"
        >
          ← Back
        </button>
        <div className="flex flex-col md:flex-row gap-6">
          <img
            src={product.image_link || product.api_featured_image || "https://via.placeholder.com/300"}
            alt={product.name}
            className="w-full md:w-1/2 h-64 object-cover rounded"
          />
          <div className="flex-1">
            <h2 className="text-2xl font-bold text-pink-600">{product.name}</h2>
            <p className="text-gray-700 mt-2">Brand: {product.brand}</p>
            <p className="text-gray-700 mt-2">Type: {product.product_type}</p>
            
           


<div className="mt-3 flex items-center space-x-1">
  {[1, 2, 3, 4, 5].map((star) => (
    <span
      key={star}
      onClick={() => handleRating(star)}
      className={`cursor-pointer text-2xl ${
        rating !== null && star <= rating ? "text-yellow-400" : "text-gray-300"
      }`}
    >
      ★
    </span>
  ))}
</div>


            {product.tag_list?.length > 0 && (
              <p className="mt-2">Tags: {product.tag_list.join(", ")}</p>
            )}

            {product.description && (
              <div className="mt-4 text-gray-600" dangerouslySetInnerHTML={{ __html: product.description }} />
            )}

            
            <div className="mt-6 flex flex-wrap gap-3 items-center">
              <button
                onClick={() => setShowDatePrompt(true)}
                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-700"
              >
                📦 Add to Inventory
              </button>

              <button
                onClick={handleConfirmWishlist}
                className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-700"
              >
                💖 Add to Wishlist
              </button>

              <div className="flex items-center gap-2">
                <select
                  value={selectedLook}
                  onChange={(e) => setSelectedLook(e.target.value)}
                  className="px-3 py-2 rounded border border-purple-300 bg-white text-sm"
                >
                  <option value="">Select Look</option>
                  {looks.map((look) => (
                    <option key={look.id} value={look.lookName}>
                      {look.lookName}
                    </option>
                  ))}
                </select>

                <button
                  onClick={handleAddToLook}
                  className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-700 text-sm"
                >
                  🎀 Add to Look
                </button>
              </div>

              {product.product_link && (
                <a
                  href={product.product_link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="px-4 py-2 bg-pink-500 text-white rounded hover:bg-pink-700"
                >
                  View Original
                </a>
              )}
            </div>

            {showDatePrompt && (
              <div className="mt-6">
                <label className="block text-sm font-medium text-gray-700">Select Open Date:</label>
                <input
                  type="date"
                  value={openDate}
                  onChange={(e) => setOpenDate(e.target.value)}
                  className="mt-1 p-2 border rounded w-full"
                />
                <button
                  onClick={handleConfirmInventory}
                  className="mt-3 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-700"
                >
                  Confirm Add to Inventory
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductPage;