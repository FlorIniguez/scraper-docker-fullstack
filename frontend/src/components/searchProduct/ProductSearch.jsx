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
  Box,
} from "@mui/material";
import CheapestProduct from "../cheapestProduct/CheapestProduct";
import Products from "../productList/Products";

const ProductSearch = () => {
  const [query, setQuery] = useState("");
  const [products, setProducts] = useState([]);
  const [cheapestProduct, setCheapestProduct] = useState(null);
  const [searchMade, setSearchMade] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const searchProducts = () => {
    setLoading(true);
    setProducts([]); // Limpiar los productos al iniciar una nueva búsqueda
    setCheapestProduct(null);
    setError(null);
    setSearchMade(false); 

    axios
      .get(`/api/products/search?query=${query}`) 
      .then((response) => {
        setProducts(response.data.SortedProducts || []); 
        setCheapestProduct(response.data.CheapestProduct || null);
        setSearchMade(true);
      })
      .catch((error) => {
        setProducts([]); 
        setCheapestProduct(null);
        if (error.response) {
          if (error.response.status === 404) {
            setError(`No se encontraron productos para: '${query}'`);
          } else {
            setError(`Error en la respuesta del servidor: ${error.response.status}`);
          }
        } else if (error.request) {
          setError("No se pudo conectar al servidor.");
        } else {
          setError(`Error desconocido: ${error.message}`);
        }
        setSearchMade(true);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <>
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={8}>
            <TextField
              fullWidth
              label="Buscar producto"
              className="textfield-buscador"
              variant="outlined"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </Grid>
          <Grid item xs={4}>
            <Button
              variant="contained"
              color="primary"
              className = "search-button"
              fullWidth
              onClick={searchProducts}
              disabled={loading}
            >
              Buscar
            </Button>
          </Grid>
        </Grid>

        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
            <CircularProgress sx={{ color: "#ab003c" }} />
          </Box>
        ) : searchMade ? (
          products.length > 0 ? (
            <>
              <CheapestProduct product={cheapestProduct} />
              <Products products={products} />
            </>
          ) : (
            <Alert severity="error" sx={{ mt: 4 }}>
              {error ||
                "No se encontraron productos. Por favor, intente nuevamente."}
            </Alert>
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
