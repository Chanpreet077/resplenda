import React, { useEffect, useState } from "react";

function Wishlist() {
  const [wishlist, setWishlist] = useState([]);
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  console.log("Wishlist.js mounted");
  console.log("UserId:", userId);
  console.log("Token:", token);

  useEffect(() => {
    const fetchWishlist = async () => {
      console.log("Fetching wishlist for user:", userId);
      try {
        const res = await fetch(`http://localhost:8080/api/wishlist/all/${userId}`, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("Wishlist fetch response status:", res.status);
        if (!res.ok) throw new Error("Failed to fetch wishlist");

        const wishlistData = await res.json();
        console.log("Wishlist data received:", wishlistData);
        setWishlist(wishlistData);

        // ✨ Enrichment: fetch product info from Makeup API
        const enrichedWishlist = await Promise.all(
          wishlistData.map(async (item) => {
            try {
              const [brand, ...nameParts] = item.productKey.split("-");
              const name = nameParts.join(" ");

              const apiRes = await fetch(
                `https://makeup-api.herokuapp.com/api/v1/products.json?brand=${encodeURIComponent(
                  brand
                )}`
              );
              if (!apiRes.ok) throw new Error("Makeup API fetch failed");

              const products = await apiRes.json();
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

        console.log("Enriched wishlist:", enrichedWishlist);
        setWishlist(enrichedWishlist);
      } catch (err) {
        console.error("Error fetching wishlist:", err);
      }
    };

    fetchWishlist();
  }, [userId, token]);

  // 💔 Remove from wishlist
  const handleDelete = async (userId, productKey) => {
    console.log("Removing wishlist item:", productKey);
    try {
      const res = await fetch(
        `http://localhost:8080/api/wishlist/${userId}/${encodeURIComponent(productKey)}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log("Delete response status:", res.status);
      if (res.ok) {
        console.log("Removed item locally:", productKey);
        setWishlist((prev) => prev.filter((item) => item.productKey !== productKey));
      } else {
        console.error("Failed to remove item");
      }
    } catch (err) {
      console.error("Error removing item:", err);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-pink-50 to-pink-200 p-6">
      <h1 className="text-4xl font-extrabold text-pink-600 text-center mb-10 tracking-wide">
        💖 My Wishlist
      </h1>

      {/* Wishlist grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
        {wishlist.map((item) => (
          <div
            key={item.productKey}
            className="bg-white rounded-2xl shadow-xl p-6 flex flex-col items-center hover:scale-105 transition-transform border-2 border-pink-200"
          >
            <img
                src={ item.product?.api_featured_image || item.product?.image_link || "https://placehold.co/150x150?text=No+Image"}
              className="w-36 h-36 object-cover rounded-full border-4 border-pink-300 mb-4"
              alt={item.product?.name || "Wishlist item"}
            />
            <h2 className="text-xl font-bold text-pink-700 text-center">
              {item.product?.name || item.productKey}
            </h2>
            <p className="text-sm text-gray-500 mt-2 text-center italic">
              {item.product?.brand || "Unknown brand"}
            </p>

            {/* Remove button */}
            <button
              onClick={() => handleDelete(userId, item.productKey)}
              className="mt-6 px-5 py-2 bg-pink-400 text-white text-sm font-semibold rounded-full hover:bg-pink-500 transition-colors"
            >
              Remove from Wishlist
            </button>
          </div>
        ))}
      </div>

      {wishlist.length === 0 && (
        <p className="text-center text-gray-500 mt-12 text-lg">
          Your wishlist is empty... time to dream big 💭💋
        </p>
      )}
    </div>
  );
}

export default Wishlist;