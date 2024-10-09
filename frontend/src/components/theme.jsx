// theme.js
import { createTheme } from "@mui/material/styles";


const theme = createTheme({
  palette: {
    primary: {
      main: '#ff4081', // Rosa principal
    },
    secondary: {
      main: '#ffffff', // Blanco para contraste
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
  },
});

export default theme;
