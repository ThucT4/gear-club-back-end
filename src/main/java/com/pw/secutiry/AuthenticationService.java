package com.pw.secutiry;
import com.pw.model.Customer;
import com.pw.model.Role;
import com.pw.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Create new list of shopping cart have a shopping cart inside
        ArrayList<HashMap<Integer, Integer>> listOfShoppingCart = new ArrayList<>();
        HashMap<Integer, Integer> firstShoppingCart = new HashMap<>();
        listOfShoppingCart.add(firstShoppingCart);

        // Create new user from request information
        Customer customer = Customer.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .shippingAddress(request.getShippingAddress())
                .role(Role.USER)
                .shoppingCart(listOfShoppingCart)
                .build();
        customerRepository.save(customer);

        // Create new token from user's detail
        String jwtToken = jwtService.generateToken(customer);

        // Send back token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // User authenticationManager to authenticate user with password and username
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If the program go to this point meaning the user is inside the database, and authenticated
        // Get user from database to generate token then send back
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Create new token from user's detail
        String jwtToken = jwtService.generateToken(customer);

        // Send back token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
