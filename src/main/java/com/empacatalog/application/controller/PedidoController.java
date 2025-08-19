package com.empacatalog.application.controller;

import com.empacatalog.application.dto.pedido.PedidoAuditDTO;
import com.empacatalog.application.dto.pedido.PedidoCreationRequest;
import com.empacatalog.application.dto.pedido.PedidoItemResponse;
import com.empacatalog.application.dto.pedido.PedidoResponse;
import com.empacatalog.application.dto.pedido.PedidoStatusUpdateRequest;
import com.empacatalog.application.dto.product.ProductResponse;
import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoItem;
import com.empacatalog.domain.model.PedidoNotFoundException;
import com.empacatalog.domain.model.User;
import com.empacatalog.domain.repository.UserRepository;
import com.empacatalog.application.usecase.CreatePedidoUseCase;
import com.empacatalog.application.usecase.GetPedidoAuditHistoryUseCase;
import com.empacatalog.application.usecase.GetPedidoUseCase;
import com.empacatalog.application.usecase.UpdatePedidoStatusUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.DefaultRevisionEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de pedidos.
 * Proporciona endpoints para crear, consultar, actualizar el estado y obtener el historial de auditoría.
 */
@RestController
@RequestMapping("/api/orders")
public class PedidoController {

    private final CreatePedidoUseCase createPedidoUseCase;
    private final GetPedidoUseCase getPedidoUseCase;
    private final UpdatePedidoStatusUseCase updatePedidoStatusUseCase;
    private final GetPedidoAuditHistoryUseCase getPedidoAuditHistoryUseCase;
    private final UserRepository userRepository;

    public PedidoController(CreatePedidoUseCase createPedidoUseCase,
                            GetPedidoUseCase getPedidoUseCase,
                            UpdatePedidoStatusUseCase updatePedidoStatusUseCase,
                            GetPedidoAuditHistoryUseCase getPedidoAuditHistoryUseCase,
                            UserRepository userRepository) {
        this.createPedidoUseCase = createPedidoUseCase;
        this.getPedidoUseCase = getPedidoUseCase;
        this.updatePedidoStatusUseCase = updatePedidoStatusUseCase;
        this.getPedidoAuditHistoryUseCase = getPedidoAuditHistoryUseCase;
        this.userRepository = userRepository;
    }

    // --- Endpoints de Consulta ---

    /**
     * Obtiene todos los pedidos del usuario autenticado.
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
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PedidoResponse> getPedidoById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pedido pedido = getPedidoUseCase.findById(id);

        // Seguridad adicional: verifica si el pedido pertenece al usuario o si el usuario es un ADMIN
        if (!pedido.getUser().getId().equals(user.getId()) && !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new PedidoNotFoundException(id);
        }

        return ResponseEntity.ok(mapToPedidoResponse(pedido));
    }

    /**
     * Obtiene el historial de auditoría de un pedido por su ID.
     * Solo los roles 'ADMIN' y 'ADVISOR' pueden ver el historial.
     *
     * @param id El ID del pedido.
     * @return Una lista de DTOs con las revisiones del pedido.
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVISOR')")
    public ResponseEntity<List<PedidoAuditDTO>> getPedidoHistory(@PathVariable Long id) {
        List<Object[]> history = getPedidoAuditHistoryUseCase.getPedidoHistory(id);
        List<PedidoAuditDTO> auditDTOs = history.stream()
                .map(this::mapToPedidoAuditDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(auditDTOs);
    }

    // --- Endpoint de Creación ---

    /**
     * Crea un nuevo pedido para el usuario autenticado.
     * Solo usuarios con el rol 'CUSTOMER' pueden crear pedidos.
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

    // --- Endpoint de Actualización de Estado ---

    /**
     * Actualiza el estado de un pedido.
     * Solo usuarios con el rol 'ADVISOR' o 'ADMIN' pueden realizar esta acción.
     *
     * @param id El ID del pedido a actualizar.
     * @param request El DTO que contiene el nuevo estado.
     * @return El pedido actualizado en formato DTO.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADVISOR', 'ADMIN')")
    public ResponseEntity<PedidoResponse> updatePedidoStatus(@PathVariable Long id, @RequestBody PedidoStatusUpdateRequest request) {
        Pedido pedidoActualizado = updatePedidoStatusUseCase.updateStatus(id, request.getNuevoEstado());
        return ResponseEntity.ok(mapToPedidoResponse(pedidoActualizado));
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

    private PedidoAuditDTO mapToPedidoAuditDTO(Object[] auditData) {
        Pedido pedido = (Pedido) auditData[0];
        DefaultRevisionEntity revision = (DefaultRevisionEntity) auditData[1];
        RevisionType type = (RevisionType) auditData[2];

        PedidoAuditDTO auditDTO = new PedidoAuditDTO();
        // Corrección: Usamos (long) para convertir el int a long, en lugar de un método.
        auditDTO.setRevisionId((long) revision.getId());
        auditDTO.setRevisionDate(LocalDateTime.ofInstant(revision.getRevisionDate().toInstant(), ZoneId.systemDefault()));
        auditDTO.setRevisionType(type);
        auditDTO.setPedido(mapToPedidoResponse(pedido));

        return auditDTO;
    }
}
