package com.uteq.sgaac.services;

import com.uteq.sgaac.model.*;
import com.uteq.sgaac.repository.ConvocatoriaAsignaturaRepository;
import com.uteq.sgaac.repository.EstudianteRepository;
import com.uteq.sgaac.repository.PostulacionRepository;
import com.uteq.sgaac.repository.PostulacionDocumentoRepository;
import com.uteq.sgaac.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PostulacionService {

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ConvocatoriaAsignaturaRepository convocatoriaAsignaturaRepository;

    @Autowired
    private ValidacionPostulacionService validacionPostulacionService;

    @Autowired
    private PostulacionDocumentoRepository postulacionDocumentoRepository;


    public Postulacion crearPostulacion(Long plazaId, String username, MultipartFile fileSolicitud, MultipartFile fileCedula, MultipartFile fileMatricula, MultipartFile fileHistorial) {

        usuario_sistema usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Estudiante estudiante = estudianteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        ConvocatoriaAsignatura plaza = convocatoriaAsignaturaRepository.findById(plazaId)
                .orElseThrow(() -> new IllegalArgumentException("Plaza no encontrada"));

        // Guardar archivos y obtener rutas
        String rutaSolicitud = storeFile(fileSolicitud, estudiante.getIdEstudiante(), "solicitud");
        String rutaCedula = storeFile(fileCedula, estudiante.getIdEstudiante(), "cedula");
        String rutaMatricula = storeFile(fileMatricula, estudiante.getIdEstudiante(), "matricula");
        String rutaHistorial = storeFile(fileHistorial, estudiante.getIdEstudiante(), "historial");

        Postulacion postulacion = new Postulacion();
        postulacion.setEstudiante(estudiante);
        postulacion.setAsignatura(plaza.getAsignatura());
        postulacion.setConvocatoria(plaza.getConvocatoria());
        postulacion.setEstadoPostulacion(PostulacionEstado.EN_REVISION);

        // Setear rutas de archivos
        postulacion.setRutaSolicitud(rutaSolicitud);
        postulacion.setRutaCedula(rutaCedula);
        postulacion.setRutaMatricula(rutaMatricula);
        postulacion.setRutaHistorial(rutaHistorial);

        Postulacion savedPostulacion = postulacionRepository.save(postulacion);

        // Ejecutar validación asincrónica si es necesario
        validacionPostulacionService.validarPostulacion(savedPostulacion);

        return savedPostulacion;
    }

    private String storeFile(MultipartFile file, Long estudianteId, String docType) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "";
        }
        String cleanedFilename = StringUtils.cleanPath(originalFilename);
        String filename = estudianteId + "_" + docType + "_" + System.currentTimeMillis() + "_" + cleanedFilename;
        fileStorageService.save(file, filename);
        return filename;
    }


    public Postulacion save(Postulacion postulacion) {
        return postulacionRepository.save(postulacion);
    }

    public List<Postulacion> findAll() {
        return postulacionRepository.findAll();
    }

    public Optional<Postulacion> findById(Long id) {
        return postulacionRepository.findById(id);
    }

    public void deleteById(Long id) {
        postulacionRepository.deleteById(id);
    }

    public Postulacion aprobarPostulacion(Long id) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada con ID: " + id));

        long documentosSubidos = 0;
        if (postulacion.getRutaSolicitud() != null) documentosSubidos++;
        if (postulacion.getRutaCedula() != null) documentosSubidos++;
        if (postulacion.getRutaMatricula() != null) documentosSubidos++;
        if (postulacion.getRutaHistorial() != null) documentosSubidos++;

        long documentosValidados = postulacion.getDocumentos().stream().filter(PostulacionDocumento::isEsValido).count();

        if (documentosSubidos > 0 && documentosSubidos > documentosValidados) {
            throw new IllegalStateException("No se puede aprobar la postulación. No todos los documentos han sido validados.");
        }

        postulacion.setEstadoPostulacion(PostulacionEstado.APROBADO);
        return postulacionRepository.save(postulacion);
    }

    public Postulacion rechazarPostulacion(Long id) {
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada con ID: " + id));
        postulacion.setEstadoPostulacion(PostulacionEstado.RECHAZADO);
        return postulacionRepository.save(postulacion);
    }

    @Transactional
    public void actualizarEstadoDocumento(Long postulacionId, String tipoDocumento, boolean esValido) {
        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada con ID: " + postulacionId));

        PostulacionDocumento documento = postulacionDocumentoRepository
                .findByPostulacionAndTipoDocumento(postulacion, tipoDocumento)
                .orElse(new PostulacionDocumento(postulacion, tipoDocumento, esValido));

        documento.setEsValido(esValido);
        postulacionDocumentoRepository.save(documento);
    }
}
