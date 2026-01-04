// src/App.js
import React from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import Login from "./Login";
import NavBar from "./NavBar";
import SkinQuizPopup from "./SkinQuizPopup";
import Home from "./Home";
import Register from "./Register";
import ProductPage from "./ProductPage";
import Inventory from "./Inventory";
import Wishlist from "./Wishlist";
import Looks from './Looks'; 

function App() {

  const location = useLocation();

  // don’t show Navbar on login/register pages
  const hideNavBar = ["/login", "/register"].includes(location.pathname);

  return (
    <>
      {!hideNavBar && <NavBar />}
      <Routes>
        <Route path="/" element={<Navigate to="/register" />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        <Route path="/quiz" element={<SkinQuizPopup />} />
        <Route path="/home" element={<Home />} />
        <Route path="/product/:productKey" element={<ProductPage />} />
        <Route path="/inventory" element={<Inventory />} />
        <Route path="/wishlist" element={<Wishlist />} />
        <Route path="/looks" element={<Looks />} />
      </Routes>
    </>
  );
}

export default App;
