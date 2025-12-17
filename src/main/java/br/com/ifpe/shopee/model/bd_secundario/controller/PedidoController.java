package br.com.ifpe.shopee.model.bd_secundario.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.bd_secundario.dto.CheckoutRequest;
import br.com.ifpe.shopee.model.bd_secundario.entity.PedidoDeCliente;
import br.com.ifpe.shopee.model.bd_secundario.repository.PedidoRepository;
import br.com.ifpe.shopee.model.bd_secundario.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository; // Acesso direto apenas para leituras simples

    // =========================================================================
    //  CHECKOUT (A Mágica)
    // =========================================================================
    @PostMapping("/checkout")
    public ResponseEntity<PedidoDeCliente> realizarCheckout(@RequestBody @Valid CheckoutRequest request) {
        
        PedidoDeCliente novoPedido = pedidoService.checkout(
            request.getIdCarrinho(), 
            request.getFormaPagamento()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    // =========================================================================
    //  LEITURA (Histórico)
    // =========================================================================
    
    // Listar pedidos de um cliente (Para tela "Meus Pedidos")
    @GetMapping("/meus-pedidos/{idCliente}")
    public ResponseEntity<List<Pedido>> listarPedidosDoCliente(@PathVariable UUID idCliente) {
        List<Pedido> pedidos = pedidoRepository.findByIdClienteOrderByDataCriacaoDesc(idCliente);
        return ResponseEntity.ok(pedidos);
    }

    // Listar vendas de uma loja (Para tela "Painel do Vendedor")
    @GetMapping("/vendas-loja/{idVendedor}")
    public ResponseEntity<List<Pedido>> listarVendasDaLoja(@PathVariable UUID idVendedor) {
        List<Pedido> pedidos = pedidoRepository.findByIdVendedorOrderByDataCriacaoDesc(idVendedor);
        return ResponseEntity.ok(pedidos);
    }
    
    // Detalhes de um pedido específico
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable UUID id) {
        return pedidoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}