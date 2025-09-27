package com.uteq.sgaac.controller;

import com.uteq.sgaac.dto.AgendarPruebaDTO;
import com.uteq.sgaac.dto.CrearPlazasForm;
import com.uteq.sgaac.model.*;
import com.uteq.sgaac.repository.*;
import com.uteq.sgaac.services.ConvocatoriaService;
import com.uteq.sgaac.services.PostulacionService;
import com.uteq.sgaac.services.PruebaOposicionEvalService;
import com.uteq.sgaac.services.PruebaOposicionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    private static final Logger logger = LoggerFactory.getLogger(CoordinadorController.class);

    private final ConvocatoriaRepository convocatoriaRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final ConvocatoriaService convocatoriaService;
    private final PostulacionRepository postulacionRepository;
    private final PruebaOposicionRepository pruebaOposicionRepository;
    private final DocenteRepository docenteRepository;
    private final PruebaOposicionEvalService pruebaOposicionEvalService;
    private final UsuarioRepository usuarioRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final PostulacionService postulacionService;
    private final PruebaOposicionService pruebaOposicionService;


    public CoordinadorController(ConvocatoriaRepository convocatoriaRepository, AsignaturaRepository asignaturaRepository, ConvocatoriaService convocatoriaService, PostulacionRepository postulacionRepository, PruebaOposicionRepository pruebaOposicionRepository, DocenteRepository docenteRepository, PruebaOposicionEvalService pruebaOposicionEvalService, UsuarioRepository usuarioRepository, CoordinadorRepository coordinadorRepository, PostulacionService postulacionService, PruebaOposicionService pruebaOposicionService) {
        this.convocatoriaRepository = convocatoriaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.convocatoriaService = convocatoriaService;
        this.postulacionRepository = postulacionRepository;
        this.pruebaOposicionRepository = pruebaOposicionRepository;
        this.docenteRepository = docenteRepository;
        this.pruebaOposicionEvalService = pruebaOposicionEvalService;
        this.usuarioRepository = usuarioRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.postulacionService = postulacionService;
        this.pruebaOposicionService = pruebaOposicionService;
    }

    @GetMapping
    public String homeCoordinador() {
        return "homeCoordinador";
    }

    private boolean isFragmentRequest(String fragment, String isPartialHeader) {
        return "true".equalsIgnoreCase(fragment) || "true".equalsIgnoreCase(isPartialHeader);
    }

    @GetMapping("/evaluar-pruebas")
    public String evaluarPruebas(Model model, @RequestParam(name = "fragment", required = false) String fragment,
                               @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        List<PruebaOposicion> pruebasPendientes = pruebaOposicionRepository.findByEstadoWithDetails("AGENDADA");
        model.addAttribute("pruebas", pruebasPendientes);

        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/evaluar-pruebas :: content";
        }
        return "siderbarCoordinador/evaluar-pruebas";
    }

    @GetMapping("/oposicion/{id}/evaluar-modal")
    public String getEvaluarOposicionModal(@PathVariable Long id, Model model) {
        PruebaOposicion prueba = pruebaOposicionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prueba de oposición no encontrada con ID: " + id));
        
        Carrera carrera = prueba.getPostulacion().getAsignatura().getCarrera();
        List<Docente> docentesPosibles = docenteRepository.findByCarrera(carrera);

        model.addAttribute("prueba", prueba);
        model.addAttribute("docentesPosibles", docentesPosibles);

        return "siderbarCoordinador/evaluar-oposicion-modal :: content";
    }

    @PostMapping("/oposicion/{id}/evaluar")
    public String procesarEvaluacionOposicion(@PathVariable Long id, PruebaOposicionEvalWrapper wrapper, RedirectAttributes redirectAttributes) {
        logger.info("Procesando evaluación para la prueba ID: {}. Se recibieron {} evaluaciones del formulario.", id, wrapper.getEvaluaciones() != null ? wrapper.getEvaluaciones().size() : 0);
        try {
            pruebaOposicionEvalService.guardarYCalcularResultados(id, wrapper.getEvaluaciones());
            redirectAttributes.addFlashAttribute("successMessage", "Evaluación guardada y finalizada con éxito.");
        } catch (Exception e) {
            logger.error("Error al procesar la evaluación de la oposición con ID: " + id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la evaluación.");
        }
        return "redirect:/coordinador?view=evaluar-pruebas";
    }

    @GetMapping("/convocatorias")
    public String gestionarConvocatorias(@RequestParam(name = "fragment", required = false) String fragment,
                                       @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/convocatorias :: content";
        }
        return "siderbarCoordinador/convocatorias";
    }

    @GetMapping("/publicar")
    public String publicarConvocatorias(Model model, @RequestParam(name = "fragment", required = false) String fragment,
                                       @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        model.addAttribute("convocatorias", convocatoriaService.findConvocatoriasListasParaPublicar());
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/publicar-convocatorias :: content";
        }
        return "siderbarCoordinador/publicar-convocatorias";
    }

    @PostMapping("/publicar/{id}")
    public String publicarConvocatoria(@PathVariable Long id,
                                       @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                       @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                       RedirectAttributes redirectAttributes) {
        try {
            convocatoriaService.publicarConvocatoria(id, fechaInicio, fechaFin);
            redirectAttributes.addFlashAttribute("successMessage", "Convocatoria publicada con éxito.");
        } catch (Exception e) {
            logger.error("Error al publicar la convocatoria", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al publicar la convocatoria.");
        }
        return "redirect:/coordinador/publicar";
    }


    @GetMapping("/postulaciones")
    public String gestionarPostulaciones(@RequestParam(name = "fragment", required = false) String fragment,
                                         @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/postulaciones :: content";
        }
        return "siderbarCoordinador/postulaciones";
    }

    @GetMapping("/postulaciones/crear")
    public String crearPostulacionForm(Model model, @RequestParam(name = "fragment", required = false) String fragment,
                                       @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {

        List<Convocatoria> convocatoriasActivas = convocatoriaRepository.findByEstado(true);
        model.addAttribute("convocatorias", convocatoriasActivas);

        List<Integer> semestres = asignaturaRepository.findDistinctSemestres();
        model.addAttribute("semestres", semestres);

        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/crearPostulacion :: content";
        }
        return "siderbarCoordinador/crearPostulacion";
    }

    @PostMapping("/postulaciones/crear")
    public String crearPostulacion(@ModelAttribute CrearPlazasForm form, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuario_sistema usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            Coordinador coordinador = coordinadorRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Coordinador no encontrado para el usuario"));

            convocatoriaService.crearPlazas(form, coordinador);
            redirectAttributes.addFlashAttribute("successMessage", "Plazas creadas con éxito.");
        } catch (Exception e) {
            logger.error("Error al crear las plazas", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear las plazas: " + e.getMessage());
        }
        return "redirect:/coordinador?view=postulaciones";
    }

    @GetMapping("/postulaciones/revisar")
    public String revisarPostulaciones(Model model, @RequestParam(name = "fragment", required = false) String fragment,
                                     @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        
        List<Postulacion> postulaciones = postulacionService.findAll();
        model.addAttribute("postulaciones", postulaciones);

        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/revisar-postulaciones :: content";
        }
        return "siderbarCoordinador/revisar-postulaciones";
    }

    @GetMapping("/ayudantias")
    public String gestionarAyudantias(@RequestParam(name = "fragment", required = false) String fragment,
                                      @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/ayudantias :: content";
        }
        return "siderbarCoordinador/ayudantias";
    }

    @GetMapping("/reportes")
    public String generarReportes(@RequestParam(name = "fragment", required = false) String fragment,
                                  @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/reportes :: content";
        }
        return "siderbarCoordinador/reportes";
    }

    @GetMapping("/configuracion")
    public String mostrarConfiguracion(@RequestParam(name = "fragment", required = false) String fragment,
                                       @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {
        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/configuracion :: content";
        }
        return "siderbarCoordinador/configuracion";
    }

    @GetMapping("/pruebas-oposicion")
    public String gestionarPruebasOposicion(Model model,
                                          @RequestParam(name = "fragment", required = false) String fragment,
                                          @RequestHeader(name = "X-Partial", required = false) String isPartialHeader) {

        List<Postulacion> postulacionesAprobadas = postulacionRepository.findByEstadoPostulacionWithDetails(PostulacionEstado.APROBADO);
        model.addAttribute("postulaciones", postulacionesAprobadas);

        List<Docente> allDocentes = docenteRepository.findAll();
        model.addAttribute("docentes", allDocentes);

        if (isFragmentRequest(fragment, isPartialHeader)) {
            return "siderbarCoordinador/pruebas-oposicion :: content";
        }
        return "siderbarCoordinador/pruebas-oposicion";
    }

    @PostMapping("/agendar-prueba")
    public String agendarPrueba(@ModelAttribute AgendarPruebaDTO dto, RedirectAttributes redirectAttributes) {
        try {
            pruebaOposicionService.agendarPrueba(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Prueba de oposición agendada y notificada con éxito.");
        } catch (Exception e) {
            logger.error("Error al agendar prueba de oposición", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agendar prueba de oposición: " + e.getMessage());
        }
        return "redirect:/coordinador?view=pruebas-oposicion";
    }

    @PostMapping("/postulaciones/{id}/aprobar")
    @ResponseBody
    public ResponseEntity<?> aprobarPostulacion(@PathVariable Long id) {
        try {
            postulacionService.aprobarPostulacion(id);
            return ResponseEntity.ok(Map.of("message", "Postulación aprobada con éxito."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/postulaciones/{id}/rechazar")
    @ResponseBody
    public ResponseEntity<?> rechazarPostulacion(@PathVariable Long id) {
        try {
            postulacionService.rechazarPostulacion(id);
            return ResponseEntity.ok(Map.of("message", "Postulación rechazada con éxito."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/postulaciones/documento/validar")
    @ResponseBody
    public ResponseEntity<?> validarDocumentoPostulacion(@RequestParam Long postulacionId, @RequestParam String tipoDocumento, @RequestParam boolean esValido) {
        try {
            postulacionService.actualizarEstadoDocumento(postulacionId, tipoDocumento, esValido);
            return ResponseEntity.ok(Map.of("message", "Estado del documento actualizado con éxito."));
        } catch (Exception e) {
            logger.error("Error al actualizar estado del documento para postulación ID: " + postulacionId, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
