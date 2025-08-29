
const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => Array.from(document.querySelectorAll(sel));

const marcaSelect = $("#marcaSelect");
const tipoSelect = $("#tipoSelect");
const searchInput = $("#searchInput");
const grid = $("#grid");
const cartList = $("#cartList");
const cartCount = $("#cartCount");
const cartTotalQty = $("#cartTotalQty");
const cartTotalPrice = $("#cartTotalPrice");
const exportBtn = $("#exportBtn");
const enviarBtn = $("#enviarBtn");
const limpiarBtn = $("#limpiarBtn");

let carrito = [];

// ----------- Inicialización -----------
function init() {
  // Poblar selects
  marcaSelect.innerHTML = `<option value="">Todas las marcas</option>` + MARCAS.map(m => `<option>${m}</option>`).join("");
  tipoSelect.innerHTML = `<option value="">Todos los productos</option>` + TIPOS.map(t => `<option>${t}</option>`).join("");

  // Listeners
  [marcaSelect, tipoSelect].forEach(el => el.addEventListener('change', render));
  searchInput.addEventListener('input', debounce(render, 120));
  exportBtn.addEventListener('click', exportarPedido);
  enviarBtn.addEventListener('click', enviarPedido);
  limpiarBtn.addEventListener('click', limpiarFiltros);

  render();
}

function limpiarFiltros(){
  marcaSelect.value = "";
  tipoSelect.value = "";
  searchInput.value = "";
  render();
}

// ----------- Render catálogo -----------
function render() {
  const marca = marcaSelect.value;
  const tipo = tipoSelect.value;
  const q = searchInput.value.toLowerCase().trim();

  const items = CATALOGO.filter(p => {
    const okMarca = !marca || p.marca === marca;
    const okTipo = !tipo || p.tipo === tipo;
    const texto = (p.nombre + " " + p.ref + " " + p.descripcion + " " + p.marca + " " + p.tipo).toLowerCase();
    const okQ = !q || texto.includes(q);
    return okMarca && okTipo && okQ;
  });

  grid.innerHTML = items.map(p => cardHTML(p)).join("") || `<p style="opacity:.6">No se encontraron productos</p>`;
  // Bind botones cantidad
  items.forEach(p => bindCardEvents(p.id));
}

function cardHTML(p){
  return `
  <article class="card" id="card-${p.id}">
    <img src="${p.img}" alt="${p.nombre}">
    <div class="p">
      <h3 style="margin:0">${p.nombre}</h3>
      <div class="meta">Ref: ${p.ref} • ${p.marca}</div>
      <div class="meta">Tipo: ${p.tipo}</div>
      <div class="meta">$${number(p.precio)}</div>
      <div class="qty">
        <button data-act="dec">–</button>
        <input data-role="qty" type="number" min="1" value="1" style="width:56px;text-align:center;border:1px solid #cbd5e1;border-radius:8px;padding:6px 8px"/>
        <button data-act="inc">+</button>
        <button class="btn add" data-act="add">Agregar</button>
      </div>
    </div>
  </article>`;
}

function bindCardEvents(id){
  const root = $("#card-"+id);
  const qty = root.querySelector('[data-role="qty"]');
  root.querySelector('[data-act="inc"]').onclick = () => qty.value = Number(qty.value) + 1;
  root.querySelector('[data-act="dec"]').onclick = () => qty.value = Math.max(1, Number(qty.value) - 1);
  root.querySelector('[data-act="add"]').onclick = () => agregar(id, Number(qty.value));
}

// ----------- Carrito -----------
function agregar(id, cantidad=1){
  const p = CATALOGO.find(x => x.id === id);
  if(!p) return;
  const idx = carrito.findIndex(i => i.id === id);
  if(idx >= 0){ carrito[idx].cantidad += cantidad; }
  else { carrito.push({id:p.id, nombre:p.nombre, ref:p.ref, precio:p.precio, cantidad}); }
  pintarCarrito();
}

function pintarCarrito(){
  cartList.innerHTML = carrito.map(item => `
    <div class="cart-item">
      <div>
        <div><strong>${item.nombre}</strong> <span class="badge">Ref ${item.ref}</span></div>
        <div class="meta">$${number(item.precio)} × 
          <button onclick="cambiar(${item.id},-1)" style="margin:0 4px">–</button>
          ${item.cantidad}
          <button onclick="cambiar(${item.id},1)" style="margin-left:4px">+</button>
          <button onclick="quitar(${item.id})" style="margin-left:8px">🗑️</button>
        </div>
      </div>
      <div><strong>$${number(item.precio*item.cantidad)}</strong></div>
    </div>
  `).join("");

  const totalQty = carrito.reduce((a,b)=>a+b.cantidad,0);
  const totalPrice = carrito.reduce((a,b)=>a+b.cantidad*b.precio,0);
  cartCount.textContent = totalQty;
  cartTotalQty.textContent = totalQty;
  cartTotalPrice.textContent = number(totalPrice);
}

function cambiar(id, delta){
  const i = carrito.findIndex(x=>x.id===id);
  if(i<0) return;
  carrito[i].cantidad = Math.max(1, carrito[i].cantidad + delta);
  pintarCarrito();
}
function quitar(id){
  carrito = carrito.filter(x=>x.id!==id);
  pintarCarrito();
}

// ----------- Exportar / Enviar -----------
function exportarPedido(){
  const payload = {
    cliente: "CLIENTE_DEMO",
    fecha: new Date().toISOString(),
    items: carrito.map(i => ({id:i.id, ref:i.ref, nombre:i.nombre, cantidad:i.cantidad, precio:i.precio})),
    totales:{
      cantidad: carrito.reduce((a,b)=>a+b.cantidad,0),
      valor: carrito.reduce((a,b)=>a+b.cantidad*b.precio,0)
    }
  };
  const blob = new Blob([JSON.stringify(payload, null, 2)], {type:'application/json'});
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'pedido-disramfor.json';
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function enviarPedido(){
  const subject = encodeURIComponent("Pedido Disramfor – Catálogo");
  const body = encodeURIComponent(carrito.map(i => `• ${i.cantidad} x ${i.nombre} (Ref ${i.ref}) = $${number(i.cantidad*i.precio)}`).join("%0D%0A"));
  window.location.href = `mailto:ventas@disramfor.com?subject=${subject}&body=${body}`;
}

// Utils
function number(n){ return (n||0).toLocaleString('es-CO'); }
function debounce(fn, ms=100){ let t; return (...a)=>{ clearTimeout(t); t=setTimeout(()=>fn.apply(this,a),ms); }; }

// Simulación de fetch a API para que luego reemplaces:
// async function cargarCatalogo(){ const r = await fetch('/api/productos?...'); CATALOGO = await r.json(); render(); }

init();
