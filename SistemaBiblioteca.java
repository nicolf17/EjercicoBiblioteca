import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SistemaBiblioteca {

    // ====== CLASE LIBRO ======
    static class Libro {
        private String titulo;
        private String autor;
        private String codigo;
        private boolean disponible;

        public Libro(String titulo, String autor, String codigo) {
            this.titulo = titulo;
            this.autor = autor;
            this.codigo = codigo;
            this.disponible = true;
        }

        public String getCodigo() {
            return codigo;
        }

        public boolean isDisponible() {
            return disponible;
        }

        public void marcarPrestado() {
            disponible = false;
        }

        public void marcarDisponible() {
            disponible = true;
        }

        public void mostrarDatos() {
            System.out.println("Código: " + codigo + " | Título: " + titulo + 
                                " | Autor: " + autor + " | Disponible: " + (disponible ? "Sí" : "No"));
        }
    }

    // ====== CLASE USUARIO ======
    static class Usuario {
        private String nombre;
        private String idUsuario;
        private ArrayList<Libro> librosPrestados;

        public Usuario(String nombre, String idUsuario) {
            this.nombre = nombre;
            this.idUsuario = idUsuario;
            this.librosPrestados = new ArrayList<>();
        }

        public String getIdUsuario() {
            return idUsuario;
        }

        public ArrayList<Libro> getLibrosPrestados() {
            return librosPrestados;
        }

        public void mostrarDatos() {
            System.out.println("Usuario: " + nombre + " | ID: " + idUsuario + 
                                " | Libros Prestados: " + librosPrestados.size());
        }

        public boolean agregarPrestamo(Libro libro) {
            if (librosPrestados.size() < 3) {
                librosPrestados.add(libro);
                return true;
            } else {
                System.out.println("El usuario ya tiene 3 libros prestados.");
                return false;
            }
        }

        public boolean devolverLibro(Libro libro) {
            return librosPrestados.remove(libro);
        }
    }

    // ====== CLASE PRÉSTAMO ======
    static class Prestamo {
        Usuario usuario;
        Libro libro;
        LocalDate fechaInicio;
        LocalDate fechaLimite;

        Prestamo(Usuario usuario, Libro libro, LocalDate fechaInicio, LocalDate fechaLimite) {
            this.usuario = usuario;
            this.libro = libro;
            this.fechaInicio = fechaInicio;
            this.fechaLimite = fechaLimite;
        }
    }

    // ====== CLASE BIBLIOTECA ======
    static class Biblioteca {
        private ArrayList<Libro> libros;
        private ArrayList<Usuario> usuarios;
        private ArrayList<Prestamo> historial;

        public Biblioteca() {
            libros = new ArrayList<>();
            usuarios = new ArrayList<>();
            historial = new ArrayList<>();
        }

        // Registrar libro
        public void registrarLibro(String titulo, String autor, String codigo) {
            libros.add(new Libro(titulo, autor, codigo));
        }

        // Registrar usuario
        public void registrarUsuario(String nombre, String idUsuario) {
            usuarios.add(new Usuario(nombre, idUsuario));
        }

        // Mostrar libros disponibles
        public void mostrarLibrosDisponibles() {
            for (Libro libro : libros) {
                if (libro.isDisponible()) {
                    libro.mostrarDatos();
                }
            }
        }

        // Mostrar usuarios
        public void mostrarUsuarios() {
            for (Usuario usuario : usuarios) {
                usuario.mostrarDatos();
            }
        }

        // Prestar libro
        public void prestarLibro(String idUsuario, String codigoLibro) {
            Usuario usuario = buscarUsuario(idUsuario);
            Libro libro = buscarLibro(codigoLibro);

            if (usuario != null && libro != null && libro.isDisponible()) {
                if (usuario.agregarPrestamo(libro)) {
                    libro.marcarPrestado();
                    LocalDate hoy = LocalDate.now();
                    LocalDate limite = hoy.plusDays(7); // plazo de 7 días
                    historial.add(new Prestamo(usuario, libro, hoy, limite));
                    System.out.println("Préstamo realizado. Fecha límite: " + limite);
                }
            } else {
                System.out.println("No se puede realizar el préstamo.");
            }
        }

        // Devolver libro
        public void devolverLibro(String idUsuario, String codigoLibro) {
            Usuario usuario = buscarUsuario(idUsuario);
            Libro libro = buscarLibro(codigoLibro);

            if (usuario != null && libro != null && usuario.devolverLibro(libro)) {
                libro.marcarDisponible();

                Prestamo prestamo = buscarPrestamo(usuario, libro);
                if (prestamo != null) {
                    LocalDate hoy = LocalDate.now();
                    long diasRetraso = ChronoUnit.DAYS.between(prestamo.fechaLimite, hoy);

                    if (diasRetraso > 0) {
                        long multa = diasRetraso * 500;
                        System.out.println("Libro devuelto con retraso de " + diasRetraso + 
                                            " días. Multa: $" + multa);
                    } else {
                        System.out.println("Libro devuelto a tiempo. No hay multa.");
                    }
                    historial.remove(prestamo);
                }
            } else {
                System.out.println("Error al devolver el libro.");
            }
        }

        // Mostrar historial
        public void mostrarHistorialPrestamos() {
            for (Prestamo p : historial) {
                System.out.println("Usuario: " + p.usuario.getIdUsuario() + " | Libro: " + p.libro.getCodigo() +
                                    " | Inicio: " + p.fechaInicio + " | Límite: " + p.fechaLimite);
            }
        }

        // Métodos auxiliares
        private Usuario buscarUsuario(String idUsuario) {
            for (Usuario u : usuarios) {
                if (u.getIdUsuario().equals(idUsuario)) return u;
            }
            return null;
        }

        private Libro buscarLibro(String codigo) {
            for (Libro l : libros) {
                if (l.getCodigo().equals(codigo)) return l;
            }
            return null;
        }

        private Prestamo buscarPrestamo(Usuario usuario, Libro libro) {
            for (Prestamo p : historial) {
                if (p.usuario == usuario && p.libro == libro) return p;
            }
            return null;
        }
    }

    // ====== CLASE MAIN ======
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- SISTEMA DE BIBLIOTECA ---");
            System.out.println("1. Registrar Libro");
            System.out.println("2. Registrar Usuario");
            System.out.println("3. Mostrar Libros Disponibles");
            System.out.println("4. Mostrar Usuarios");
            System.out.println("5. Prestar Libro");
            System.out.println("6. Devolver Libro");
            System.out.println("7. Mostrar Historial de Préstamos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Código: ");
                    String codigo = sc.nextLine();
                    biblioteca.registrarLibro(titulo, autor, codigo);
                    break;
                case 2:
                    System.out.print("Nombre usuario: ");
                    String nombre = sc.nextLine();
                    System.out.print("ID usuario: ");
                    String id = sc.nextLine();
                    biblioteca.registrarUsuario(nombre, id);
                    break;
                case 3:
                    biblioteca.mostrarLibrosDisponibles();
                    break;
                case 4:
                    biblioteca.mostrarUsuarios();
                    break;
                case 5:
                    System.out.print("ID usuario: ");
                    String idU = sc.nextLine();
                    System.out.print("Código libro: ");
                    String codL = sc.nextLine();
                    biblioteca.prestarLibro(idU, codL);
                    break;
                case 6:
                    System.out.print("ID usuario: ");
                    String idDev = sc.nextLine();
                    System.out.print("Código libro: ");
                    String codDev = sc.nextLine();
                    biblioteca.devolverLibro(idDev, codDev);
                    break;
                case 7:
                    biblioteca.mostrarHistorialPrestamos();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
