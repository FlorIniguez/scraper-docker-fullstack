import React from "react";
import { AppBar, Toolbar, Typography, Box } from "@mui/material";
import Logo from '../assets/imgs/lowest-price_5497175.png'; 


const NavBar = () => {
  return (

      <AppBar position="static">
        <Toolbar>
          {/* Logo de la aplicación */}
          <Box component="img" src={Logo} alt="Logo" sx={{ height: 40, mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Comparador de Precios
          </Typography>

    
        </Toolbar>
      </AppBar>
 
  );
};

export default NavBar;
