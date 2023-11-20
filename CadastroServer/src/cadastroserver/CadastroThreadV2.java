/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Produto;
import model.Usuario;

/**
 *
 * @author andre
 */
public class CadastroThreadV2 extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    private boolean state;

    public CadastroThreadV2(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

@Override
public void run() {
        try {
            ObjectOutputStream saida = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(s1.getInputStream());
            String login = (String) entrada.readObject();
            String senha = (String) entrada.readObject();
            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                saida.writeObject(null);
                s1.close();
                return;
            } else {
                saida.writeObject(data());
                saida.writeObject("Usuario conectado com sucesso");
            }
            
            while(true) {
                String comando = (String) entrada.readObject();
                if("l".equals(comando.toLowerCase())) {
                    saida.writeObject(data());
                    saida.writeObject(ctrl.findProdutoEntities());
                } else if("e".equals(comando) || "E".equals(comando)) {
                    Movimento mov = new Movimento();
                    mov.setIdUsuario(usuario);
                    mov.setTipo('E');
                    int pessoaId = (int) entrada.readObject();
                    mov.setIdPessoa(this.ctrlPessoa.findPessoa(pessoaId));
                    int produtoId = (int) entrada.readObject();
                    mov.setIdProduto(this.ctrl.findProduto(produtoId));
                    int prodQnt = (int) entrada.readObject();
                    mov.setQuantidade(prodQnt);
                    BigDecimal prodPrc = (BigDecimal) entrada.readObject();
                    mov.setValorUnitario(prodPrc);
                    ctrlMov.create(mov);
                    Produto prod = ctrl.findProduto(produtoId);
                    prod.setQuantidade(prod.getQuantidade()+prodQnt);
                    ctrl.edit(prod);
                    saida.writeObject(data());
                    saida.writeObject("Entrada efetuada com sucesso");
                } else if("s".equals(comando) || "s".equals(comando)) {
                    Movimento mov = new Movimento();
                    mov.setIdUsuario(usuario);
                    mov.setTipo('S');
                    int pessoaId = (int) entrada.readObject();
                    mov.setIdPessoa(this.ctrlPessoa.findPessoa(pessoaId));
                    int produtoId = (int) entrada.readObject();
                    mov.setIdProduto(this.ctrl.findProduto(produtoId));
                    int prodQnt = (int) entrada.readObject();
                    mov.setQuantidade(prodQnt);
                    BigDecimal prodPrc = (BigDecimal) entrada.readObject();
                    mov.setValorUnitario(prodPrc);
                    ctrlMov.create(mov);
                    Produto prod = ctrl.findProduto(produtoId);
                    prod.setQuantidade(prod.getQuantidade() - prodQnt);
                    ctrl.edit(prod);
                    saida.writeObject(data());
                    saida.writeObject("Saida efetuada com sucesso");
                    
                }
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        }
}

private String data(){
    LocalDateTime now = LocalDateTime.now();
    DayOfWeek diaDaSemana = now.getDayOfWeek();
    Month mes = now.getMonth();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd HH:mm:ss");
    String time = now.format(formatter);
    String data = ">> Nova comunicação em " + diaDaSemana.name() + " " + mes.name() + " " + time + " BRT " + now.getYear();  
    return data;
}
    
}
