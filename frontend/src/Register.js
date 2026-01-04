import React, { useState } from "react";
import { useNavigate } from "react-router-dom"
function Register(){

const [email, setEmail] =  useState("");
const [username, setUsername] =  useState("");
const [password, setPassword] =  useState("");
const [message, setMessage] =  useState(null);

const handleSubmit = async (e) => { //async means we can use await
    e.preventDefault();


    if (!email || !username || !password){
        setMessage("Please fill in all fields!")
    return;
    }

    const url = "http://localhost:8080/api/users/register";

    try{
        const response = await fetch(url, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({email, username, password})
        });
        if (response.ok){
            setMessage("Registered successfully! 🎀 You can now log in.");
            setEmail("");
            setUsername("");
            setPassword("");
            
        } else {
            const data = await response.json();
            setMessage(data.message || "Registration failed. 😢");
        
        }
    } catch(error){
        setMessage("Network error. Please try again. 💔");
    }
};

const navigate = useNavigate();
const handleLoginClick = () => {
    navigate("/login");
}

return (
    <div
    className = "min-h-screen flex flex-col items-center justify-start"
    style = {{
        backgroundColor: "#fffafc", //whitish pinl
    }}
    >
        {/* Title */}
        <h1
        className="mt-12 text-5xl font-extrabold"
        style = {{
            color: "#ff8fab", //baby pink
            fontFamily: "'Dancing Script', cursive"
        }}
        >
            Resplenda
        </h1>

<div  className="max-w-sm mx-auto p-6 bg-white border border-pink-200 rounded-2xl shadow-lg mt-10">

{/*Form container */}
<form onSubmit = {handleSubmit} >
    <h2 className = "text-2xl font-bold mb-6 text-center text-pink-500">Register✨</h2>

    {message && <div className = "mb-4 text-center text-pink-600 font-medium">{message}</div>}
  
    <label className = "block mb-2 font-semibold text-pink-600">Username</label>
    <input
    type = "text"
    className = "w-full p-2 mb-4 border border-pink-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-pink-300"
    value = {username}
    onChange = {(e) => setUsername(e.target.value)}
    required
    />

     <label className = "block mb-2 font-semibold text-pink-600">Email</label>
    <input
    type = "email"
    className = "w-full p-2 mb-4 border border-pink-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-pink-300"
    value = {email}
    onChange = {(e) => setEmail(e.target.value)}
    required
    />

     <label className = "block mb-2 font-semibold text-pink-600">Password</label>
    <input
    type = "password"
    className = "w-full p-2 mb-4 border border-pink-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-pink-300"
    value = {password}
    onChange = {(e) => setPassword(e.target.value)}
    required
    />

    <button 
    type = "submit"
    className = "w-full bg-pink-400 text-white py-2 rounded-lg shadow hover:bg-pink-500 transition duration-200"
    >
    Register
    </button>



</form>

    <button 
    type = "button"
    onClick={handleLoginClick}
    className = "w-full mt-4 bg-pink-700 text-white py-2 rounded-lg shadow hover:bg-pink-900 transition duration-200"
    >
    Login
    </button>
    </div>


</div>
);
}


export default Register;
