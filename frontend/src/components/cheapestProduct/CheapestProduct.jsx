import {
  Box,
  Card,
  CardContent,
  CardMedia,
  Button,
  Typography,
  CardActions,
  Tooltip
} from "@mui/material";
import { styled } from "@mui/system";

const StyledCard = styled(Card)(({ theme }) => ({
  maxWidth: 345,
  margin: "0 auto",
  borderRadius: "12px",
  boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
  transition: "transform 0.3s ease, box-shadow 0.3s ease",
  "&:hover": {
    transform: "scale(1.05)",
    boxShadow: "0 8px 30px rgba(0, 0, 0, 0.2)",
  },
}));

const CheapestProduct = ({ product }) => {
  if (!product) {
    return null;
  }

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        mt: 4,
        mb: 10,
      }}
    >
      <Typography
        variant="h5"
        component="div"
        gutterBottom
        sx={{ fontWeight: "bold", color: "#333" }}
      >
        PRODUCTO MÁS BARATO
      </Typography>
      <Tooltip title={product.name} arrow>
        <StyledCard>
          <CardMedia
            component="img"
            image={product.logo}
            alt={product.name}
            sx={{
              height: 180,
              objectFit: "contain",
              maxWidth: "80%",
              margin: " auto",
              padding: 2,
            }}
          />
          <CardContent sx={{ textAlign: "center" }}>
            <Typography
              gutterBottom
              variant="h6"
              component="div"
              sx={{
                maxHeight: "2.5em",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
                fontWeight: "bold",
                color: "#333",
              }}
            >
              {product.name}
            </Typography>
            <Typography variant="h5" color="text.secondary">
              Precio: <strong>${product.price}</strong>
            </Typography>
          </CardContent>

          <CardActions sx={{ justifyContent: "center", mb: 2 }}>
            <Button
              variant="contained"
              color="primary"
              href={product.link}
              target="_blank"
              sx={{
                borderRadius: "20px",
                textTransform: "none",
                padding: "0.5em 1.5em",
              }}
            >
              Ver Producto
            </Button>
          </CardActions>
        </StyledCard>
      </Tooltip>
    </Box>
  );
};

export default CheapestProduct;
