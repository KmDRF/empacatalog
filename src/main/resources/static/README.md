
# Disramfor – Mockup Catálogo + Toma de Pedidos (Frontend)

Prototipo estático listo para conectar con tu backend en **IntelliJ (Spring Boot)**.  
Incluye filtros por **Marca**, **Tipo de producto** y **búsqueda por texto**, tarjetas de catálogo, carrito lateral, exportación a JSON y envío por correo.

## Cómo usar
1. Descomprime el ZIP.
2. Abre `index.html` en **Visual Studio Code** con Live Server, o cualquier navegador.
3. Prueba filtros y agrega productos al carrito.
4. Usa **"Descargar pedido (JSON)"** o **"Enviar por correo"**.

## Conectar con tu API
- Reemplaza el contenido de `CATALOGO` en `src/data.js` por un `fetch` a tu API.
- Endpoints sugeridos:
  - `GET /api/marcas`
  - `GET /api/productos?marca=&tipo=&q=`
  - `POST /api/pedidos` (envía el JSON generado en exportación).

> Los estilos se encuentran en `styles.css` y las funciones principales en `src/app.js`.
