import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import ProductSearch from "./components/searchProduct/ProductSearch";
import NavBar from "./components/NavBar";
import {ThemeProvider, CssBaseline, Box } from '@mui/material';
import theme from "./components/theme";

function App() {

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ minHeight: "100vh", backgroundColor: "#f0f4f8" }}>
        {" "}
        {/* Color de fondo suave */}
        <NavBar />
        <ProductSearch />
      </Box>
    </ThemeProvider>
  );
}

export default App;
