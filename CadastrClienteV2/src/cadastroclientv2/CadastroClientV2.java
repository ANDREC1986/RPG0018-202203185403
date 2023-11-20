/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclientv2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class CadastroClientV2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
            try {
            Socket socket = new Socket("localhost", 4321);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String login = "op1";
            String senha = "op1";
            SaidaFrame frame = new SaidaFrame();
            frame.setVisible(true);
            frame.setBounds(1000, 800, 500, 180);
            frame.setDefaultCloseOperation(SaidaFrame.DISPOSE_ON_CLOSE);
            ThreadCliente thread = new ThreadCliente(entrada,frame.texto);
            thread.start();
            saida.writeObject(login);
            saida.writeObject(senha);
            while(true) {
                System.out.print("L - Listar | X - Finalizar | E - Entrada | S - Saida \n");
                String option = reader.readLine();
                switch(option.toLowerCase()) {
                    case "e" -> {                    
                        saida.writeObject("e");
                        doMovement(reader,saida);
                    }                    
                    case "s" -> {
                        saida.writeObject("s");
                        doMovement(reader,saida);
                    }
                    
                    case "l" -> {
                        saida.writeObject("l");
                    }
                    case "x" -> {
                        frame.dispose();
                        System.exit(0);
                    }
                    default -> {
                        System.out.println("Opcao nao existe");}
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CadastroClientV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void doMovement(BufferedReader reader, ObjectOutputStream saida) throws IOException {
        System.out.println("Id da Pessoa:");
        saida.writeObject(Integer.valueOf(reader.readLine()));
        System.out.println("Id do Produto");
        saida.writeObject(Integer.valueOf(reader.readLine()));
        System.out.println("Quantidade:");
        saida.writeObject(Integer.valueOf(reader.readLine()));
        System.out.println("Valor Unitario:");
        saida.writeObject(BigDecimal.valueOf(Float.parseFloat(reader.readLine())));     
    }
}

