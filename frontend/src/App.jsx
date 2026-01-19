import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";
import AuthPage from "./pages/AuthPage";
import MainPage from "./pages/MainPage";
import EditPage from "./pages/EditPage";
import AddPage from "./pages/AddPage";
import CartPage from "./pages/CartPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AuthPage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/product/edit/:id" element={<EditPage />} />
        <Route path="/product/new" element={<AddPage />} />
        <Route path="/cart" element={<CartPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
