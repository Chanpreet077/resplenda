import React, { useEffect, useState } from "react";

function Inventory() {
  const [inventory, setInventory] = useState([]);
  const [newItem, setNewItem] = useState({ productKey: "", openDate: "" });
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  console.log("Inventory.js mounted");
  console.log("UserId:", userId);
  console.log("Token:", token);

  useEffect(() => {
    const fetchInventory = async () => {
      console.log("Fetching inventory for user:", userId);
      try {
        const res = await fetch(`http://localhost:8080/api/inventory/all/${userId}`, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("Inventory fetch response status:", res.status);
        if (!res.ok) throw new Error("Failed to fetch inventory");

        const inventoryData = await res.json();
        console.log("Inventory data received:", inventoryData);
        setInventory(inventoryData);

      
        const enrichedInventory = await Promise.all(
          inventoryData.map(async (item) => {
            try {
              // Split productKey into brand and name
              const [brand, ...nameParts] = item.productKey.split("-");
              const name = nameParts.join(" ");

              // Call Makeup API
              const apiRes = await fetch(
                `https://makeup-api.herokuapp.com/api/v1/products.json?brand=${encodeURIComponent(
                  brand
                )}`
              );
              if (!apiRes.ok) throw new Error("Makeup API fetch failed");

              const products = await apiRes.json();

              // Find matching product by name
              const matchedProduct = products.find(
                (p) => p.name.toLowerCase().trim() === name.toLowerCase().trim()
              );

              return { ...item, product: matchedProduct || null };
            } catch (err) {
              console.error(`Error enriching ${item.productKey}:`, err);
              return { ...item, product: null };
            }
          })
        );

        console.log("Enriched inventory:", enrichedInventory);
        setInventory(enrichedInventory);
      } catch (err) {
        console.error("Error fetching inventory:", err);
      }
    };

    fetchInventory();
  }, [userId, token]);

// Helper function to decode HTML entities like &trade;
const decodeHtmlEntity = (str) => {
  const txt = document.createElement("textarea");
  txt.innerHTML = str;
  return txt.value;
};

// Your delete function
const handleDelete = async (userId, productKey) => {
  console.log("🗑️ Deleting inventory item:", productKey);

  // ✅ Step 1: Decode any HTML entities
  const decodedKey = decodeHtmlEntity(productKey);

  // ✅ Step 2: Encode the decoded key for safe URL usage
  const safeKey = encodeURIComponent(decodedKey);

  try {
    const res = await fetch(
      `http://localhost:8080/api/inventory/${userId}/${safeKey}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );


      console.log("Delete response status:", res.status);
      if (res.ok) {
        console.log("Deleted item locally:", productKey);
        setInventory((prev) => prev.filter((item) => item.productKey !== productKey));
      } else {
        console.error("Failed to delete item");
      }
    } catch (err) {
      console.error("Error deleting item:", err);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-pink-100 to-pink-200 p-6">
      <h1 className="text-3xl font-bold text-pink-700 text-center mb-8">
        🧁 My Beauty Shelf
      </h1>

      {/* Inventory grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {inventory.map((item) => (
          <div
            key={item.productKey}
            className="bg-white rounded-xl shadow-lg p-4 flex flex-col items-center hover:scale-105 transition-transform"
          >
            <img
              src={item.product?.api_featured_image || "https://placehold.co/150x150?text=No+Image"}
              className="w-32 h-32 object-cover rounded-full border-4 border-pink-300 mb-4"
              alt={item.product?.name || "Product image"}
            />
            <h2 className="text-lg font-semibold text-pink-600">
              {item.product?.name || item.productKey}
            </h2>
            <p className="text-sm text-gray-500 mt-2">
              Opened: {item.openDate || "—"}
            </p>
            <p className="text-sm text-gray-500">
              Expires: {item.expiryDate || "—"}
            </p>

            {/* Delete button */}
            <button
              onClick={() => handleDelete(userId, item.productKey)}
              className="mt-4 px-4 py-2 bg-pink-500 text-white text-sm font-semibold rounded-lg hover:bg-pink-600 transition-colors"
            >
              Remove
            </button>
          </div>
        ))}
      </div>

      {inventory.length === 0 && (
        <p className="text-center text-gray-500 mt-10">
          Your shelf is empty... time to stock up 💅
        </p>
      )}
    </div>
  );
}

export default Inventory;