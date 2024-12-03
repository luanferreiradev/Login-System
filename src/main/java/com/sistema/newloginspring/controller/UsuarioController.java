package com.sistema.newloginspring.controller;

import com.sistema.newloginspring.model.Usuario;
import com.sistema.newloginspring.repository.UsuarioRepository;
import com.sistema.newloginspring.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    // Endpoint para criar uma nova conta
    @PostMapping("/criar")
    public ResponseEntity<String> criarConta(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        usuario.setSenha(crypt(usuario.getSenha()));
        usuarioRepository.save(usuario);
        emailService.sendEmail(usuario.getEmail(), "Conta Criada", "Conta criada em Login System");
        return ResponseEntity.ok("Conta criada com sucesso.");
    }

    // Endpoint para login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getAtivo() && verificarSenha(senha, usuario.getSenha())) {
            return ResponseEntity.ok("Login bem-sucedido.");
        }
        return ResponseEntity.badRequest().body("E-mail ou senha incorretos.");
    }

    // Endpoint para recuperação de senha
    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getAtivo()) {
            String resetLink = "http://localhost:8080/reset-password?email=" + email; // Link de redefinição de senha
            String message = "Clique no link para redefinir sua senha: " + resetLink;
            emailService.sendEmail(email, "Recuperação de Senha", message);
            return ResponseEntity.ok("E-mail de recuperação enviado para " + email);
        }
        return ResponseEntity.badRequest().body("Usuário não encontrado.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getAtivo()) {
            usuario.setSenha(crypt(novaSenha));
            usuarioRepository.save(usuario);
            emailService.sendEmail(email, "Senha Redefinida", "Conta alterada com sucesso em Login System");
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        }
        return ResponseEntity.badRequest().body("Usuário não encontrado.");
    }

    @PostMapping("/enviar-email-suporte")
    public ResponseEntity<String> enviarEmailSuporte(@RequestParam String email, @RequestParam String assunto, @RequestParam String mensagem) {
        String suporteEmail = "bankmalvader@gmail.com";
        String fullMessage = "Email de: " + email + "\n\n" + mensagem;
        emailService.sendEmail(suporteEmail, assunto, fullMessage);
        return ResponseEntity.ok("E-mail enviado para o suporte com sucesso.");
    }

    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // Método para criptografar a senha
    private String crypt(String senha) {
        return passwordEncoder.encode(senha);
    }

    // Método para verificar a senha
    private boolean verificarSenha(String senha, String senhaCriptografada) {
        return passwordEncoder.matches(senha, senhaCriptografada);
    }
}