package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.enums.StatusDePedidoEnum;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, UUID> {
    
    // Buscar pedidos de um CLIENTE (Histórico de compras)
    // O campo 'idCliente' existe tanto no PedidoDeCliente quanto no PedidoDeVendedor
    List<Pedido> findByIdClienteOrderByDataCriacaoDesc(UUID idCliente);

    // Buscar pedidos de um VENDEDOR (Painel de Vendas da Loja)
    // O campo 'idVendedor' existe no PedidoDeVendedor (e no PedidoDeCliente também, conforme sua modelagem)
    List<Pedido> findByIdVendedorOrderByDataCriacaoDesc(UUID idVendedor);

    // Buscar por Status (Ex: Admin quer ver todos "EM_DISPUTA")
    List<Pedido> findByStatusAtual(StatusDePedidoEnum status);
}