package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.AddFamilyMemberDTO;
import org.luismore.hlvsapi.domain.dtos.UserRegisterDTO;
import org.luismore.hlvsapi.domain.entities.Token;
import org.luismore.hlvsapi.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(UserRegisterDTO info);
    User findByNameOrEmail(String username, String email);
    User findByIdentifier(String identifier);
    User findByEmail(String email);
    List<User> findByIdentifiers(List<String> identifiers);
    boolean checkPassword(User user, String password);
    boolean isActive(User username);
    void toggleEnable(String username);
    void changeRoles(User user, List<String> roles);
    Token registerToken(User user) throws Exception;
    Boolean isTokenValid(User user, String token);
    void cleanTokens(User user) throws Exception;
    User findUserAuthenticated();
    void DeleteUser(User user);
    void updatePassword(String identifier, String newPassword);
    List<User> getAllUsers();
    boolean isAvailable(User user);
    List<String> getUserRolesByEmail(String email);
    void addFamilyMember(UUID houseId, AddFamilyMemberDTO addFamilyMemberDTO);
}
