package com.tijetravel.backend.util;

import java.text.Normalizer;

public class NormalizadorTexto {

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        // Quitar espacios extra y pasar a minusculas
        String limpio = texto.trim().toLowerCase();
        // Separar caracteres diacriticos (acentos)
        String normalizado = Normalizer.normalize(limpio, Normalizer.Form.NFD);
        // Quitar los acentos
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < normalizado.length(); i++) {
            char c = normalizado.charAt(i);
            if (Character.getType(c) != Character.NON_SPACING_MARK) {
                builder.append(c);
            }
        }
        // Reemplazar multiples espacios por uno solo
        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    public static boolean sonIguales(String texto1, String texto2) {
        String n1 = normalizar(texto1);
        String n2 = normalizar(texto2);
        return n1.equals(n2);
    }
}

