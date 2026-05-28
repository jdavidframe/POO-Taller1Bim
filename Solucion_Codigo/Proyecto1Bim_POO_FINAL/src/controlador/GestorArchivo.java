/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import modelo.RegistroAlumnos;

/**
 *
 * @author jdavi
 */

public class GestorArchivo {

    private static final String ARCHIVO
            = "datos_sistema.dat";

    private GestorArchivo() {
    }

    public static void guardarDatos(
            ArrayList<RegistroAlumnos> registros) {

        try (ObjectOutputStream salida
                = new ObjectOutputStream(
                        new FileOutputStream(ARCHIVO))) {

            salida.writeObject(registros);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al guardar datos: "
                    + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<RegistroAlumnos> cargarDatos() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada
                = new ObjectInputStream(
                        new FileInputStream(archivo))) {

            Object objeto = entrada.readObject();

            if (objeto instanceof ArrayList<?>) {
                return (ArrayList<RegistroAlumnos>) objeto;
            }

        } catch (Exception e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    public static boolean existeArchivo() {
        return new File(ARCHIVO).exists();
    }

    public static void eliminarArchivo() {

        File archivo = new File(ARCHIVO);

        if (archivo.exists()) {
            archivo.delete();
        }
    }

    public static int cantidadRegistros() {
        return cargarDatos().size();
    }
}
