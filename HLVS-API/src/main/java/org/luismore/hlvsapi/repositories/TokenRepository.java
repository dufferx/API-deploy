package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.Token;
import org.luismore.hlvsapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    List<Token> findByUserAndActive(User user, Boolean active);
}