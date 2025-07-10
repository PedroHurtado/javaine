package com.example;

import com.example.common.NotFondException;

public class MiResource implements AutoCloseable {

    public void usar() {
        System.out.println("Usando el recurso...");
        throw new NotFondException("No existe");
    }

    @Override
    public void close() throws Exception {
        System.out.println("Cerrando el recurso...");
    }

    public static void run() {
        try (MiResource recurso = new MiResource()) {
            recurso.usar();
        } 
        catch(NotFondException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){

        }
        System.out.println("fin");
    }
}
