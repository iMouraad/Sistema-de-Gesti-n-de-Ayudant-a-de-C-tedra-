package com.uteq.sgaac.controller;

import com.uteq.sgaac.dto.EstudianteDashboardDTO;
import com.uteq.sgaac.model.*;
import com.uteq.sgaac.repository.*;
import com.uteq.sgaac.services.AuthService;
import com.uteq.sgaac.services.PostulacionService;
import com.uteq.sgaac.services.PlazaService;
import com.uteq.sgaac.services.PruebaOposicionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired
    private PlazaService plazaService;

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PruebaOposicionService pruebaOposicionService;

    @GetMapping("/dashboard-data")
    @ResponseBody
    public ResponseEntity<EstudianteDashboardDTO> getDashboardData(Principal principal) {
        usuario_sistema usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        Estudiante estudiante = estudianteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        long activeApplications = postulacionRepository.countByEstudianteAndEstadoPostulacionIn(
            estudiante, 
            Arrays.asList(PostulacionEstado.EN_REVISION, PostulacionEstado.APROBADO)
        );

        long scheduledTests = pruebaOposicionService.findByEstudiante(estudiante).size();

        EstudianteDashboardDTO dashboardData = new EstudianteDashboardDTO(
            usuario.getNombres(),
            activeApplications,
            scheduledTests,
            estudiante.getPromedioGeneral()
        );

        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/mis-pruebas")
    public String getMisPruebas(Model model, Principal principal,
                                      @RequestParam(name = "fragment", required = false) String fragment,
                                      @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {

        usuario_sistema usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        Estudiante estudiante = estudianteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        List<PruebaOposicion> pruebas = pruebaOposicionService.findByEstudiante(estudiante);
        model.addAttribute("pruebas", pruebas);

        boolean isFragmentRequest = "true".equalsIgnoreCase(fragment) || "true".equalsIgnoreCase(isPartialHeader);
        if (isFragmentRequest) {
            return "siderbarEstudiante/mis-pruebas :: content";
        } else {
            return "redirect:/homeEstudiante?view=mis-pruebas";
        }
    }

    @GetMapping("/postulaciones")
    public String getPlazasDisponibles(Model model,
                                       @RequestParam(name = "fragment", required = false) String fragment,
                                       @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        usuario_sistema usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        model.addAttribute("plazas", plazaService.findPlazasParaEstudiante(usuario));

        boolean isFragmentRequest = "true".equalsIgnoreCase(fragment) || "true".equalsIgnoreCase(isPartialHeader);
        if (isFragmentRequest) {
            return "siderbarEstudiante/postular-ayudantia :: content";
        }

        return "redirect:/homeEstudiante?view=postulaciones";
    }

    @GetMapping("/perfil")
    public String getPerfil(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<usuario_sistema> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalStateException("Usuario no encontrado");
        }

        usuario_sistema usuario = usuarioOpt.get();
        Estudiante estudiante = estudianteRepository.findByUsuario(usuario).orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiante", estudiante);

        return "siderbarEstudiante/perfil";
    }

    @PostMapping("/perfil/upload-photo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadPhoto(@RequestParam("photo") MultipartFile file,
                                                         Authentication authentication) {
        try {
            String username = authentication.getName();
            String newPhotoUrl = authService.updateUserPhoto(username, file);
            return ResponseEntity.ok(Map.of("fotoUrl", newPhotoUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        usuario_sistema usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, usuario.getPasswordHash())) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/homeEstudiante?view=perfil";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
            return "redirect:/homeEstudiante?view=perfil";
        }

        authService.changePassword(username, newPassword);

        redirectAttributes.addFlashAttribute("success", "¡Contraseña actualizada exitosamente!");
        return "redirect:/homeEstudiante?view=perfil";
    }

    @Autowired
    private PostulacionService postulacionService;

    @PostMapping("/postulacion/crear")
    public String crearPostulacion(@RequestParam("plazaId") Long plazaId,
                                   @RequestParam("fileSolicitud") MultipartFile fileSolicitud,
                                   @RequestParam("fileCedula") MultipartFile fileCedula,
                                   @RequestParam("fileMatricula") MultipartFile fileMatricula,
                                   @RequestParam("fileHistorial") MultipartFile fileHistorial,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            postulacionService.crearPostulacion(plazaId, username, fileSolicitud, fileCedula, fileMatricula, fileHistorial);
            redirectAttributes.addFlashAttribute("message",
                    "¡Postulación enviada exitosamente! Tus documentos han sido subidos.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la postulación: " + e.getMessage());
        }

        return "redirect:/homeEstudiante?view=postulaciones";
    }

    @GetMapping("/mis-postulaciones")
    public String getMisPostulaciones(Model model, Principal principal,
                                      @RequestParam(name = "fragment", required = false) String fragment,
                                      @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {

        usuario_sistema usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        Estudiante estudiante = estudianteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        List<Postulacion> postulaciones = postulacionRepository.findByEstudiante(estudiante);
        model.addAttribute("postulaciones", postulaciones);

        boolean isFragmentRequest = "true".equalsIgnoreCase(fragment) || "true".equalsIgnoreCase(isPartialHeader);
        if (isFragmentRequest) {
            return "siderbarEstudiante/mis-postulaciones :: content";
        } else {
            return "redirect:/homeEstudiante?view=mis-postulaciones";
        }
    }
}