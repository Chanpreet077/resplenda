// src/Login.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import SkinQuizPopup from "./SkinQuizPopup";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState(null);

  const [showQuiz, setShowQuiz] = useState(false); 
  const [userId, setUserId] = useState(null);      // new state
  const [token, setToken] = useState(null);  

  useEffect(() => {
  if (userId && token && showQuiz) {
    setShowQuiz(true); // This reaffirms the condition
  }
}, [userId, token, showQuiz]);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username || !password) {
      setMessage("Please fill all fields");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        setMessage("Invalid username or password.");
        return;
      }

      const data = await response.json();
      console.log("Login response data:", data);

      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);

       // set states
      setUserId(data.userId);
      setToken(data.token);


      // Use backend showQuiz variable
    if (data.showQuiz) {
        setShowQuiz(true);
    } else {
  navigate("/home"); }
    } catch (err) {
      console.error(err);
      setMessage("An error occurred. Please try again.");
    }
  };


const handleQuizComplete = () => {
setShowQuiz(false);
navigate("/home");
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-pink-50">
      <div className="bg-white p-6 rounded-2xl shadow-lg w-80">
        <h2 className="text-2xl font-bold text-pink-500 text-center mb-4">Login</h2>
        {message && <div className="text-red-500 text-sm text-center mb-2">{message}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-pink-300"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-pink-300"
          />
          <button
            type="submit"
            className="w-full bg-pink-500 text-white py-2 rounded-lg hover:bg-pink-700 transition"
          >
            Login
          </button>
        </form>

        {/* Render SkinQuizPopup only if showQuiz is true AND userId/token exist */}
{userId && token && showQuiz && (
  <SkinQuizPopup
    userId={userId}
    token={token}
    onComplete={handleQuizComplete}
  />
)}
        <p className="text-sm text-gray-599 text-center mt-4">
          Don't have an account?{" "}
          <span
            className="text-pink-500 cursor-pointer hover:underline"
            onClick={() => navigate("/register")}
          >
            Register
          </span>
        </p>
      </div>
    </div>
  );
}

export default Login;
