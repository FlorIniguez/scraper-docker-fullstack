# FullStack (JAVA/REACT) de Scraping de Productos

Este proyecto consiste en un scraper en Java para buscar y comparar productos ***electro y tecnologia*** en diferentes sitios web de e-commerce. Hasta ahora, se han implementado scrapers para tres sitios web: Rodo, Mercado Libre y Garbarino.

## Objetivo del Proyecto

El objetivo del proyecto es implementar una funcionalidad de comparación de precios para los productos buscados por el cliente. Muestra el producto más barato y la lista de todos los productos.

El scraper extrae datos relevantes, como el nombre del producto, el precio y el enlace de compra, permitiendo al usuario tomar decisiones informadas basadas en el precio.

**Todavía hay cosas que me gustaría agregar/mejorar. Cualquier contribución o consejo es bienvenido.**


## Tecnologías Utilizadas

### Backend
- **Java**: Lenguaje de programación utilizado para desarrollar el backend.
- **Spring Boot**: Framework para crear aplicaciones Java basadas en microservicios.
- **Maven**: Herramienta de gestión de proyectos y automatización de construcción.
- **Docker**: Plataforma utilizada para contenerizar la aplicación.

### Frontend
- **React**: Biblioteca de JavaScript para construir interfaces de usuario.
- **Material-UI (MUI)**: Biblioteca de componentes de React que implementa el diseño de Material Design.

### Otras
- **JSoup**: Biblioteca para realizar scraping de contenido HTML.



## Instrucciones para Ejecutar la Aplicación

Para correr la aplicación de scraping, sigue estos pasos:


1. **Clona el repositorio:**
 
   ```bash
   git clone https://github.com/FlorIniguez/scraper-docker-fullstack
   
   cd scraper-docker-fullstack

2. **Ejecutar Docker Compose:**
   - Asegúrate de que Docker y Docker Compose estén instalados y en funcionamiento.
   - Ejecuta el siguiente comando para levantar los servicios:

      ```bash
      docker-compose up
      
4. **Acceder a la Aplicación:**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080

