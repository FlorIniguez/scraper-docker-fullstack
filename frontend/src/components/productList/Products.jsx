
import { Box, Grid, Card, CardContent, CardMedia, Button, Typography, Tooltip } from '@mui/material';
import { styled } from '@mui/system';

const StyledCard = styled(Card)(({ theme }) => ({
  borderRadius: '12px',
  boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    transform: 'scale(1.05)',
    boxShadow: '0 8px 30px rgba(0, 0, 0, 0.2)',
  },
  width: '100%', 
  maxWidth: '500px', 
}));

const Products = ({ products }) => {
  return (
    <Box sx={{ flexGrow: 1, mt: 4, mx: 'auto', maxWidth: '3000px', padding: 2 }}>
      <Typography variant="h5" component="div" align="center" gutterBottom sx={{ pb: '30px' }}>
        Productos Disponibles
      </Typography>
      <Grid container spacing={4} justifyContent="space-around">
        {products.map((product, index) => (
          <Grid item key={index} xs={12} sm={6} md={4} lg={3} className="found-products">
            <Tooltip title={product.name} arrow>
              <StyledCard sx={{ width: '100%' }}>
                <CardMedia
                  component="img"
                  image={product.logo}
                  alt={product.name}
                  sx={{
                    height: 140,
                    objectFit: 'contain',
                    padding: 2,
                    margin: '0 auto',
                  }}
                />
                <CardContent>
                  <Typography
                    gutterBottom
                    variant="h6"
                    component="div"
                    align="center"
                    noWrap
                    sx={{
                      maxHeight: '2.5em',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    {product.name}
                  </Typography>
                  <Typography variant="h5" color="text.secondary" align="center">
                    Precio: <strong>${product.price}</strong>
                  </Typography>
                </CardContent>

                <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                  <Button variant="contained" color="primary" href={product.link} target="_blank">
                    Ver Producto
                  </Button>
                </Box>
              </StyledCard>
            </Tooltip>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default Products;
