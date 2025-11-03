package com.NoCountry.credit_onboarding_platform.controller;

import com.NoCountry.credit_onboarding_platform.dto.AuthRequest;
import com.NoCountry.credit_onboarding_platform.dto.AuthResponse;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.model.Usuario.EstadoUsuario;
import com.NoCountry.credit_onboarding_platform.security.JwtUtil;
import com.NoCountry.credit_onboarding_platform.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // ========== CRUD Básico ==========
    
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuario.setId(id);
        Usuario actualizado = usuarioService.save(usuario);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ========== Endpoints de Negocio ==========
    
    @PostMapping("/registro/cliente")
    public ResponseEntity<?> registrarCliente(@RequestBody Usuario cliente) {
        try {
            Usuario nuevoCliente = usuarioService.registrarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/registro/admin")
    public ResponseEntity<?> registrarAdmin(@RequestBody Usuario admin) {
        try {
            Usuario nuevoAdmin = usuarioService.registrarAdministrador(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
     @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
                )
            );
            
            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            
            // Generar token JWT
            Map<String, Object> extraClaims = new HashMap<>();
            
            // Obtener usuario completo
            Usuario usuario = usuarioService.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            extraClaims.put("rol", usuario.getRol().name());
            extraClaims.put("userId", usuario.getId());
            
            String token = jwtUtil.generateToken(userDetails, extraClaims);
            
            // Crear respuesta
            AuthResponse response = AuthResponse.fromUsuario(
                usuario,
                token,
                jwtUtil.getExpirationTime()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al procesar el login: " + e.getMessage()));
        }
    }
    
    // Agregar método para buscar por email
    public Optional<Usuario> findByEmail(String email) {
        return usuarioService.findByEmail(email);
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            EstadoUsuario nuevoEstado = EstadoUsuario.valueOf(body.get("estado"));
            Usuario actualizado = usuarioService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody Usuario datosActualizados) {
        try {
            Usuario actualizado = usuarioService.actualizarPerfil(id, datosActualizados);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/clientes")
    public ResponseEntity<List<Usuario>> getClientes() {
        List<Usuario> clientes = usuarioService.getClientes();
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/clientes/activos")
    public ResponseEntity<List<Usuario>> getClientesActivos() {
        List<Usuario> clientes = usuarioService.getClientesActivos();
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/administradores")
    public ResponseEntity<List<Usuario>> getAdministradores() {
        List<Usuario> admins = usuarioService.getAdministradores();
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarPorNombre(@RequestParam String nombre) {
        List<Usuario> usuarios = usuarioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/verificar-email")
    public ResponseEntity<Map<String, Boolean>> verificarEmail(@RequestParam String email) {
        boolean existe = usuarioService.existeEmail(email);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @GetMapping("/salute")
    public ResponseEntity<String> salute() {
        return ResponseEntity.ok("¡Hola!");
    }
}