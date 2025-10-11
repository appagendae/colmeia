package com.agendae.colmeia.infra.adapter.out.db;

import com.agendae.colmeia.infra.entity.ExternalAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * ‘Interface’ do Spring Data JPA.
 * O Spring criará automaticamente a implementação desta ‘interface’ em tempo de execução,
 * fornecendo métodos CRUD básicos (save, findById, etc.).
 */
@Repository
public interface SpringDataExternalAccountRepository extends JpaRepository<ExternalAccountEntity, UUID> {
    // TODO adicionar métodos de busca customizados
    Optional<ExternalAccountEntity> findByProviderAndProviderUserId(String provider, String providerUserId);
}
