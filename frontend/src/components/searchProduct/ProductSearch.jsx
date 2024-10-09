import React, { useState } from "react";
import axios from "axios";
import {
  Typography,
  Container,
  Grid,
  TextField,
  Button,
  Alert,
  CircularProgress, 
  Box
} from "@mui/material";
import CheapestProduct from "../cheapestProduct/CheapestProduct";
import Products from "../productList/Products";

const ProductSearch = () => {
  const [query, setQuery] = useState("");
  const [products, setProducts] = useState([]);
  const [cheapestProduct, setCheapestProduct] = useState(null);
  const [searchMade, setSearchMade] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false); // Estado para la carga

  const searchProducts = () => {
    setLoading(true); // Iniciar carga
    axios
      .get(`http://localhost:8080/products/search?query=${query}`)
      .then((response) => {
        // Solo actualiza los estados si no hay error
        setProducts(response.data.productList || []);
        setCheapestProduct(response.data.CheapestProduct || null);
        setSearchMade(true);
        setError(null); // Limpiar cualquier error previo
      })
      .catch((error) => {
        console.error("Error al traer productos:", error);
        // Verifica si el error es 404 para mostrar un mensaje específico
        if (error.response && error.response.status === 404) {
          setError("No se encontraron productos para la búsqueda.");
          // Restablecer los productos si hay un error
          setProducts([]); // Asegúrate de que no haya productos
          setCheapestProduct(null); // Asegúrate de que no haya producto más barato
        } else {
          setError("Hubo un problema al buscar los productos.");
        }
        // searchMade se establece como true después de la búsqueda, sin importar si tuvo éxito
        // o falló, permitiendo que la aplicación siga funcionando.
        setSearchMade(true);
      })
      .finally(() => {
        setLoading(false); // Finalizar carga
      });
  };

  return (
    <>
      {/* Contenedor de búsqueda */}
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={8}>
            <TextField
              fullWidth
              label="Buscar producto"
              variant="outlined"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </Grid>
          <Grid item xs={4}>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={searchProducts}
            >
              Buscar
            </Button>
          </Grid>
        </Grid>

        {/* Mostrar indicador de carga si está en proceso */}
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <CircularProgress sx={{ color: '#ab003c' }} /> {/* Color rosa */}
          </Box>
        ) : searchMade ? (
          products.length > 0 ? (
            <>
              {/* Producto más barato */}
              <CheapestProduct product={cheapestProduct} />

              {/* Lista de productos */}
              <Products products={products} />
            </>
          ) : error ? (
            <Alert severity="error" sx={{ mt: 4 }}>
              {error}
            </Alert>
          ) : (
            <Typography variant="h6" sx={{ mt: 4 }} textAlign="center">
              No se encontraron productos. Por favor, intente nuevamente.
            </Typography>
          )
        ) : (
          <Typography variant="body1" sx={{ mt: 4 }} textAlign="left">
            Por favor, realice una búsqueda.
          </Typography>
        )}
      </Container>
    </>
  );
};

export default ProductSearch;
