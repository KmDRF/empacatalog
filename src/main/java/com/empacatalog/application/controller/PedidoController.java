package com.empacatalog.application.controller;

import com.empacatalog.application.dto.pedido.PedidoCreationRequest;
import com.empacatalog.application.dto.pedido.PedidoResponse;
import com.empacatalog.application.dto.product.ProductResponse;
import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoItem;
import com.empacatalog.domain.model.PedidoNotFoundException;
import com.empacatalog.domain.model.User;
import com.empacatalog.domain.repository.UserRepository;
import com.empacatalog.application.usecase.CreatePedidoUseCase;
import com.empacatalog.application.usecase.GetPedidoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de pedidos.
 * Proporciona endpoints para crear y consultar pedidos, devolviendo DTOs de respuesta.
 */
@RestController
@RequestMapping("/api/orders")
public class PedidoController {

    private final CreatePedidoUseCase createPedidoUseCase;
    private final GetPedidoUseCase getPedidoUseCase;
    private final UserRepository userRepository;

    public PedidoController(CreatePedidoUseCase createPedidoUseCase,
                            GetPedidoUseCase getPedidoUseCase,
                            UserRepository userRepository) {
        this.createPedidoUseCase = createPedidoUseCase;
        this.getPedidoUseCase = getPedidoUseCase;
        this.userRepository = userRepository;
    }

    /**
     * Crea un nuevo pedido para el usuario autenticado.
     * Solo usuarios con el rol 'CUSTOMER' pueden crear pedidos.
     *
     * @param request La solicitud de creación del pedido
     * @return El pedido creado con un estado HTTP 201 Created, en formato DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PedidoResponse> createPedido(@RequestBody PedidoCreationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pedido nuevoPedido = createPedidoUseCase.createPedido(request, user);
        return new ResponseEntity<>(mapToPedidoResponse(nuevoPedido), HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los pedidos del usuario autenticado.
     *
     * @return Una lista de pedidos del usuario en formato DTO
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<PedidoResponse>> getPedidosByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Pedido> pedidos = getPedidoUseCase.findByUser(user);
        List<PedidoResponse> pedidoResponses = pedidos.stream()
                .map(this::mapToPedidoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidoResponses);
    }

    /**
     * Obtiene un pedido específico por su ID.
     * Se asegura de que el pedido pertenezca al usuario autenticado.
     *
     * @param id El ID del pedido
     * @return El pedido encontrado en formato DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PedidoResponse> getPedidoById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pedido pedido = getPedidoUseCase.findById(id);

        if (!pedido.getUser().getId().equals(user.getId()) && !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new PedidoNotFoundException(id);
        }

        return ResponseEntity.ok(mapToPedidoResponse(pedido));
    }

    // --- Métodos de mapeo de DTOs ---

    private PedidoResponse mapToPedidoResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setUserId(pedido.getUser().getId());
        response.setUserEmail(pedido.getUser().getEmail());
        response.setFechaCreacion(pedido.getFechaCreacion());
        response.setEstado(pedido.getEstado());
        response.setTotal(pedido.getTotal());

        List<PedidoItemResponse> items = pedido.getItems().stream()
                .map(this::mapToPedidoItemResponse)
                .collect(Collectors.toList());
        response.setItems(items);

        return response;
    }

    private PedidoItemResponse mapToPedidoItemResponse(PedidoItem item) {
        PedidoItemResponse response = new PedidoItemResponse();
        response.setId(item.getId());
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());

        ProductResponse productDto = new ProductResponse();
        productDto.setId(item.getProduct().getId());
        productDto.setName(item.getProduct().getName());
        productDto.setDescription(item.getProduct().getDescription());
        productDto.setPrice(item.getProduct().getPrice());
        productDto.setPartNumber(item.getProduct().getPartNumber());
        productDto.setCategory(item.getProduct().getCategory());
        response.setProduct(productDto);

        return response;
    }
}
