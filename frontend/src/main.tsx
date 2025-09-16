import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Rotas from './rotas/Rotas.tsx'
import { AuthProvider } from "./context/AuthContext";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <AuthProvider>
          <Rotas />
      </AuthProvider>
  </StrictMode>,
)
