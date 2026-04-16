package main.sistema_cafeteria_autoservicio.Models;

import java.util.Date;

public class Usuario {
    public int id;
    public String nombre;
    public String email;
    public String password;
    public Date fecha_registro;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getEmail() {
        return email;
    }
    public Date getFecha_registro() {
        return fecha_registro;
    }
}
