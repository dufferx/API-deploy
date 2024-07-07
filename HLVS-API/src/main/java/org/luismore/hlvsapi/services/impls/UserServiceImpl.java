package org.luismore.hlvsapi.services.impls;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.luismore.hlvsapi.domain.dtos.AddFamilyMemberDTO;
import org.luismore.hlvsapi.domain.dtos.UserRegisterDTO;
import org.luismore.hlvsapi.domain.entities.House;
import org.luismore.hlvsapi.domain.entities.Role;
import org.luismore.hlvsapi.domain.entities.Token;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.HouseRepository;
import org.luismore.hlvsapi.repositories.RoleRepository;
import org.luismore.hlvsapi.repositories.TokenRepository;
import org.luismore.hlvsapi.repositories.UserRepository;
import org.luismore.hlvsapi.services.UserService;
import org.luismore.hlvsapi.utils.JWTTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final JWTTools jwtTools;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HouseRepository houseRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, JWTTools jwtTools, TokenRepository tokenRepository, RoleRepository roleRepository, HouseRepository houseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTools = jwtTools;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.houseRepository = houseRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    @Transactional(rollbackOn = Exception.class)
    public void create(UserRegisterDTO info){
        User user = new User();
        user.setName(info.getName());
        user.setPassword(passwordEncoder.encode(info.getPassword()));
        user.setEmail(info.getEmail());
        user.setActive(true);

        Role defaultRole = roleRepository.findById("VISI")
                .orElseThrow(() -> new IllegalArgumentException("Visitant role Can(not) be found"));

        user.setRoles(Collections.singletonList(defaultRole));

        userRepository.save(user);
    }


    @Override
    public User findByNameOrEmail(String name, String email){
        return userRepository.findByNameOrEmail(name, email).orElse(null);
    }

    @Override
    public User findByIdentifier(String identifier){
        return userRepository.findByNameOrEmail(identifier, identifier).orElse(null);
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> findByIdentifiers(List<String> identifiers) {
        return userRepository.findByEmailIn(identifiers);
    }

    @Override
    public boolean checkPassword(User user, String password){
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changeRoles(User user, List<String> roles){
        List<Role> newRoles = getRoles(roles);
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    private Role getRoleById(String roleId){
        return roleRepository.findById(roleId).orElse(null);
    }

    private List<Role> getRoles(List<String> rolesIds){
        return roleRepository.findAllById(rolesIds);
    }

    @Override
    public boolean isActive(User user) {
        return user.getActive();
    }

    @Override
    public void toggleEnable(String username) {
        User user = userRepository.findByNameOrEmail(username, username).orElse(null);
        assert user != null;
        user.setActive(!user.getActive());
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Token registerToken(User user) throws Exception {
        cleanTokens(user);

        String tokenString = jwtTools.generateToken(user);
        Token token = new Token(tokenString, user);

        tokenRepository.save(token);

        return token;
    }


    @Override
    public Boolean isTokenValid(User user, String token) {
        try {
            cleanTokens(user);
            List<Token> tokens = tokenRepository.findByUserAndActive(user, true);

            tokens.stream()
                    .filter(tk -> tk.getContent().equals(token))
                    .findAny()
                    .orElseThrow(() -> new Exception());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void cleanTokens(User user) throws Exception {
        List<Token> tokens = tokenRepository.findByUserAndActive(user, true);

        tokens.forEach(token -> {
            if(!jwtTools.verifyToken(token.getContent())) {
                token.setActive(false);
                tokenRepository.save(token);
            }
        });

    }

    @Override
    public User findUserAuthenticated() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByNameOrEmail(username, username).orElse(null);
    }

    @Override
    public void DeleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updatePassword(String identifier, String newPassword){
        User user = findByIdentifier(identifier);
        if (user == null) {
            throw new EntityNotFoundException("User Can(not) be found with identifier: " + identifier);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            User user = this.findByIdentifier(identifier);
            if (user == null) {
                throw new UsernameNotFoundException("User: " + identifier + ", Can(not) be found!");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        };
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isAvailable(User user) {
        return false;
    }

    @Override
    public List<String> getUserRolesByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User Can(not) be found with email: " + email));
        return user.getRoles().stream().map(Role::getRole).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addFamilyMember(UUID houseId, AddFamilyMemberDTO addFamilyMemberDTO) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House Can(not) be found with id: " + houseId));
        User user = new User();
        user.setName(addFamilyMemberDTO.getName());
        user.setEmail(addFamilyMemberDTO.getEmail());
        user.setPassword(passwordEncoder.encode(addFamilyMemberDTO.getPassword()));
        user.setHouse(house);

        Role defaultRole = roleRepository.findById("RESI")
                .orElseThrow(() -> new IllegalArgumentException("Resident role Can(not) be found"));
        user.setRoles(Collections.singletonList(defaultRole));

        userRepository.save(user);
    }

}
