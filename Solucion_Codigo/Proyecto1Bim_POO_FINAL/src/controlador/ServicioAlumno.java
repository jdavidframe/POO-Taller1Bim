package controlador;

import java.util.ArrayList;
import modelo.Alumno;
import modelo.Beca;
import modelo.Matricula;
import modelo.Pension;
import modelo.RegistroAlumnos;

/**
 * Servicio principal que conecta la vista con el modelo.
 * Centraliza toda la lógica de negocio del sistema.
 *
 * @author jdavi
 */
public class ServicioAlumno {

    private ArrayList<RegistroAlumnos> registros;

    public ServicioAlumno() {
        registros = GestorArchivo.cargarDatos();
        if (registros == null) {
            registros = new ArrayList<>();
        }
    }

    // =========================================================
    // REGISTRO
    // =========================================================

    /**
     * Registra un nuevo alumno con su matrícula inicial.
     * Retorna false si ya existe un alumno con esa cédula.
     */
    public boolean registrarAlumno(Alumno alumno, Matricula matricula) {
        for (RegistroAlumnos r : registros) {
            if (r.getAlumno().getCedula().equals(alumno.getCedula())) {
                return false;
            }
        }

        // Si el alumno aplica a beca de mérito, se la asignamos automáticamente
        Beca becaMerito = alumno.generarBecaMeritoSiAplica();
        if (becaMerito != null) {
            matricula.agregarBeca(becaMerito);
        }

        RegistroAlumnos nuevo = new RegistroAlumnos(alumno);
        nuevo.agregarMatricula(matricula);
        registros.add(nuevo);

        GestorArchivo.guardarDatos(registros);
        return true;
    }

    // =========================================================
    // BÚSQUEDA
    // =========================================================

    /**
     * Busca y retorna el RegistroAlumnos por cédula.
     * Retorna null si no existe.
     */
    public RegistroAlumnos buscarRegistro(String cedula) {
        for (RegistroAlumnos r : registros) {
            if (r.getAlumno().getCedula().equals(cedula.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Busca y retorna solo el Alumno por cédula.
     * Retorna null si no existe.
     */
    public Alumno buscarAlumno(String cedula) {
        RegistroAlumnos reg = buscarRegistro(cedula);
        return (reg != null) ? reg.getAlumno() : null;
    }

    /**
     * Retorna la lista completa de registros (solo lectura conceptual).
     */
    public ArrayList<RegistroAlumnos> getRegistros() {
        return registros;
    }

    // =========================================================
    // BECAS
    // =========================================================

    /**
     * Agrega una beca a la última matrícula activa del alumno.
     * Retorna false si el alumno no existe, no tiene matrículas,
     * o si el total de descuentos supera el 100%.
     */
    public boolean agregarBecaAlumno(String cedula, Beca beca) {
        RegistroAlumnos registro = buscarRegistro(cedula);
        if (registro == null || registro.getMatriculas().isEmpty()) {
            return false;
        }

        Matricula ultima = registro.getMatriculas()
                .get(registro.getMatriculas().size() - 1);

        boolean agregado = ultima.agregarBeca(beca);
        if (agregado) {
            GestorArchivo.guardarDatos(registros);
        }
        return agregado;
    }

    // =========================================================
    // PAGOS
    // =========================================================

    /**
     * Marca como PAGADA una pensión específica del alumno.
     * @param cedula      Cédula del alumno
     * @param indicePension  Índice (0-9) de la pensión dentro de la matrícula
     * @param indiceMatricula  Índice de la matrícula (la última si es -1)
     * @return true si se pudo marcar como pagada
     */
    public boolean pagarPension(String cedula, int indiceMatricula, int indicePension) {
        RegistroAlumnos registro = buscarRegistro(cedula);
        if (registro == null || registro.getMatriculas().isEmpty()) {
            return false;
        }

        int idxMat = (indiceMatricula == -1)
                ? registro.getMatriculas().size() - 1
                : indiceMatricula;

        if (idxMat < 0 || idxMat >= registro.getMatriculas().size()) {
            return false;
        }

        Matricula matricula = registro.getMatriculas().get(idxMat);
        Pension[] pensiones = matricula.getPensiones();

        if (indicePension < 0 || indicePension >= pensiones.length) {
            return false;
        }

        Pension pension = pensiones[indicePension];
        if ("PAGADA".equalsIgnoreCase(pension.getEstado())) {
            return false; // ya estaba pagada
        }

        pension.marcarPagada();
        GestorArchivo.guardarDatos(registros);
        return true;
    }

    // =========================================================
    // CARTERA / MOROSIDAD
    // =========================================================

    /**
     * Calcula el total de cartera vencida de un alumno.
     */
    public double calcularCarteraVencida(String cedula) {
        RegistroAlumnos registro = buscarRegistro(cedula);
        if (registro == null) {
            return 0;
        }
        Cartera cartera = new Cartera(registro);
        return cartera.calcularCarteraVencida(registro.getAlumno());
    }

    /**
     * Retorna true si el alumno tiene más de 2 pensiones vencidas.
     */
    public boolean tieneAlertaMorosidad(String cedula) {
        RegistroAlumnos registro = buscarRegistro(cedula);
        if (registro == null) {
            return false;
        }
        Cartera cartera = new Cartera(registro);
        return cartera.emitirAlertaMorosidad(registro.getAlumno());
    }
}
