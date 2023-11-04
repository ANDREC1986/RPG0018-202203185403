/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Produto;

/**
 *
 * @author andre
 */
public class CadastroClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Socket socket = new Socket("localhost", 4321);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String login = "op1";
            String senha = "op1";
            System.out.println("Usuario conectado com Sucesso");
            saida.writeObject(login);
            saida.writeObject(senha);
            saida.writeObject("L");
            List<Produto> produtos = (List<Produto>) entrada.readObject();
            produtos.forEach((e) -> System.out.println(e.getNome()));
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
}
