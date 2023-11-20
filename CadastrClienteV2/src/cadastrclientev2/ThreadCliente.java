/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastrclientev2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.Produto;

/**
 *
 * @author andre
 */
public class ThreadCliente extends Thread {
    private ObjectInputStream entrada;
    private JTextArea textArea;
    
    public ThreadCliente(ObjectInputStream entrada, JTextArea textArea) {
    this.entrada = entrada;
    this.textArea = textArea;
    }
    
    @Override
    public void run() {
        while(true) {
        try {
            Object objeto = entrada.readObject();
            if (objeto instanceof String) {
            SwingUtilities.invokeLater(() -> textArea.append((String) objeto + "\n"));
            } else if (objeto instanceof List) {
                List<Produto> lista = (List<Produto>) objeto;
                lista.forEach((e) -> {
                    SwingUtilities.invokeLater(() -> textArea.append(e.getNome() + " - " + e.getQuantidade() + "\n"));
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        
        }
        }
    }
}
