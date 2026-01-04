// src/Navbar.js
import React from "react";
import { NavLink } from "react-router-dom";

function Navbar() {
  return (
    <nav className="bg-pink-200 shadow p-4 flex justify-between items-center">
      <div className="text-2xl font-bold text-pink-700">Resplenda</div>

      <div className="space-x-6">
        <NavLink
          to="/home"
          className={({ isActive }) =>
            isActive ? "text-white font-bold" : "text-pink-700 hover:text-white"
          }
        >
          Home
        </NavLink>

        <NavLink
          to="/inventory"
          className={({ isActive }) =>
            isActive ? "text-white font-bold" : "text-pink-700 hover:text-white"
          }
        >
          Inventory
        </NavLink>

        <NavLink
          to="/wishlist"
          className={({ isActive }) =>
            isActive ? "text-white font-bold" : "text-pink-700 hover:text-white"
          }
        >
          Wishlist
        </NavLink>

          <NavLink 
          to="/looks" 
          className={({isActive}) =>
           isActive ? "text-white font-bold" : "text-pink-700 hover:text-white"
        }
        >
          Looks 
          </NavLink>



      </div>

      <div>
        <button
          className="bg-pink-400 text-white px-3 py-1 rounded"
          onClick={() => {
            localStorage.clear();
            window.location.href = "/login";
          }}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
